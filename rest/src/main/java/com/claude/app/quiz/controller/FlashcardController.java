package com.claude.app.quiz.controller;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.entity.QuizSet;
import com.claude.app.quiz.service.IFlashcardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flashcards")
@AllArgsConstructor
public class FlashcardController {
    private final IFlashcardService flashcardService;

    @GetMapping
    public List<Flashcard> getAll(@RequestParam(required = false) String set) {
        if (set != null) {
            return flashcardService.findByQuizSet(set);
        }
        return flashcardService.findAll();
    }

    @GetMapping("/sets")
    public List<QuizSet> getQuizSets() {
        return flashcardService.findAllQuizSets();
    }

    @GetMapping("/random")
    public Flashcard getRandom(@RequestParam(required = false) String set) {
        return flashcardService.getRandom(set);
    }

    @GetMapping("/count")
    public Map<String, Long> getCount() {
        List<QuizSet> sets = flashcardService.findAllQuizSets();
        Map<String, Long> counts = sets.stream()
                .collect(Collectors.toMap(
                        QuizSet::getName,
                        s -> flashcardService.countByQuizSet(s.getName())
                ));
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        counts.put("total", total);
        return counts;
    }
}
