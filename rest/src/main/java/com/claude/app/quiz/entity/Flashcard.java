package com.claude.app.quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    @Column(name = "option_a", columnDefinition = "TEXT")
    private String optionA;
    
    @Column(name = "option_b", columnDefinition = "TEXT")
    private String optionB;
    
    @Column(name = "option_c", columnDefinition = "TEXT")
    private String optionC;
    
    @Column(name = "option_d", columnDefinition = "TEXT")
    private String optionD;
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id")
    @Getter @Setter
    private QuizSet quizSet;
}
