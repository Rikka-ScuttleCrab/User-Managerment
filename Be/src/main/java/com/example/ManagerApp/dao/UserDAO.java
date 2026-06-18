package com.example.ManagerApp.dao;
import java.util.List;
import java.util.Optional;
import com.example.ManagerApp.domain.User;
public interface UserDAO {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    List<User> findAll(int page, int size, boolean includeAdmin);
    long countUsers(boolean includeAdmin);
    List<User> searchUsers(String keyword, String role, String gender, String status, boolean includeAdmin);
    void assignRolesToUser(Long userId, List<Long> roleIds);
    void assignUsersToRole(Long roleId, List<Long> userIds);
    void save(User user);
    void updateInfo(User user);
    void updateRole(Long userId, Long roleId);
    void updateActive(Long id, Boolean active);
    void updatePassword(Long id, String password);
}