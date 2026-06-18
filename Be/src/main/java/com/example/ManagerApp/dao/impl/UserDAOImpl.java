package com.example.ManagerApp.dao.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;
import com.example.ManagerApp.dao.UserDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.domain.User;
import com.example.ManagerApp.domain.enumeration.Gender;
import com.example.ManagerApp.security.SecurityService;
import com.example.ManagerApp.utils.DBConnection;;
@Repository
public class UserDAOImpl implements UserDAO {
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = mapUser(rs);
                user.setRoles(
                        getRolesByUserId(
                                conn,
                                user.getId()
                        )
                );
                return Optional.of(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error findById", e);
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error findByUsername", e);
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error findByEmail", e);
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        String sql =
            "SELECT * FROM users WHERE username = ? OR email = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = mapUser(rs);
                user.setRoles(getRolesByUserId(conn, user.getId()));
                return Optional.of(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error findByUsernameOrEmail", e);
        }
        return Optional.empty();
    }
    @Override
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        String deleteSql =
                "DELETE FROM user_role WHERE user_id = ?";
        String insertSql =
                """
                INSERT INTO user_role(user_id, role_id)
                VALUES (?, ?)
                """;
        try (
                Connection conn =
                        DBConnection.getConnection()
        ) {
            conn.setAutoCommit(false);
            // clear old roles
            try (
                    PreparedStatement deletePs =
                            conn.prepareStatement(deleteSql)
            ) {
                deletePs.setLong(1, userId);
                deletePs.executeUpdate();
            }
            // insert new roles
            try (
                    PreparedStatement insertPs =
                            conn.prepareStatement(insertSql)
            ) {
                for (Long roleId : roleIds) {
                    insertPs.setLong(1, userId);
                    insertPs.setLong(2, roleId);
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error assign roles to user",
                    e
            );
        }
    }
    @Override
    public void assignUsersToRole(
            Long roleId,
            List<Long> userIds
    ) {
        String deleteSql =
                "DELETE FROM user_role WHERE role_id = ?";
        String insertSql =
                """
                INSERT INTO user_role(user_id, role_id)
                VALUES (?, ?)
                """;
        try (
                Connection conn =
                        DBConnection.getConnection()
        ) {
            conn.setAutoCommit(false);
            // clear old users
            try (
                    PreparedStatement deletePs =
                            conn.prepareStatement(deleteSql)
            ) {
                deletePs.setLong(1, roleId);
                deletePs.executeUpdate();
            }
            // insert new users
            try (
                    PreparedStatement insertPs =
                            conn.prepareStatement(insertSql)
            ) {
                for (Long userId : userIds) {
                    insertPs.setLong(1, userId);
                    insertPs.setLong(2, roleId);
                    insertPs.addBatch();
                }
                insertPs.executeBatch();
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error assign users to role",
                    e
            );
        }
    }
    @Override
    public void save(User user) {
        String userSql =
            """
            INSERT INTO users(
                username,
                email,
                password_hash,
                gender,
                nickname,
                active,
                created_at,
                created_by
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        String userRoleSql =
            """
            INSERT INTO user_role(user_id, role_id)
            VALUES (?, ?)
            """;
        try (
            Connection conn = DBConnection.getConnection()
        ) {
            conn.setAutoCommit(false);
            Long userId;
            // INSERT USER
            try (
                PreparedStatement ps = conn.prepareStatement(
                    userSql,
                    PreparedStatement.RETURN_GENERATED_KEYS
                )
            ) {
                Instant now = Instant.now();
                String currentUser =
                    SecurityService
                        .getCurrentUserLogin()
                        .orElse("SYSTEM");
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(
                    4,
                    user.getGender() != null
                        ? user.getGender().name()
                        : null
                );
                ps.setString(5, user.getNickname());
                ps.setBoolean(6, true);
                ps.setTimestamp(
                    7,
                    java.sql.Timestamp.from(now)
                );
                ps.setString(8, currentUser);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new RuntimeException(
                        "Cannot create user"
                    );
                }
                userId = rs.getLong(1);
                user.setId(userId);
            }
            // INSERT USER ROLES
            if (
                user.getRoles() != null
                && !user.getRoles().isEmpty()
            ) {
                try (
                    PreparedStatement ps =
                        conn.prepareStatement(userRoleSql)
                ) {
                    for (Role role : user.getRoles()) {
                        ps.setLong(1, userId);
                        ps.setLong(2, role.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error saving user",
                e
            );
        }
    }
    @Override
    public void updateInfo(User user) {
        String sql =
            "UPDATE users SET " +
            "email = ?, " +
            "gender = ?, " +
            "nickname = ? " +
            "WHERE id = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, user.getEmail());
            ps.setString(
                2,
                user.getGender() != null
                    ? user.getGender().name()
                    : null
            );
            ps.setString(3, user.getNickname());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating user info",
                e
            );
        }
    }
    @Override
    public void updateRole(Long userId, Long roleId) {
        String sql =
            "UPDATE users SET role_id = ? WHERE id = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setLong(1, roleId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating role",
                e
            );
        }
    }
    @Override
    public void updateActive(Long id, Boolean active) {
        String sql =
                "UPDATE users SET active = ? WHERE id = ?";
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setBoolean(1, active);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error updating active status",
                    e
            );
        }
    }
    @Override
    public void updatePassword(Long id, String password) {
        String sql =
            "UPDATE users SET password_hash = ? WHERE id = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, password);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating password",
                e
            );
        }
    }
    // mapper từ ResultSet → User
    private User mapUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password_hash"));
        String gender = rs.getString("gender");
        if (gender != null) {
            user.setGender(
                    Gender.valueOf(gender)
            );
        }
        user.setNickname(rs.getString("nickname"));
        user.setActive(rs.getBoolean("active"));
        return user;
    }
    @Override
    public List<User> findAll(int page, int size, boolean includeAdmin) {
        String sql;
        if (includeAdmin) {
            sql = """
                SELECT *
                FROM users
                LIMIT ?
                OFFSET ?
            """;
        } else {
            sql = """
                SELECT DISTINCT u.*
                FROM users u
                WHERE u.id NOT IN (
                    SELECT ur.user_id
                    FROM user_role ur
                    JOIN roles r
                        ON ur.role_id = r.id
                    WHERE r.role_name = 'ADMIN'
                )
                LIMIT ?
                OFFSET ?
            """;
        }
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * size;
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, size);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = mapUser(rs);
                user.setRoles(
                        getRolesByUserId(
                                conn,
                                user.getId()
                        )
                );
                users.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error fetching users",
                    e
            );
        }
        return users;
    }
    @Override
    public long countUsers(boolean includeAdmin) {
        String sql;
        if (includeAdmin) {
            sql = """
                SELECT COUNT(*)
                FROM users
            """;
        } else {
            sql = """
                SELECT COUNT(DISTINCT u.id)
                FROM users u
                WHERE u.id NOT IN (
                    SELECT ur.user_id
                    FROM user_role ur
                    JOIN roles r
                        ON ur.role_id = r.id
                    WHERE r.role_name = 'ADMIN'
                )
            """;
        }
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error count users",
                    e
            );
        }
        return 0;
    }
    private Set<Role> getRolesByUserId(Connection conn, Long userId) throws Exception {
        String sql = """
            SELECT r.id, r.role_name
            FROM roles r
            JOIN user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            Set<Role> roles = new java.util.HashSet<>();
            while (rs.next()) {
                Role r = new Role();
                r.setId(rs.getLong("id"));
                r.setRoleName(rs.getString("role_name"));
                roles.add(r);
            }
            return roles;
        }
    }
    @Override
    public List<User> searchUsers(String keyword, String role, String gender, String status, boolean includeAdmin) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT u.*
            FROM users u
            LEFT JOIN user_role ur
                ON u.id = ur.user_id
            LEFT JOIN roles r
                ON ur.role_id = r.id
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();
        // keyword
        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                AND (
                    LOWER(u.username) LIKE ?
                    OR LOWER(u.email) LIKE ?
                    OR LOWER(u.nickname) LIKE ?
                    OR CAST(u.id AS CHAR) LIKE ?
                )
            """);
            String k = "%" + keyword.toLowerCase() + "%";
            params.add(k);
            params.add(k);
            params.add(k);
            params.add(k);
        }
        // role
        if (role != null && !role.isBlank()) {
            sql.append("""
                AND UPPER(r.role_name) = UPPER(?)
            """);
            params.add(role);
        }
        // gender
        if (gender != null && !gender.isBlank()) {
            sql.append("""
                AND UPPER(u.gender) = UPPER(?)
            """);
            params.add(gender);
        }
        // status
        if ("ACTIVE".equalsIgnoreCase(status)) {
            sql.append("""
                AND u.active = TRUE
            """);
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            sql.append("""
                AND u.active = FALSE
            """);
        }
        if (!includeAdmin) {
            sql.append("""
                AND u.id NOT IN (
                    SELECT ur2.user_id
                    FROM user_role ur2
                    JOIN roles r2
                        ON ur2.role_id = r2.id
                    WHERE UPPER(r2.role_name) = 'ADMIN'
                )
            """);
        }
        List<User> users = new ArrayList<>();
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = mapUser(rs);
                user.setRoles(
                        getRolesByUserId(
                                conn,
                                user.getId()
                        )
                );
                users.add(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error searching users",
                    e
            );
        }
        return users;
    }
}