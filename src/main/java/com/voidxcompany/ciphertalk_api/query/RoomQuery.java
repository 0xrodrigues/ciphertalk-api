package com.voidxcompany.ciphertalk_api.query;

public class RoomQuery {
    public static final String INSERT_ROOM = """
                INSERT INTO rooms (address, name, description, visibility, created_at)
                VALUES (:address, :name, :description, :visibility, :createdAt)
            """;

    public static final String GET_ALL_PUBLIC_ROOMS = """
                SELECT id, address, name, description, visibility, created_at
                FROM rooms
                WHERE visibility = 'PUBLIC'
                ORDER BY created_at DESC
            """;

    public static final String GET_ROOM_BY_ADDRESS = """
            select id, address, name, description, visibility, created_at from rooms where address = :address
            """;
}
