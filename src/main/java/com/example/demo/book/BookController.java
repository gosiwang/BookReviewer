package com.example.demo.book;

import com.example.demo.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService; // ReviewService 추가

    // 메인 검색 화면
    @GetMapping("/books")
    public String listBooks(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        return "mainsearch";
    }

    // 검색 결과 화면
    @GetMapping("/books/results")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            // 검색된 책들 가져오기
            var books = bookService.searchBooks(keyword);
            model.addAttribute("books", books);
            model.addAttribute("keyword", keyword);

            // 각 도서에 대해 해당 리뷰도 함께 가져오기
            for (var book : books) {
                var reviews = reviewService.findReviewsByBook(book.getId()); // 리뷰 서비스에서 도서별 리뷰를 가져옵니다.
                model.addAttribute("reviews" + book.getId(), reviews); // 리뷰 목록을 모델에 추가
            }
        }
        return "searchresults";
    }
}
