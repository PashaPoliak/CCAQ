package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.repository.FlashcardRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class FlashcardLoaderService {
    private final FlashcardRepository flashcardRepository;

    public FlashcardLoaderService(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }

    @Transactional
    public void loadData() throws IOException {
        var resource = new ClassPathResource("db/quizlet_cards.csv");
        if (!resource.exists()) {
            return;
        }
        
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.lines()
                .skip(1)
                .filter(line -> line.contains(","))
                .forEach(line -> {
                    String[] parts = parseCsvLine(line);
                    if (parts.length >= 3) {
                        Flashcard card = new Flashcard();
                        card.setQuestion(parts[0]);
                        card.setAnswer(parts[1]);
                        card.setCategory(parts[2]);
                        flashcardRepository.save(card);
                    }
                });
        }
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
