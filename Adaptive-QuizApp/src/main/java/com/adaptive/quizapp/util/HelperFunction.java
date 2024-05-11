package com.adaptive.quizapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.adaptive.quizapp.entity.Question;
import com.adaptive.quizapp.entity.Quiz;
import com.adaptive.quizapp.entity.Result;
import com.adaptive.quizapp.entity.User;
import com.adaptive.quizapp.repository.ResultRepository;
import com.adaptive.quizapp.service.QuestionService;
import com.adaptive.quizapp.service.QuizService;
import com.adaptive.quizapp.service.ResultService;
import com.adaptive.quizapp.service.ResultServiceImpl;
import com.adaptive.quizapp.service.UserService;

public class HelperFunction {
	
	public static boolean duplicateUserName(String username, UserService userService) {
		List<User> users = userService.getAllUsers();
		for (User user: users) {
			if (user.getName().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<Question> renderQuestions(List<Question> questions){
		for (Question question: questions) {
			question.setOptionA("A. " + question.getOptionA());
			question.setOptionB("B. " + question.getOptionB());
			question.setOptionC("C. " + question.getOptionC());
			question.setOptionD("D. " + question.getOptionD());
			question.setQuestion(questions.indexOf(question)+1 + ". " + question.getQuestion());
		}
		return questions;
	}
	
	public static int calculateScore(Result result, int quizId, QuestionService questionService) {
		List<Question> questions = questionService.getQuestionsByQuizId(quizId);
		int score = 0;
		for (int i=0; i<20; i++) {
			if (result.getAnswer()[i].equals(questions.get(i).getSolution())){
				score = score + 5;
			}
		}
		return score;
	}
	
	public static int[] calculateMetadata(Result result, int quizId, ResultService resultService) {
		List<Result> classResults = resultService.getResultsByQuizId(quizId);
		classResults.sort(new ResultComparator());
		int score = (int) result.getScore();
		int max = (int) classResults.get(0).getScore();			
		int above = classResults.indexOf(result);
		int below = classResults.size() - 1 - classResults.indexOf(result);
		int[] output = {score, max, above, below};
		return output;
	}
	
	public static ArrayList<String[]> collateHistory(List<Result> results, QuizService quizService, ResultService resultService, String c, String d) {
		ArrayList<String[]> history = new ArrayList<>();
		for (Result result: results) {
			Quiz quiz = quizService.getQuizById(result.getQuizId());
			List<Quiz> temp = filterQuiz(Arrays.asList(quiz), c, d);			
			if (temp.isEmpty()) 
				continue;
			String quizId = String.valueOf(quiz.getId());
			String quizName = quiz.getName();
			String category = quiz.getCategory();
			String difficulty = quiz.getDifficulty();
			List<Result> classResults = resultService.getResultsByQuizId(quiz.getId());
			classResults.sort(new ResultComparator());
			int[] metadata = calculateMetadata(result, quiz.getId(), resultService);
			String[] h = {quizId, quizName, category, difficulty, String.valueOf(metadata[0]), String.valueOf(metadata[1]), String.valueOf(metadata[2]), String.valueOf(metadata[3])};
			history.add(h);
		}
		return history;
	}
	
	public static List<Quiz> filterQuiz(List<Quiz> quizzes, String category, String difficulty){
		List<Quiz> output = new ArrayList<>();
		for (Quiz q: quizzes) {
			if (category.equals("") && difficulty.equals("")) {
				output.add(q);
			} else if (category.equals("") && q.getDifficulty().equals(difficulty)) {
				output.add(q);
			} else if (q.getCategory().equals(category) && difficulty.equals("")) {
				output.add(q);
			} else if (q.getCategory().equals(category) && q.getDifficulty().equals(difficulty)) {
				output.add(q);
			}
		}
		return output;
	}
	
	public static HashSet<String> getAllCategories(List<Quiz> quizzes){
		HashSet<String> categories = new HashSet<>();
		for (Quiz quiz: quizzes) {
			categories.add(quiz.getCategory());
		}
		return categories;
	}
	
	public static List<String[]> getQuizMetadata(List<Quiz> quizzes, ResultService resultService) {
		
		List<String[]> quizzesWithMetadata = new ArrayList<>(); // String[1+3+2]
		for (Quiz quiz: quizzes) {
			String quizId = String.valueOf(quiz.getId());
			String quizName = quiz.getName();
			String category = quiz.getCategory();
			String difficulty = quiz.getDifficulty();
			
			List<Result> results = resultService.getResultsByQuizId(quiz.getId());
			results.sort(new ResultComparator());
			String numOfAttendance = String.valueOf(results.size());
			String highestScore;
			if (results.size()==0) {
				highestScore = null;
			} else {
				highestScore = String.valueOf(results.get(0).getScore());				
			}
			String[] quizWithMetadata = {quizId, quizName, category, difficulty, numOfAttendance, highestScore};
			quizzesWithMetadata.add(quizWithMetadata);
		}
		return quizzesWithMetadata;
	}
	
	public static ArrayList<String[]> renderStudentResults(List<Result> results, UserService userService){
		results.sort(new ResultComparator());
		ArrayList<String[]> studentResults = new ArrayList<>();
		int ranking = 1;
		for (Result result: results) {
			String studentName = userService.getUserById(result.getStudentId()).getName();
			String score = String.valueOf(result.getScore());
			String[] studentResult = {studentName, score, String.valueOf(ranking)};
			ranking = ranking + 1;
			studentResults.add(studentResult);
		}
		return studentResults;
	}

}
