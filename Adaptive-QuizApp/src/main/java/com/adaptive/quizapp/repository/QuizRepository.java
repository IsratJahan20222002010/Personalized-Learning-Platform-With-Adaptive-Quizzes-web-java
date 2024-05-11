package com.adaptive.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adaptive.quizapp.entity.Quiz;
import com.adaptive.quizapp.entity.User;

public interface QuizRepository extends JpaRepository<Quiz, Integer>{

}
