package com.example.mapbius_server.common;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseWarmUp implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseWarmUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("SELECT 1");
    }
}
