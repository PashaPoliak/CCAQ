package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.repository.FlashcardRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class FlashcardService implements IFlashcardService {
    private final FlashcardRepository flashcardRepository;
    private boolean loaded = false;

    public FlashcardService(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (loaded || flashcardRepository.count() > 0) {
            loaded = true;
            return;
        }

        try {
            var resource = new ClassPathResource("db/quizlet_cards.csv");
            if (!resource.exists()) {
                loaded = true;
                return;
            }

            try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                reader.lines()
                    .skip(1)
                    .filter(line -> line.contains(","))
                    .forEach(line -> {
                        String[] parts = parseCsvLine(line);
                        if (parts.length >= 3 && !parts[0].isEmpty()) {
                            Flashcard card = new Flashcard(0L, parts[0], parts[1], parts[2]);
                            flashcardRepository.save(card);
                        }
                    });
            }
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load flashcards", e);
        }
    }

    @Override
    public List<Flashcard> findByCategory(String category) {
        return flashcardRepository.findByCategory(category);
    }

    @Override
    public List<Flashcard> findAll() {
        return flashcardRepository.findAll();
    }

    @Override
    public long countByCategory(String category) {
        return flashcardRepository.countByCategory(category);
    }

    @Override
    public Flashcard getRandom(String category) {
        List<Flashcard> cards;
        if (category != null) {
            cards = flashcardRepository.findByCategory(category);
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

    private String[] parseCsvLine(String line) {
        var result = new java.util.ArrayList<String>();
        var current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());

        return result.toArray(new String[0]);
    }
}