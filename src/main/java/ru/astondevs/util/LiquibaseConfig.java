package ru.astondevs.util;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);  // здесь DataSource из Bean
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");;
        liquibase.setShouldRun(true); // обязательно
        return liquibase;
    }
}
