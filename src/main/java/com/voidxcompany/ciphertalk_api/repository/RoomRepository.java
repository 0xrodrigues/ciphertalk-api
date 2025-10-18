package com.voidxcompany.ciphertalk_api.repository;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Room> roomRowMapper = (rs, rowNum) -> Room.builder()
            .roomId(rs.getLong("room_id"))
            .address(rs.getString("address"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .hostUserId(rs.getLong("host_user_id"))
            .maxUsers(rs.getInt("max_users"))
            .visibility(Room.RoomVisibility.valueOf(rs.getString("visibility")))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();

    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> Tag.builder()
            .tagId(rs.getLong("tag_id"))
            .name(rs.getString("name"))
            .build();

    public Room create(Room room) {
        String sql = """
            INSERT INTO tb_room (address, name, description, host_user_id, max_users, visibility)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, room.getAddress());
            ps.setString(2, room.getName());
            ps.setString(3, room.getDescription());
            ps.setLong(4, room.getHostUserId());
            ps.setInt(5, room.getMaxUsers());
            ps.setString(6, room.getVisibility().name());
            return ps;
        }, keyHolder);

        Long roomId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        room.setRoomId(roomId);

        // Insert tags if present
        if (room.getTags() != null && !room.getTags().isEmpty()) {
            for (Tag tag : room.getTags()) {
                Long tagId = findOrCreateTag(tag.getName());
                linkRoomTag(roomId, tagId);
            }
        }

        return findById(roomId).orElse(room);
    }

    public Optional<Room> findById(Long id) {
        String sql = "SELECT * FROM tb_room WHERE room_id = ?";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, id);
        
        if (rooms.isEmpty()) {
            return Optional.empty();
        }
        
        Room room = rooms.getFirst();
        room.setTags(findTagsByRoomId(room.getRoomId()));
        return Optional.of(room);
    }

    public Optional<Room> findByAddress(String address) {
        String sql = "SELECT * FROM tb_room WHERE address = ?";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, address);
        
        if (rooms.isEmpty()) {
            return Optional.empty();
        }
        
        Room room = rooms.getFirst();
        room.setTags(findTagsByRoomId(room.getRoomId()));
        return Optional.of(room);
    }

    public List<Room> findPublicRooms() {
        String sql = "SELECT * FROM tb_room WHERE visibility = 'PUBLIC' ORDER BY created_at DESC";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper);
        
        for (Room room : rooms) {
            room.setTags(findTagsByRoomId(room.getRoomId()));
        }
        
        return rooms;
    }

    public List<Room> searchRooms(String query) {
        String sql = """
            SELECT DISTINCT r.* FROM tb_room r
            LEFT JOIN tb_room_tag rt ON r.room_id = rt.room_id
            LEFT JOIN tb_tag t ON rt.tag_id = t.tag_id
            WHERE r.visibility = 'PUBLIC'
            AND (
                LOWER(r.name) LIKE LOWER(?)
                OR LOWER(r.description) LIKE LOWER(?)
                OR LOWER(t.name) LIKE LOWER(?)
            )
            ORDER BY r.created_at DESC
            """;
        
        String searchPattern = "%" + query + "%";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, searchPattern, searchPattern, searchPattern);
        
        for (Room room : rooms) {
            room.setTags(findTagsByRoomId(room.getRoomId()));
        }
        
        return rooms;
    }

    private List<Tag> findTagsByRoomId(Long roomId) {
        String sql = """
            SELECT t.* FROM tb_tag t
            INNER JOIN tb_room_tag rt ON t.tag_id = rt.tag_id
            WHERE rt.room_id = ?
            """;
        return jdbcTemplate.query(sql, tagRowMapper, roomId);
    }

    private Long findOrCreateTag(String tagName) {
        String selectSql = "SELECT tag_id FROM tb_tag WHERE name = ?";
        List<Long> tagIds = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getLong("tag_id"), tagName);
        
        if (!tagIds.isEmpty()) {
            return tagIds.getFirst();
        }
        
        String insertSql = "INSERT INTO tb_tag (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tagName);
            return ps;
        }, keyHolder);
        
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void linkRoomTag(Long roomId, Long tagId) {
        String sql = "INSERT INTO tb_room_tag (room_id, tag_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, roomId, tagId);
    }
}
