package com.adaptive.quizapp.service;

import java.util.List;

import com.adaptive.quizapp.entity.Question;

public interface QuestionService {
	
	public List<Question> getQuestionsByQuizId(int id);
	public Question saveQuestion(Question question);
	public Question getQuestionById(int id);
	public void deleteQuestionById(int id);

}
