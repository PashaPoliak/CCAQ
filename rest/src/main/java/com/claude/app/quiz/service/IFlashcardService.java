package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.entity.QuizSet;

import java.util.List;

public interface IFlashcardService {
    List<Flashcard> findByQuizSet(String setName);

    List<Flashcard> findAll();

    List<QuizSet> findAllQuizSets();

    long countByQuizSet(String setName);

    Flashcard getRandom(String setName);
}
