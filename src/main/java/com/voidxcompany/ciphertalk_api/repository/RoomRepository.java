package com.voidxcompany.ciphertalk_api.repository;

import com.voidxcompany.ciphertalk_api.model.Room;
import com.voidxcompany.ciphertalk_api.model.RoomControl;
import com.voidxcompany.ciphertalk_api.model.RoomVisibility;
import com.voidxcompany.ciphertalk_api.query.RoomQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoomRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoomControlRepository roomControlRepository;

    public void createNewRoom(Room domain) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("address", domain.getAddress())
                .addValue("name", domain.getName())
                .addValue("description", domain.getDescription())
                .addValue("visibility", domain.getVisibility().name())
                .addValue("createdAt", domain.getCreatedAt().toString())
                .addValue("maxUsers", domain.getMaxUsers());
        jdbcTemplate.update(RoomQuery.INSERT_ROOM, params);
        roomControlRepository.save(new RoomControl(domain));
    }

    public List<Room> getAllPublicRooms() {
        return jdbcTemplate.query(RoomQuery.GET_ALL_PUBLIC_ROOMS, roomRowMapper());
    }

    public Room getRoomByAddress(String address) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("address", address);
        return jdbcTemplate.queryForObject(RoomQuery.GET_ROOM_BY_ADDRESS, params, roomRowMapper());
    }

    private RowMapper<Room> roomRowMapper() {
        return (rs, rowNum) -> {
            Room room = new Room();
            room.setId(rs.getLong("id"));
            room.setAddress(rs.getString("address"));
            room.setName(rs.getString("name"));
            room.setDescription(rs.getString("description"));
            room.setMaxUsers(rs.getInt("max_users"));
            room.setVisibility(RoomVisibility.valueOf(rs.getString("visibility")));
            room.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return room;
        };
    }

}
