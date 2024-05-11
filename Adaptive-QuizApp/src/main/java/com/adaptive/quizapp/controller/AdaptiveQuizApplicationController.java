package com.adaptive.quizapp.controller;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adaptive.quizapp.config.CustomUserDetails;
import com.adaptive.quizapp.config.LoginSuccessHandler;
import com.adaptive.quizapp.entity.Question;
import com.adaptive.quizapp.entity.Quiz;
import com.adaptive.quizapp.entity.Result;
import com.adaptive.quizapp.entity.User;
import com.adaptive.quizapp.service.QuestionService;
import com.adaptive.quizapp.service.QuizService;
import com.adaptive.quizapp.service.ResultService;
import com.adaptive.quizapp.service.UserService;
import com.adaptive.quizapp.util.HelperFunction;
import com.adaptive.quizapp.util.StudentComparator;

@Controller
public class AdaptiveQuizApplicationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private QuizService quizService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ResultService resultService;
	
	@RequestMapping("/")
	public String homePage(Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		
			if (user.getRole().equals("admin"))
			{
				return "dashboard_admin";
			}
			else {
				return "dashboard_student";
			}
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/sign_up")
	public String signUp(Model model) {
		model.addAttribute("user", new User());
		return "sign_up";
	}
	
	@PostMapping("/sign_up")
	public String saveUser(@ModelAttribute("user") User user) {
		if (HelperFunction.duplicateUserName(user.getName(), userService)) {
			return "redirect:/sign_up?duplicate_username"; 
		} else if (!user.getEmail().contains("@")) {
			return "redirect:/sign_up?invalid_email";
		}
		userService.save(user);
		return "redirect:/sign_up?success";
	}
	
	@RequestMapping("/dashboard_admin")
	public String dashboardOfAdmin() {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		
		if (user.getRole().equals("admin"))
		{
			return "dashboard_admin";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/dashboard_student")
	public String dashboardOfStudent(Model model) {
		return "dashboard_student";
	}
	
	@RequestMapping("/select_quiz")
	public String selectQuiz(@RequestParam("category") String category, @RequestParam("difficulty") String difficulty, Model model) {
			List<Quiz> quizzes = HelperFunction.filterQuiz(quizService.getAllQuizzes(), category, difficulty);
			HashSet<String> categories = HelperFunction.getAllCategories(quizService.getAllQuizzes());
			model.addAttribute("categories", categories);
			model.addAttribute("quiz", new Quiz("",category, difficulty));
			model.addAttribute("quizzes", quizzes);
			return "select_quiz";
	}
	
	@RequestMapping("/attempt_quiz/{quizId}")
	public String attemptQuiz(@PathVariable Integer quizId, Model model) {
			Quiz quiz = quizService.getQuizById(quizId);
			List<Question> questions = HelperFunction.renderQuestions(questionService.getQuestionsByQuizId(quizId));
			// database check if the quiz have been attempted before
			Result result = resultService.getResultByQuizIdAndStudentId(quizId, userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
			if (result==null) 
				result = new Result();
			model.addAttribute("resultId", result.getId());
			model.addAttribute("result", new Result());
			model.addAttribute("quiz", quiz);
			model.addAttribute("questions", questions);
			return "attempt_quiz";
	}
	
	@PostMapping("/submit_quiz/{quizId}/{resultId}")
	public String submitQuiz(@PathVariable int resultId, @ModelAttribute("results") Result result, @PathVariable int quizId, Model model) {
			if (resultId != 0)  // == 0 means creating new result, should update in saveResult. Otherwise locate the old result by resultId  
				result.setId(resultId);
			int score = HelperFunction.calculateScore(result, quizId, questionService);
			result.setStudentId(userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
			result.setQuizId(quizId);
			result.setScore(score);
			resultService.saveResult(result);
			return "redirect:/view_result/" + quizId;
	}
	
	@RequestMapping("/view_result/{quizId}")
	public String viewResult(@PathVariable int quizId, Model model) {
			Result result = resultService.getResultByQuizIdAndStudentId(quizId, userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
			int[] metadata = HelperFunction.calculateMetadata(result, quizId, resultService);
			model.addAttribute("output", metadata);
			return "view_result";
	}
	
	@RequestMapping("/view_history")
	public String viewHistory(@RequestParam("category") String c, @RequestParam("difficulty") String d, Model model) {
			List<Result> results = resultService.getResultsByStudentId(userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
			ArrayList<String[]> history = HelperFunction.collateHistory(results, quizService, resultService, c, d); // String[1+3+4]		
			model.addAttribute("quiz", new Quiz("", c, d));
			model.addAttribute("categories", HelperFunction.getAllCategories(quizService.getAllQuizzes()));
			model.addAttribute("history", history);
			return "view_history";
	}
	
	@RequestMapping("/view_answer/{quizId}")
	public String viewAnswer(@PathVariable Integer quizId, Model model) {
			List<Question> questions = HelperFunction.renderQuestions(questionService.getQuestionsByQuizId(quizId));
			String[] answers = resultService.getResultByQuizIdAndStudentId(quizId, userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName()).getId()).getAnswer();
			model.addAttribute("questions", questions);
			model.addAttribute("answers", answers);
			model.addAttribute("quiz", quizService.getQuizById(quizId));
			return "view_answer";
	}
	
	@RequestMapping("/manage_quiz")
	public String manageQuiz(@RequestParam("category") String category, @RequestParam("difficulty") String difficulty, Model model) {	
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin"))
		{
			List<Quiz> quizzes = HelperFunction.filterQuiz(quizService.getAllQuizzes(), category, difficulty);
			model.addAttribute("categories", HelperFunction.getAllCategories(quizService.getAllQuizzes()));
			model.addAttribute("quiz", new Quiz("", category, difficulty)); // for filtering, not form
			model.addAttribute("quizzes", quizzes);
			return "manage_quiz";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
		
		
	}
	
	@RequestMapping("/create_quiz")
	public String createQuiz(Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			model.addAttribute("quiz", new Quiz());
			return "create_quiz";
		}
		else {
		return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@PostMapping("save_quiz")
	public String saveQuiz(@ModelAttribute("quiz") Quiz quiz, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			quizService.saveQuiz(quiz);
			for (int i=0; i<20; i++) {
				Question question = new Question();
				question.setQuizId(quiz.getId());			
				questionService.saveQuestion(question);
			}
			return "redirect:/create_quiz_questions/" + quiz.getId();
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/create_quiz_questions/{quizId}")
	public String createQuizQuestions(@PathVariable int quizId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			List<Question> questions = questionService.getQuestionsByQuizId(quizId);
			model.addAttribute("questions", questions);
			model.addAttribute("quizId", quizId);
			return "create_quiz_questions";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/edit_question/{quizId}/{questionId}")
	public String editQuestion(@PathVariable int quizId, @PathVariable int questionId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			model.addAttribute("question", questionService.getQuestionById(questionId));
			model.addAttribute("quizId", quizId);
			model.addAttribute("questionId", questionId);
			return "edit_question";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@PostMapping("/save_question/{quizId}/{questionId}")
	public String saveQuestion(@ModelAttribute("q") Question question, @PathVariable int quizId, @PathVariable int questionId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			question.setId(questionId);
			question.setQuizId(quizId);
			questionService.saveQuestion(question);
			return "redirect:/create_quiz_questions/" + question.getQuizId();
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/edit_quiz/{quizId}")
	public String editQuiz(@PathVariable int quizId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			model.addAttribute("quizId", quizId); // instead of using quiz.id in html because quiz.id will be the overwritten one (0)
			model.addAttribute("quiz", quizService.getQuizById(quizId)); // instead of new quiz because we want to have an auto fill in form
			return "edit_quiz";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@PostMapping("update_quiz/{quizId}")
	public String updateQuiz(@ModelAttribute("quiz") Quiz quiz, @PathVariable int quizId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			quiz.setId(quizId);
			quizService.saveQuiz(quiz);
			return "redirect:/create_quiz_questions/" + quiz.getId();
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/delete_quiz/{quizId}")
	public String deleteQuiz(@PathVariable int quizId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			quizService.deleteQuizById(quizId);
			for (Result result: resultService.getResultsByQuizId(quizId))
				resultService.deleteResultById(result.getId());
			for (Question question: questionService.getQuestionsByQuizId(quizId))
				questionService.deleteQuestionById(question.getId()); 	
		return "redirect:/dashboard_admin?deletion_success"; 
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/filter_quiz")
	public String filterQuiz(@RequestParam("category") String category, @RequestParam("difficulty") String difficulty, Model model) {	
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			List<Quiz> quizzes = HelperFunction.filterQuiz(quizService.getAllQuizzes(), category, difficulty);
			List<String[]> quizzesWithMetadata = HelperFunction.getQuizMetadata(quizzes, resultService);
			model.addAttribute("categories", HelperFunction.getAllCategories(quizzes));
			model.addAttribute("quiz", new Quiz("", category, difficulty));
			model.addAttribute("quizzesWithMetadata", quizzesWithMetadata);
			return "filter_quiz";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/view_student_result/{quizId}")
	public String viewStudentResult(@PathVariable int quizId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			List<Result> results = resultService.getResultsByQuizId(quizId);
			ArrayList<String[]> studentResults = HelperFunction.renderStudentResults(results, userService); // String[3]
			model.addAttribute("studentResults", studentResults);
			model.addAttribute("quizName", quizService.getQuizById(quizId).getName());
			return "view_student_result";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/manage_student")
	public String manageStudent(@RequestParam String sortBy, @RequestParam boolean sortAsc, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			List<User> students = userService.getAllUsers();
			students = StudentComparator.sortStudent(students, sortBy, sortAsc);
			model.addAttribute("sortAsc", !sortAsc);
			model.addAttribute("students", students);
			return "manage_student";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/edit_student/{studentId}")
	public String editStudent(@PathVariable int studentId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			User student = userService.getUserById(studentId);
			model.addAttribute("student", student);
			model.addAttribute("studentId", studentId);
			return "edit_student";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@PostMapping("/update_student/{studentId}")
	public String updateStudent(@PathVariable int studentId, @ModelAttribute("student") User student, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			User existingStudent = userService.getUserById(studentId);
			existingStudent.setEmail(student.getEmail());
			existingStudent.setName(student.getName());
			existingStudent.setRole(student.getRole());
			userService.adminSaveUser(existingStudent);
			return "redirect:/manage_student?sortBy=studentName&sortAsc=true";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
	@RequestMapping("/delete_student/{studentId}")
	public String deleteStudent(@PathVariable int studentId, Model model) {
		User user = userService.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
		if (user.getRole().equals("admin")) {
			userService.deleteStudentById(studentId);
			for (Result result: resultService.getResultsByStudentId(studentId))
				resultService.deleteResultById(result.getId());
			return "redirect:/manage_student?sortBy=studentName&sortAsc=true";
		}
		else {
			return "redirect:/dashboard_student?denied_access";
		}
	}
	
}
