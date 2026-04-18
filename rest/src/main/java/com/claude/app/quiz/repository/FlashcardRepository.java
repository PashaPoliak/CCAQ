package com.claude.app.quiz.repository;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.entity.QuizSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByQuizSet(QuizSet quizSet);

    List<Flashcard> findByQuizSetName(String name);
}
