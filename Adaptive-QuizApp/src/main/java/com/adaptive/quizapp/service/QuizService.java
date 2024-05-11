package com.adaptive.quizapp.service;

import java.util.List;

import com.adaptive.quizapp.entity.Quiz;

public interface QuizService {
	
	public List<Quiz> getAllQuizzes();
	public Quiz getQuizById(int id);
	public Quiz saveQuiz(Quiz quiz);
	public void deleteQuizById(int id);

}
