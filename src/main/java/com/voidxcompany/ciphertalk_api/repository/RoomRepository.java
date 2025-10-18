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
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .address(rs.getString("address"))
            .isPublic(rs.getBoolean("is_public"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> Tag.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build();

    public Room create(Room room) {
        String sql = "INSERT INTO rooms (name, description, address, is_public, created_at) VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, room.getName());
            ps.setString(2, room.getDescription());
            ps.setString(3, room.getAddress());
            ps.setBoolean(4, room.isPublic());
            ps.setTimestamp(5, Timestamp.valueOf(room.getCreatedAt()));
            return ps;
        }, keyHolder);

        Long roomId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        room.setId(roomId);

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
        String sql = "SELECT * FROM rooms WHERE id = ?";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, id);
        
        if (rooms.isEmpty()) {
            return Optional.empty();
        }
        
        Room room = rooms.getFirst();
        room.setTags(findTagsByRoomId(room.getId()));
        return Optional.of(room);
    }

    public Optional<Room> findByAddress(String address) {
        String sql = "SELECT * FROM rooms WHERE address = ?";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper, address);
        
        if (rooms.isEmpty()) {
            return Optional.empty();
        }
        
        Room room = rooms.getFirst();
        room.setTags(findTagsByRoomId(room.getId()));
        return Optional.of(room);
    }

    public List<Room> findPublicRooms() {
        String sql = "SELECT * FROM rooms WHERE is_public = true ORDER BY created_at DESC";
        List<Room> rooms = jdbcTemplate.query(sql, roomRowMapper);
        
        for (Room room : rooms) {
            room.setTags(findTagsByRoomId(room.getId()));
        }
        
        return rooms;
    }

    public List<Room> searchRooms(String query) {
        String sql = """
            SELECT DISTINCT r.* FROM rooms r
            LEFT JOIN room_tags rt ON r.id = rt.room_id
            LEFT JOIN tags t ON rt.tag_id = t.id
            WHERE r.is_public = true
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
            room.setTags(findTagsByRoomId(room.getId()));
        }
        
        return rooms;
    }

    private List<Tag> findTagsByRoomId(Long roomId) {
        String sql = """
            SELECT t.* FROM tags t
            INNER JOIN room_tags rt ON t.id = rt.tag_id
            WHERE rt.room_id = ?
            """;
        return jdbcTemplate.query(sql, tagRowMapper, roomId);
    }

    private Long findOrCreateTag(String tagName) {
        String selectSql = "SELECT id FROM tags WHERE name = ?";
        List<Long> tagIds = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getLong("id"), tagName);
        
        if (!tagIds.isEmpty()) {
            return tagIds.getFirst();
        }
        
        String insertSql = "INSERT INTO tags (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tagName);
            return ps;
        }, keyHolder);
        
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void linkRoomTag(Long roomId, Long tagId) {
        String sql = "INSERT INTO room_tags (room_id, tag_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, roomId, tagId);
    }
}
