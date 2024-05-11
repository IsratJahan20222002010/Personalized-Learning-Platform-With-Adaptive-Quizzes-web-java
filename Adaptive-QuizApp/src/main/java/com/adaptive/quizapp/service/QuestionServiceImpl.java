package com.adaptive.quizapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adaptive.quizapp.entity.Question;
import com.adaptive.quizapp.repository.QuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService{
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Override
	public List<Question> getQuestionsByQuizId(int id) {
		return questionRepository.findByQuizId(id);
	}

	@Override
	public Question saveQuestion(Question question) {
		return questionRepository.save(question);
	}

	@Override
	public Question getQuestionById(int id) {
		return questionRepository.findById(id).get();
	}

	@Override
	public void deleteQuestionById(int id) {
		questionRepository.deleteById(id);		
	}

}
