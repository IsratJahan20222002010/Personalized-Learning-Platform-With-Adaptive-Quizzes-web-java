package com.adaptive.quizapp.service;

import java.util.List;

import com.adaptive.quizapp.entity.Result;

public interface ResultService {
	
	public Result saveResult(Result result);
	public List<Result> getResultsByStudentId(int id);
	public List<Result> getResultsByQuizId(int id);
	public Result getResultByQuizIdAndStudentId(int quizId, int studentId);
	public Result getResultById(int id);
	public void deleteResultById(int id);

}
