package com.claude.app.quiz.controller;

import com.claude.app.quiz.entity.Flashcard;
import com.claude.app.quiz.service.IFlashcardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flashcards")
@AllArgsConstructor
public class FlashcardController {
    private final IFlashcardService flashcardService;

    @GetMapping
    public List<Flashcard> getAll(@RequestParam(required = false) String category) {
        if (category != null) {
            return flashcardService.findByCategory(category);
        }
        return flashcardService.findAll();
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return List.of("fundamental", "foundations");
    }

    @GetMapping("/random")
    public Flashcard getRandom(@RequestParam(required = false) String category) {
        return flashcardService.getRandom(category);
    }

    @GetMapping("/count")
    public Map<String, Long> getCount() {
        long fundamental = flashcardService.countByCategory("fundamental");
        long foundations = flashcardService.countByCategory("foundations");
        return Map.of("fundamental", fundamental, "foundations", foundations, "total", fundamental + foundations);
    }
}
