package com.adaptive.quizapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adaptive.quizapp.entity.Quiz;
import com.adaptive.quizapp.repository.QuizRepository;

@Service
public class QuizServiceImpl implements QuizService{

	@Autowired
	private QuizRepository quizRepository;
	
	@Override
	public List<Quiz> getAllQuizzes() {
		return quizRepository.findAll();
	}

	@Override
	public Quiz getQuizById(int id) {
		return quizRepository.findById(id).get();
	}

	@Override
	public Quiz saveQuiz(Quiz quiz) {
		return quizRepository.save(quiz);
	}

	@Override
	public void deleteQuizById(int id) {
		quizRepository.deleteById(id);
		
	}

}
