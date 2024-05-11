package com.adaptive.quizapp.util;

import java.util.Comparator;
import java.util.List;

import com.adaptive.quizapp.entity.Result;
import com.adaptive.quizapp.service.QuizService;
import com.adaptive.quizapp.service.ResultService;

public class ResultComparator implements Comparator<Result> {
		
	@Override
	public int compare(Result r1, Result r2){
		if (r1.getScore() < r2.getScore()) {
			return 1;
		} else {
			return -1;
		}
	}
}
