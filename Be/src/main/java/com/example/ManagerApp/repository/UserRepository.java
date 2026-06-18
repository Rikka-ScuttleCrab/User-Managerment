// package com.example.ManagerApp.repository;
// import java.util.Optional;
// import org.springframework.data.jpa.repository.JpaRepository;
// import com.example.ManagerApp.domain.User;
// public interface UserRepository extends JpaRepository<User, Long> {
//     @Override
//     Optional<User> findById(Long id);
//     Optional<User> findByUsername(String username);
//     Optional<User> findByEmail(String email);
//     Optional<User> findByUsernameOrEmail(String username, String email);
// }