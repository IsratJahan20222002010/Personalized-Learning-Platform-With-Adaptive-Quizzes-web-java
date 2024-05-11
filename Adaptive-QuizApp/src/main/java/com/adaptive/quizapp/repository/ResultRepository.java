package com.adaptive.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adaptive.quizapp.entity.Result;
import com.adaptive.quizapp.entity.User;

public interface ResultRepository extends JpaRepository<Result, Integer>{
	
	List<Result> findByStudentId(int id);
	List<Result> findByQuizId(int id);
	Result findByQuizIdAndStudentId(int quizId, int studentId);

}
