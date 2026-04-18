package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.entity.QuizSet;
import com.claude.app.quiz.repository.FlashcardRepository;
import com.claude.app.quiz.repository.QuizSetRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FlashcardLoaderService {

    private final FlashcardRepository flashcardRepository;
    private final QuizSetRepository quizSetRepository;
    private final ResourcePatternResolver resourcePatternResolver;

    @Transactional
    public void loadData() {
        if (!quizSetRepository.findAll().isEmpty()) {
            return;
        }

        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:db/*.csv");
            Set<String> csvFiles = Arrays.stream(resources)
                    .map(r -> {
                        try {
                            return r.getFilename();
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (String csvFile : csvFiles) {
                loadCsvFile(csvFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load flashcards", e);
        }
    }

    private void loadCsvFile(String csvFileName) throws Exception {
        String setName = csvFileName.replace(".csv", "");
        String title = formatTitle(setName);

        QuizSet quizSet = new QuizSet();
        quizSet.setName(setName);
        quizSet.setTitle(title);
        quizSet = quizSetRepository.save(quizSet);

        Resource resource = resourcePatternResolver.getResource("classpath:db/" + csvFileName);
        if (!resource.exists()) {
            return;
        }

        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            List<String> lines = reader.lines().toList();
            if (lines.size() < 2) return;

            String header = lines.get(0).toLowerCase();
            if (!header.contains("question") || !header.contains("correct")) {
                return;
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = parseCsvLine(line);
                if (parts.length < 6) continue;

                Flashcard card = new Flashcard();
                card.setQuestion(parts[0]);
                card.setOptionA(parts[1]);
                card.setOptionB(parts[2]);
                card.setOptionC(parts[3]);
                card.setOptionD(parts[4]);
                card.setCorrectAnswer(parts[5]);
                card.setExplanation(parts.length > 6 ? parts[6] : "");
                card.setQuizSet(quizSet);
                flashcardRepository.save(card);
            }
        }
    }

    private String formatTitle(String name) {
        return Arrays.stream(name.split("[-_]"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(name);
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
