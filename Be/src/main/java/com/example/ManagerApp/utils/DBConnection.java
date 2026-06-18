package com.example.ManagerApp.utils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class DBConnection {
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    static {
        Properties props = new Properties();
        try (InputStream input =
                 DBConnection.class
                     .getClassLoader()
                     .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException(
                    "Cannot find application.properties"
                );
            }
            props.load(input);
            URL = props.getProperty("spring.datasource.url");
            USERNAME =
                props.getProperty("spring.datasource.username");
            PASSWORD =
                props.getProperty("spring.datasource.password");
            String driver =
                props.getProperty(
                    "spring.datasource.driver-class-name"
                );
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                "Failed to load database config",
                e
            );
        }
    }
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                "Cannot connect to database",
                e
            );
        }
    }
}