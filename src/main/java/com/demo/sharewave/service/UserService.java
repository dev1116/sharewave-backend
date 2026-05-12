package com.demo.sharewave.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.demo.sharewave.constant.AuthConstant;
import com.demo.sharewave.entity.User;
import com.demo.sharewave.object.AuthRequest;
import com.demo.sharewave.object.LoginRequest;
import com.demo.sharewave.object.RegisterRequest;
import com.demo.sharewave.repository.UserRepository;
import com.demo.sharewave.util.JwtUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

//	 @Override
	public ResponseEntity<?> registerUser(RegisterRequest req) {
		// Encrypt the password

		Optional<User> existingUser = userRepo.findByEmail(req.getEmail());

		if (existingUser.isPresent()) {
			return ResponseEntity.badRequest().body("Email already exists!");
		}

		User user = new User();

		user.setName(req.getName());
		user.setEmail(req.getEmail());
		user.setCreatedDate(LocalDateTime.now());
		user.setPasswordExpiryDate(LocalDateTime.now().plusDays(90));

		// password encode
		user.setPassword(passwordEncoder.encode(req.getPassword()));

		userRepo.save(user);

		String token = jwtUtil.generateToken(user.getEmail());

		return ResponseEntity.ok(Map.of("token", token));
	}
	
	@Transactional
	public ResponseEntity<?> authenticateUser(LoginRequest req) {

	    Optional<User> existingUser =
	            userRepo.findByEmail(req.getEmail());

	    if (existingUser.isEmpty()) {

	        return ResponseEntity
	                .badRequest()
	                .body("User does not exist. Please register.");
	    }

	    User userData = existingUser.get();

	    boolean isPasswordCorrect =
	            passwordEncoder.matches(
	                    req.getPassword(),
	                    userData.getPassword()
	            );

	    if (!isPasswordCorrect) {

	        return ResponseEntity
	                .badRequest()
	                .body("Invalid password.");
	    }

	    String token =
	            jwtUtil.generateToken(userData.getEmail());

	    return ResponseEntity.ok(
	            Map.of(
	                    "message", "Login successful",
	                    "token", token
	            )
	    );
	}
	
	
	private void getUserAuthResponse(LoginRequest userVO ,User userData) {

		String token = null;
		try {
			token = getAuthToken(userVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public String getAuthToken( LoginRequest dto) throws Exception {
		AuthRequest authRequest = new AuthRequest();
		authRequest.setUserName(dto.getEmail());
		authRequest.setPassword(AuthConstant.AUTH_PSW);
		return generateToken(authRequest).getBody();
	}
	
	public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
		// log.info("Inside generateToken 22*************");
		String token = saveLoginDetails(authRequest);
		if (StringUtils.isNotBlank(token)) {
			return new ResponseEntity<>(token, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(token, HttpStatus.NOT_FOUND);
		}
	}
	
	public String saveLoginDetails(AuthRequest authRequest) {
		String token = null;
		Optional<User> userdetailsOptional = userRepo.findByEmail(authRequest.getUserName());
		if (userdetailsOptional.isPresent()) {
			User userDetails = userdetailsOptional.get();
			token = jwtUtil.generateToken(authRequest.getUserName().toLowerCase());
			

		}
		return token;
	}
}
