package com.example.demo.review;

import com.example.demo.book.Book;
import com.example.demo.book.BookRepository;
import com.example.demo.user.Users;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    // 특정 도서의 리뷰들 반환
    public List<Review> findReviewsByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    // 리뷰 생성
    @Transactional
    public void createReview(Users user, Long bookId, String content, int rating) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도서입니다."));
        
        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByUserAndBook_Id(user, bookId)) {
            throw new IllegalStateException("이미 리뷰를 작성하셨습니다.");
        }

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setContent(content);
        review.setRating(rating);
        reviewRepository.save(review);
    }

    // 리뷰 조회
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, String content, int rating) {
        Review review = getReview(reviewId);
        review.setContent(content);
        review.setRating(rating);
        reviewRepository.save(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = getReview(reviewId);
        reviewRepository.delete(review);
    }

    // 사용자의 리뷰 존재 여부 확인
    public boolean hasUserReviewed(Users user, Long bookId) {
        return reviewRepository.existsByUserAndBook_Id(user, bookId);
    }
}