package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.entity.QuizSet;
import com.claude.app.quiz.repository.FlashcardRepository;
import com.claude.app.quiz.repository.QuizSetRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FlashcardService implements IFlashcardService {
    private final FlashcardRepository flashcardRepository;
    private final QuizSetRepository quizSetRepository;
    private final FlashcardLoaderService flashcardLoaderService;

    @PostConstruct
    public void init() {
        flashcardLoaderService.loadData();
    }

    @Override
    public List<Flashcard> findByQuizSet(String setName) {
        return flashcardRepository.findByQuizSetName(setName);
    }

    @Override
    public List<Flashcard> findAll() {
        return flashcardRepository.findAll();
    }

    @Override
    public List<QuizSet> findAllQuizSets() {
        return quizSetRepository.findAll();
    }

    @Override
    public long countByQuizSet(String setName) {
        return flashcardRepository.findByQuizSetName(setName).size();
    }

    @Override
    public Flashcard getRandom(String setName) {
        List<Flashcard> cards;
        if (setName != null && !setName.isEmpty()) {
            cards = flashcardRepository.findByQuizSetName(setName);
        } else {
            cards = flashcardRepository.findAll();
        }
        if (cards.isEmpty()) {
            return null;
        }
        int size = cards.size();
        if (size == 1) {
            return cards.get(0);
        }
        return cards.get((int) (Math.random() * size));
    }
}
