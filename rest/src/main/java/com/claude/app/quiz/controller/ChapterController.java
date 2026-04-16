package com.claude.app.quiz.controller;

import com.claude.app.quiz.entity.Chapter;
import com.claude.app.quiz.entity.Section;
import com.claude.app.quiz.service.QuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {
    private final QuizService quizService;

    public ChapterController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public List<Chapter> getAllChapters() {
        return quizService.getAllChapters();
    }

    @GetMapping("/{id}/sections")
    public List<Section> getSectionsByChapter(@PathVariable Integer id) {
        return quizService.getSectionsByChapter(id);
    }
}
