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
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String a;
    
    @Column(name = "option_b", columnDefinition = "TEXT")
    private String b;
    
    @Column(name = "option_c", columnDefinition = "TEXT")
    private String c;
    
    @Column(name = "option_d", columnDefinition = "TEXT")
    private String d;
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id")
    @Getter @Setter
    @JsonIgnore
    private QuizSet quizSet;
}
