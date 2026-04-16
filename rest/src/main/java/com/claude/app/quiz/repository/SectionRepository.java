package com.claude.app.quiz.repository;

import com.claude.app.quiz.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    List<Section> findByChapterId(Integer chapterId);
}
