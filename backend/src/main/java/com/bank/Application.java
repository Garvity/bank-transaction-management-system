package com.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public static class DatabaseSchemaMigrationRunner implements CommandLineRunner {
        private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaMigrationRunner.class);

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Override
        public void run(String... args) {
            logger.info("Initializing database schema migration checks...");

            // 1. Alter signupthree pin column size
            try {
                jdbcTemplate.execute("ALTER TABLE signupthree MODIFY COLUMN pin VARCHAR(255)");
                logger.info("Upgraded signupthree.pin column size to VARCHAR(255).");
            } catch (Exception e) {
                try {
                    jdbcTemplate.execute("ALTER TABLE signupthree ALTER COLUMN pin VARCHAR(255)");
                    logger.info("Upgraded signupthree.pin column size via H2 syntax.");
                } catch (Exception ex) {
                    logger.debug("Failed to alter signupthree.pin: {}", ex.getMessage());
                }
            }

            // 2. Alter login pin column size
            try {
                jdbcTemplate.execute("ALTER TABLE login MODIFY COLUMN pin VARCHAR(255)");
                logger.info("Upgraded login.pin column size to VARCHAR(255).");
            } catch (Exception e) {
                try {
                    jdbcTemplate.execute("ALTER TABLE login ALTER COLUMN pin VARCHAR(255)");
                    logger.info("Upgraded login.pin column size via H2 syntax.");
                } catch (Exception ex) {
                    logger.debug("Failed to alter login.pin: {}", ex.getMessage());
                }
            }

            // 3. Add pin_lookup_hash column to login
            try {
                jdbcTemplate.execute("ALTER TABLE login ADD COLUMN pin_lookup_hash VARCHAR(100)");
                logger.info("Added pin_lookup_hash column to login table.");
            } catch (Exception e) {
                logger.debug("Could not add pin_lookup_hash column (may already exist): {}", e.getMessage());
            }

            // 4. Alter bank pin column size
            try {
                jdbcTemplate.execute("ALTER TABLE bank MODIFY COLUMN pin VARCHAR(100)");
                logger.info("Upgraded bank.pin column size to VARCHAR(100).");
            } catch (Exception e) {
                try {
                    jdbcTemplate.execute("ALTER TABLE bank ALTER COLUMN pin VARCHAR(100)");
                    logger.info("Upgraded bank.pin column size via H2 syntax.");
                } catch (Exception ex) {
                    logger.debug("Failed to alter bank.pin: {}", ex.getMessage());
                }
            }

            logger.info("Database schema migration checks completed.");
        }
    }
}
