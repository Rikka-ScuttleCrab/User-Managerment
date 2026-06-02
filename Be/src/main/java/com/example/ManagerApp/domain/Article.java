// package com.example.ManagerApp.domain;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.Size;
// import lombok.Getter;
// import lombok.Setter;

// import java.time.Instant;

// import com.example.ManagerApp.domain.enumeration.ArticleStatus;
// import com.example.ManagerApp.security.SecurityService;

// @Entity
// @Table(name = "articles")
// @Getter
// @Setter
// public class Article {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Size(min = 2, max = 256)
//     private String title;

//     @Lob
//     @Column(columnDefinition = "TEXT")
//     private String content;

//     @ManyToOne
//     @JoinColumn(name = "author_id")
//     private User author;

//     @ManyToOne
//     @JoinColumn(name = "category_id")
//     private Category category;

//     @Enumerated(EnumType.STRING)
//     private ArticleStatus status;

//     @Column(name = "like_count")
//     private Long likeCount = 0L;

//     @Column(name = "dislike_count")
//     private Long dislikeCount = 0L;

//     @ManyToOne
//     @JoinColumn(name = "approved_by")
//     private User approvedBy;

//     @Column(name = "approved_at")
//     private Instant approvedAt;

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