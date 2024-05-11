package com.adaptive.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adaptive.quizapp.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
	
	List<Question> findByQuizId(int id);

}
