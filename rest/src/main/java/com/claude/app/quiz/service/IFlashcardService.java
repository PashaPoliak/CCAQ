package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;

import java.util.List;

public interface IFlashcardService {
    List<Flashcard> findByCategory(String category);

    List<Flashcard> findAll();

    long countByCategory(String category);

    Flashcard getRandom(String category);
}
