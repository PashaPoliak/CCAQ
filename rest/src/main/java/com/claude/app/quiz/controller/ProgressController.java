package com.claude.app.quiz.controller;

import com.claude.app.quiz.entity.Progress;
import com.claude.app.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final QuizService quizService;

    public ProgressController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public List<Progress> getAllProgress() {
        return quizService.getAllProgress();
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<Progress> getSectionProgress(@PathVariable String sectionId) {
        return quizService.getSectionProgress(sectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{sectionId}")
    public Progress updateSectionProgress(@PathVariable String sectionId, @RequestBody ProgressRequest request) {
        return quizService.updateSectionProgress(sectionId, request.completed());
    }

    public record ProgressRequest(Boolean completed) {
    }
}
