// package com.example.ManagerApp.domain;
// import java.time.Instant;
// import com.example.ManagerApp.security.SecurityService;
// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// @Entity
// @Table(name = "categories")
// @Getter
// @Setter
// public class Category {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
//     @Column(unique = true, nullable = false)
//     private String categoryName;
//     @Column(name = "description")
//     private String description;
//     @Column(name = "created_at")
//     private Instant createdAt;
//     @Column(name = "created_by")
//     private String createdBy;
//     @Column(name = "updated_at")
//     private Instant updatedAt;
//     @Column(name = "updated_by")
//     private String updatedBy;
//     @PrePersist
//     public void handleBeforeCreate() {
//         this.createdAt = Instant.now();
//         this.createdBy = SecurityService.getCurrentUserLogin().isPresent() ? SecurityService.getCurrentUserLogin().get()
//                 : "";
//     }
//     @PreUpdate
//     public void handleBeforeUpdate() {
//         this.updatedAt = Instant.now();
//         this.updatedBy = SecurityService.getCurrentUserLogin().isPresent() ? SecurityService.getCurrentUserLogin().get()
//                 : "";
//     }
// }