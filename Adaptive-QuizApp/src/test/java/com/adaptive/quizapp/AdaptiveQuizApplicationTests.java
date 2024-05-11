package com.adaptive.quizapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.adaptive.quizapp.entity.User;
import com.adaptive.quizapp.repository.UserRepository;
import com.adaptive.quizapp.service.UserService;

@SpringBootTest
class AdaptiveQuizApplicationTests {

	@Autowired
	private UserService service;

	@MockBean
	private UserRepository repository;

	@Test
	public void saveUserTest() {
		User user = new User("admin", "pass", "admin@gmail.com", "student");
		when(repository.save(user)).thenReturn(user);
		assertEquals(user, service.adminSaveUser(user));
	}

	@Test
	public void deleteUserTest() {
		User user = new User("admin", "pass", "admin@gmail.com", "student");
		service.deleteStudentById(user.getId());
		verify(repository, times(1)).delete(user);
	}


}
