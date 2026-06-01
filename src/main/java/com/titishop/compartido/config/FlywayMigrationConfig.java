package com.titishop.compartido.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayMigrationConfig {

	@Bean
	@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
	ApplicationRunner flywayMigrationRunner(DataSource dataSource) {
		return args -> Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.load()
				.migrate();
	}
}
