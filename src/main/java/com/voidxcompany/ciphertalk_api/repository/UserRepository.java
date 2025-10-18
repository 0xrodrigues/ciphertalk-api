package com.voidxcompany.ciphertalk_api.repository;

import com.voidxcompany.ciphertalk_api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .userId(rs.getLong("user_id"))
            .username(rs.getString("username"))
            .build();

    public User create(User user) {
        String sql = "INSERT INTO tb_user (username) VALUES (?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            return ps;
        }, keyHolder);

        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setUserId(userId);
        
        return user;
    }

    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM tb_user WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM tb_user WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public User findOrCreate(String username) {
        return findByUsername(username)
                .orElseGet(() -> create(User.builder().username(username).build()));
    }
}
