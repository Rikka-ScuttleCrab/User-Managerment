package com.example.ManagerApp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.ManagerApp.dao.RoleDAO;
import com.example.ManagerApp.domain.Role;
import com.example.ManagerApp.utils.DBConnection;

@Repository
public class RoleDAOImpl implements RoleDAO {

    @Override
    public Optional<Role> findById(Long id) {

        String sql = "SELECT * FROM roles WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Role role = new Role();

                role.setId(rs.getLong("id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));

                return Optional.of(role);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error find role by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {

        String sql =
            "SELECT * FROM roles WHERE role_name = ?";

        try (
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Role role = new Role();

                role.setId(rs.getLong("id"));

                role.setRoleName(
                    rs.getString("role_name")
                );
                role.setDescription(rs.getString("description"));

                return Optional.of(role);
            }

            return Optional.empty();

        } catch (Exception e) {

            throw new RuntimeException(
                "Error while finding role",
                e
            );
        }
    }

    @Override
    public boolean existsByRoleName(String roleName) {

        String sql = """
            SELECT COUNT(*)
            FROM roles
            WHERE role_name = ?
        """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error checking role",
                    e
            );
        }

        return false;
    }

    @Override
    public Role save(Role role) {

        String sql = "INSERT INTO roles(role_name, description) VALUES(?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps =
                conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {

            ps.setString(1, role.getRoleName());
            ps.setString(2, "");
            if(role.getDescription() != null)
                ps.setString(2, (role.getDescription()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                role.setId(rs.getLong(1));
            }

            return role;

        } catch (Exception e) {
            throw new RuntimeException("Error while saving role", e);
        }
    }

    @Override
    public void update(Role role) {

        String sql =
            "UPDATE roles SET " +
            "role_name = ?, " +
            "description = ? " +
            "WHERE id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, role.getRoleName());

            ps.setString(2, role.getDescription());

            ps.setLong(3, role.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating role",
                e
            );
        }
    }


    @Override
    public List<Role> findAll() {

        String sql = """
            SELECT *
            FROM roles
        """;

        List<Role> roles = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Role role = new Role();

                role.setId(rs.getLong("id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching roles", e);
        }

        return roles;
    }

    @Override
    public void deleteById(Long id) {

        String deleteUserRoleSql =
                "DELETE FROM user_role WHERE role_id = ?";

        String deletePermissionRoleSql =
                "DELETE FROM permission_role WHERE role_id = ?";

        String deleteRoleSql =
                "DELETE FROM roles WHERE id = ?";

        try (
                Connection conn = DBConnection.getConnection()
        ) {

            conn.setAutoCommit(false);

            // delete user_role
            try (
                    PreparedStatement ps =
                            conn.prepareStatement(deleteUserRoleSql)
            ) {

                ps.setLong(1, id);
                ps.executeUpdate();
            }

            // delete permission_role
            try (
                    PreparedStatement ps =
                            conn.prepareStatement(deletePermissionRoleSql)
            ) {

                ps.setLong(1, id);
                ps.executeUpdate();
            }

            // delete role
            try (
                    PreparedStatement ps =
                            conn.prepareStatement(deleteRoleSql)
            ) {

                ps.setLong(1, id);

                int affected = ps.executeUpdate();

                if (affected == 0) {
                    throw new RuntimeException("Role not found");
                }
            }

            conn.commit();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error deleting role",
                    e
            );
        }
    }
}