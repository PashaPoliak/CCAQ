package com.claude.app.quiz.repository;

import com.claude.app.quiz.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByCategory(String category);

    List<Flashcard> findByCategoryIn(List<String> categories);

    long countByCategory(String category);
}