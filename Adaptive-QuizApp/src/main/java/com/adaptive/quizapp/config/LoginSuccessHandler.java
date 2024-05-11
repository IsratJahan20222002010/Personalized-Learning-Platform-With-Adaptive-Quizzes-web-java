package com.adaptive.quizapp.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.adaptive.quizapp.service.UserService;
import com.adaptive.quizapp.service.UserServiceImpl;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
 
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String redirectURL = request.getContextPath();
        if (userDetails.getRole().equals("admin")) {
            redirectURL = "dashboard_admin";
        } else if (userDetails.getRole().equals("student")) {
            redirectURL = "dashboard_student";
        } 
        response.sendRedirect(redirectURL);
         
    }

}
