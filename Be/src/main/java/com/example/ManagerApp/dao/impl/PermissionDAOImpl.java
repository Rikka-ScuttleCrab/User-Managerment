package com.example.ManagerApp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.ManagerApp.admin.services.dto.response.RolePermissionResponse;
import com.example.ManagerApp.dao.PermissionDAO;
import com.example.ManagerApp.utils.DBConnection;

@Repository
public class PermissionDAOImpl
        implements PermissionDAO {

    @Override
    public Boolean hasPermission(
            String roleName,
            String permissionName,
            String actionName) {

        String sql = """
            SELECT COUNT(*) AS total
            FROM roles r
            INNER JOIN permission_role rp
                ON r.id = rp.role_id
            INNER JOIN permissions p
                ON p.id = rp.permission_id
            WHERE r.role_name = ?
            AND p.permission_name = ?
            AND p.permission_action = ?
        """;

        try (
            Connection conn =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);
            ps.setString(2, permissionName);
            ps.setString(3, actionName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt("total") > 0;
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error checking permission",
                    e
            );
        }

        return false;
    }


    @Override
    public void addPermissionToRole(
            String roleName,
            String permissionName,
            String actionName
    ) {

        String sql = """
            INSERT INTO permission_role(role_id, permission_id)
            SELECT r.id, p.id
            FROM roles r, permissions p
            WHERE r.role_name = ?
            AND p.permission_name = ?
            AND p.permission_action = ?
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);
            ps.setString(2, permissionName);
            ps.setString(3, actionName);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error add permission", e);
        }
    }

    @Override
    public void removePermissionFromRole(
            String roleName,
            String permissionName,
            String actionName
    ) {

        String sql = """
            DELETE rp
            FROM permission_role rp
            JOIN roles r
                ON rp.role_id = r.id
            JOIN permissions p
                ON rp.permission_id = p.id
            WHERE r.role_name = ?
            AND p.permission_name = ?
            AND p.permission_action = ?
        """;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, roleName);
            ps.setString(2, permissionName);
            ps.setString(3, actionName);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error remove permission", e);
        }
    }

    @Override
    public boolean existsByNameAndAction(
            String permissionName,
            String actionName
    ) {

        String sql = """
            SELECT COUNT(*)
            FROM permissions
            WHERE permission_name = ?
            AND permission_action = ?
        """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, permissionName);
            ps.setString(2, actionName);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error checking permission existence",
                    e
            );
        }

        return false;
    }


    @Override
    public List<RolePermissionResponse>
    getAllRolePermissions() {

        String sql = """
            SELECT
                r.role_name,
                p.permission_name,
                p.permissionDescription,
                p.permission_action,
                p.description
            FROM permission_role rp
            JOIN roles r
                ON rp.role_id = r.id
            JOIN permissions p
                ON rp.permission_id = p.id
            ORDER BY r.role_name
        """;

        List<RolePermissionResponse> results =
                new ArrayList<>();

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                results.add(
                        new RolePermissionResponse(
                                rs.getString("role_name"),
                                rs.getString("permission_name"),
                                rs.getString("permissionDescription"),
                                rs.getString("permission_action"),
                                rs.getString("description")
                        )
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error fetching role permissions",
                    e
            );
        }

        return results;
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {

        String sql = """
            SELECT DISTINCT p.permission_name, 
            p.permission_action
            FROM users u
            JOIN user_role ur ON u.id = ur.user_id
            JOIN roles r ON ur.role_id = r.id
            JOIN permission_role rp ON r.id = rp.role_id
            JOIN permissions p ON rp.permission_id = p.id
            WHERE u.id = ?
        """;

        List<String> permissions = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String permission =
                        rs.getString("permission_name")
                        + ":"
                        + rs.getString("permission_action");

                permissions.add(permission);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error fetching permissions by user", e);
        }

        return permissions;
    }

    @Override
    public void updateRolePermissions(
            Long roleId,
            String permissionName,
            List<String> actions
    ) {

        String deleteSql = """
            DELETE pr
            FROM permission_role pr
            JOIN permissions p
                ON pr.permission_id = p.id
            WHERE pr.role_id = ?
            AND p.permission_name = ?
        """;

        String insertSql = """
            INSERT INTO permission_role(role_id, permission_id)
            SELECT ?, p.id
            FROM permissions p
            WHERE p.permission_name = ?
            AND p.permission_action = ?
        """;

        try (
                Connection conn =
                        DBConnection.getConnection()
        ) {

            conn.setAutoCommit(false);

            // DELETE OLD ACTIONS
            try (
                    PreparedStatement ps =
                            conn.prepareStatement(deleteSql)
            ) {

                ps.setLong(1, roleId);
                ps.setString(2, permissionName);

                ps.executeUpdate();
            }

            // INSERT NEW ACTIONS
            try (
                    PreparedStatement ps =
                            conn.prepareStatement(insertSql)
            ) {

                for (String action : actions) {

                    ps.setLong(1, roleId);
                    ps.setString(2, permissionName);
                    ps.setString(3, action);

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conn.commit();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error updating role permissions",
                    e
            );
        }
    }
}