package com.encryption.v1.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.encryption.exception.AppException;
import com.encryption.services.UserDetailService;
import com.model.dto.UserDto;
import com.model.entity.Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * The {@code UserController} class handles HTTP requests related to user
 * management operations
 * 
 * <p>
 * This class is a Spring Boot REST controller, which maps HTTP requests to
 * handler methods of REST controllers.
 * </p>
 * 
 * It provides end points to register a user
 * ({@code /enc-service/user/register}) for {@literal POST Requests} the
 * pay-load with annotation {@code} @RequestBody}
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestController.html">
 *      RestController</a>
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html">
 *      RequestMapping</a>
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html">
 *      RequestBody</a>
 * @author Kabir Akware
 */
@RestController
@RequestMapping("v1/users")
@AllArgsConstructor
public class UserController {

	/**
	 * {@link UserDetailService} interface parameter
	 */
	private UserDetailService userService;

	/**
	 * Registers the user with end point ({@code /register})
	 * 
	 * @param user User details of the user to be registered (in JSON :
	 *             {@code {"username": "userId","password": "password"}})
	 * @return Registration success response in JSON
	 *         ({@code {"timestamp": "yyyy-MM-dd
	 *         HH:mm:ss.SSSSSS>", "code": "code", "username": "user8", "password":
	 *         "$2a$12$9jwvhNTLG8envM74hQxoaeMCPgSEZ6VUKUlOaC8zqoYKMX4BTfUtm",
	 *         "authorities": "USER"}})
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@Operation(summary = "Register a user in the system")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User registered", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
			@ApiResponse(responseCode = "400", description = "User not available", content = @Content) })
	@PostMapping("register")
	public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto user) throws AppException {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
	}

	@GetMapping
	public ResponseEntity<List<Users>> user() throws AppException {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.getUsers());
	}

}
