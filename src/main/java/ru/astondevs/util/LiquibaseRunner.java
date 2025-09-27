package ru.astondevs.util;



import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class LiquibaseRunner {
    public static void runMigrations() {
        Properties properties = new Properties();
        try (InputStream inputStream = ConnectionManager.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(inputStream);

            String url = properties.getProperty("hibernate.connection.url");
            String username = properties.getProperty("hibernate.connection.username");
            String password = properties.getProperty("hibernate.connection.password");

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(conn));

                Liquibase liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yaml",
                        new ClassLoaderResourceAccessor(),
                        database
                );


                liquibase.update(new liquibase.Contexts(), new liquibase.LabelExpression());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error running Liquibase migrations", e);
        }
    }
}
