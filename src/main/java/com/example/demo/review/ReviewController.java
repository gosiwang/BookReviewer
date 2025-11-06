package com.example.demo.review;

import com.example.demo.book.Book;
import com.example.demo.book.BookRepository;
import com.example.demo.user.Users;
import com.example.demo.user.UserRepository;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // 리뷰 작성 화면
    @GetMapping("/create")
    public String showReviewCreatePage(@RequestParam("bookId") Long bookId, Model model) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "도서를 찾을 수 없습니다.");
        }
        model.addAttribute("book", book);
        return "reviewcreate"; // 리뷰 작성 페이지로 이동
    }

    // 리뷰 저장
    @PostMapping("/create")
    public String createReview(
            @RequestParam("bookId") Long bookId,
            @RequestParam("content") String content,
            @RequestParam("rating") int rating
    ) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Users currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        reviewService.createReview(currentUser, bookId, content, rating);
        return "redirect:/books"; // 리뷰 작성 후 도서 목록으로 이동
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showReviewEditPage(@PathVariable("id") Long id, Model model, Principal principal) {
        Review review = reviewService.getReview(id);

        // 작성자 본인만 수정 가능하도록 체크
        if (!review.getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        model.addAttribute("review", review);
        model.addAttribute("book", review.getBook());
        return "reviewcreate";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateReview(@PathVariable("id") Long id,
            @RequestParam("content") String content,
            @RequestParam("rating") int rating,
            Principal principal) {
        Review review = reviewService.getReview(id);

        if (!review.getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        reviewService.updateReview(id, content, rating);
        return "redirect:/books";
    }

    // 리뷰 삭제
    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteReview(@PathVariable("id") Long id, Principal principal) {
        Review review = reviewService.getReview(id);

        // 작성자 본인만 삭제 가능하도록 체크
        if (!review.getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        reviewService.deleteReview(id);
        return "redirect:/books";
    }
}


