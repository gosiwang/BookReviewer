package com.example.demo.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
	@Query("SELECT distinct b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	    List<Book> searchBooks(@Param("keyword") String keyword);
}