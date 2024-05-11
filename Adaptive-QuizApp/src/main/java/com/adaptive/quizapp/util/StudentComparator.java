package com.adaptive.quizapp.util;

import java.util.Comparator;
import java.util.List;

import com.adaptive.quizapp.entity.User;

public class StudentComparator {
	
	public static List<User> sortStudent(List<User> students, String sortBy, boolean sortAsc){
		if (sortBy.equals("studentName")) {
			if (sortAsc) {
				students.sort(new SortByNameAsc());
			} else {
				students.sort(new SortByNameDsc());
			}
		} else if (sortBy.equals("studentEmail")) {
			if (sortAsc) {
				students.sort(new SortByEmailAsc());
			} else {
				students.sort(new SortByEmailDsc());
			}
		}
		return students;
	}
	
	private static class SortByNameAsc implements Comparator<User>{
		@Override
		public int compare(User user1, User user2){
			return user1.getName().compareTo(user2.getName());
		}
	}
	
	private static class SortByNameDsc implements Comparator<User>{
		@Override
		public int compare(User user1, User user2){
			return user2.getName().compareTo(user1.getName());
		}
	}
	
	private static class SortByEmailAsc implements Comparator<User>{
		@Override
		public int compare(User user1, User user2){
			return user1.getEmail().compareTo(user2.getEmail());
		}
	}
	
	private static class SortByEmailDsc implements Comparator<User>{
		@Override
		public int compare(User user1, User user2){
			return user2.getEmail().compareTo(user1.getEmail());
		}
	}
	
}



