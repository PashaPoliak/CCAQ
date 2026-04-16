package com.claude.app.quiz.service;

import com.claude.app.quiz.entity.Chapter;
import com.claude.app.quiz.entity.Progress;
import com.claude.app.quiz.entity.Section;
import com.claude.app.quiz.repository.ChapterRepository;
import com.claude.app.quiz.repository.ProgressRepository;
import com.claude.app.quiz.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final ChapterRepository chapterRepository;
    private final SectionRepository sectionRepository;
    private final ProgressRepository progressRepository;

    public QuizService(ChapterRepository chapterRepository, SectionRepository sectionRepository, ProgressRepository progressRepository) {
        this.chapterRepository = chapterRepository;
        this.sectionRepository = sectionRepository;
        this.progressRepository = progressRepository;
    }

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    public List<Section> getSectionsByChapter(Integer chapterId) {
        return sectionRepository.findByChapterId(chapterId);
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    @Transactional
    public Progress updateSectionProgress(String sectionId, Boolean completed) {
        Optional<Progress> existing = progressRepository.findBySectionId(sectionId);
        Progress progress;
        if (existing.isPresent()) {
            progress = existing.get();
        } else {
            progress = new Progress();
            progress.setSectionId(sectionId);
        }
        progress.setCompleted(completed);
        return progressRepository.save(progress);
    }

    public Optional<Progress> getSectionProgress(String sectionId) {
        return progressRepository.findBySectionId(sectionId);
    }
}
