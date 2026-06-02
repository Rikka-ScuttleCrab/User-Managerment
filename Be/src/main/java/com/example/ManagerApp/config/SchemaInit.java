package com.example.ManagerApp.config;

import java.sql.Connection;
import java.sql.Statement;

import com.example.ManagerApp.utils.DBConnection;



public class SchemaInit {

    public static void init() {

        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(256) NOT NULL UNIQUE,
                email VARCHAR(256) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                gender VARCHAR(20),
                nickname VARCHAR(256),
                active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP,
                created_by VARCHAR(100),
                updated_at TIMESTAMP,
                updated_by VARCHAR(100)
            )
        """;

        String createRoles = """
            CREATE TABLE IF NOT EXISTS roles (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                role_name VARCHAR(100) NOT NULL UNIQUE,
                description VARCHAR(255)
            )
        """;

        String createPermissions = """
            CREATE TABLE IF NOT EXISTS permissions (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                permission_name VARCHAR(100) NOT NULL,
                permission_action VARCHAR(100) NOT NULL,
                description VARCHAR(255),
                permissionDescription VARCHAR(255)
            )
        """;

        String createUserRole = """
            CREATE TABLE IF NOT EXISTS user_role (
                user_id BIGINT NOT NULL,
                role_id BIGINT NOT NULL,
                PRIMARY KEY (user_id, role_id),
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (role_id) REFERENCES roles(id)
            )
        """;

        String createPermissionRole = """
            CREATE TABLE IF NOT EXISTS permission_role (
                permission_id BIGINT NOT NULL,
                role_id BIGINT NOT NULL,
                PRIMARY KEY (permission_id, role_id),
                FOREIGN KEY (permission_id) REFERENCES permissions(id),
                FOREIGN KEY (role_id) REFERENCES roles(id)
            )
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.execute(createUsers);
            st.execute(createRoles);
            st.execute(createPermissions);
            st.execute(createUserRole);
            st.execute(createPermissionRole);

            System.out.println("Tables created successfully!");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
