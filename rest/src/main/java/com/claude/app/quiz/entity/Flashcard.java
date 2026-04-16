package com.claude.app.quiz.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "flashcards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "question", columnDefinition = "TEXT")
    private String question;
    
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "category")
    private String category;

}