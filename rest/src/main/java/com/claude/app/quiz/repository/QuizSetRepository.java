package com.claude.app.quiz.repository;

import com.claude.app.quiz.entity.QuizSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
    Optional<QuizSet> findByName(String name);
}
