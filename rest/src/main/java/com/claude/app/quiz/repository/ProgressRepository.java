package com.claude.app.quiz.repository;

import com.claude.app.quiz.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findAll();

    Optional<Progress> findBySectionId(String sectionId);
}
