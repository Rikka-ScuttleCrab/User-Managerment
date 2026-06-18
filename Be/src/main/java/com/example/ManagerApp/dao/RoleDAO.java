package com.example.ManagerApp.dao;
import java.util.List;
import java.util.Optional;
import com.example.ManagerApp.domain.Role;
public interface RoleDAO {
    Optional<Role> findById(Long id);
    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
    List<Role> findAll();
    void update(Role role);
    Role save(Role role);
    void deleteById(Long id);
}