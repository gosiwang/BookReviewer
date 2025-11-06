package com.example.demo.review;

import com.example.demo.user.Users;
import com.example.demo.book.Book;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 리뷰 작성자

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // 리뷰 대상 도서

    @Column(nullable = false)
    private String content; // 리뷰 내용

    @Column(nullable = false)
    private int rating; // 평점 (1~5)

    private LocalDateTime createdDate; // 리뷰 작성일
    private LocalDateTime modifiedDate; // 리뷰 수정일

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}
