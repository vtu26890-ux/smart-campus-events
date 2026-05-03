package com.campus.events.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Converts Railway's DATABASE_URL (postgresql://user:pass@host/db)
 * to the JDBC format Spring needs (jdbc:postgresql://host/db).
 */
@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            // Convert Railway format → JDBC format
            String jdbcUrl = databaseUrl
                .replace("postgresql://", "jdbc:postgresql://")
                .replace("postgres://", "jdbc:postgresql://");

            return DataSourceBuilder.create()
                .url(jdbcUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
        }

        // Local dev fallback → H2 in-memory
        return DataSourceBuilder.create()
            .url("jdbc:h2:mem:campusdb;DB_CLOSE_DELAY=-1")
            .driverClassName("org.h2.Driver")
            .username("sa")
            .password("")
            .build();
    }
}
