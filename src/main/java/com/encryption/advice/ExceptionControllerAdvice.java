package com.encryption.advice;

import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.encryption.exception.AppException;
import com.encryption.exception.UserNameNotFound;
import com.encryption.utility.CommonUtility;
import com.model.dto.AppError;
import com.model.enums.Codes;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;

/**
 * ExceptionControllerAdvice handles exceptions globally across the entire
 * application.
 * 
 * <p>
 * This class uses {@code @RestControllerAdvice} to catch specific exceptions
 * and return appropriate responses, providing consistent error handling across
 * all controllers.
 * 
 * <p>
 * In particular, it handles validation errors thrown when method arguments fail
 * validation and custom exception thrown in the system.
 * 
 * <p>
 * It returns a {@link ResponseEntity} by wrapping the error in a user readable
 * way in the {@link AppError} object
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RestControllerAdvice.html">
 *      RestControllerAdvice</a>
 * @author Kabir Akware
 */
@AllArgsConstructor
@RestControllerAdvice
public class ExceptionControllerAdvice {

	/**
	 * General error message String
	 */
	private static final String GENERAL_ERROR_MESSAGE = "Something went wrong!! Kindly contact the administrator";

	/**
	 * Handles {@link MethodArgumentNotValidException} thrown when validation on a
	 * request field fails.
	 * 
	 * <p>
	 * This method catches validation errors for null fields in a request body which
	 * are marked as not null. It extracts error messages for each invalid field and
	 * returns them in the {@link ResponseEntity} response wrapped in
	 * {@link AppError} class.
	 * </p>
	 * 
	 * @param e Exception thrown when validation fails.
	 * @return A {@link ResponseEntity} containing a map where the keys are field
	 *         names and the values are error messages wrapped in the
	 *         {@link AppError} class
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<AppError> nullCheck(MethodArgumentNotValidException e) {
		return ResponseEntity.badRequest()
				.body(AppError
						.getInstance(
								CommonUtility.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.ERR07, String
										.join(", ",
												e.getBindingResult().getFieldErrors().stream()
														.map(FieldError::getDefaultMessage).toList())
										.replace("\"", "'"),
								"Mandatory parameter does not satisfy the requirement. Please check the request"));
	}

	/**
	 * Handles {@link HttpMessageNotReadableException} thrown when the JSON request
	 * validation fails.
	 * 
	 * <p>
	 * This method catches validation errors for invalid JSON formats in a request
	 * body. It returns the error message for the corresponding invalid field and
	 * returns them in the {@link ResponseEntity} response wrapped in
	 * {@link AppError} class.
	 * </p>
	 * 
	 * @param e Exception thrown when the JSON validation fails
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<AppError> jsonCheck(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest()
				.body(AppError.getInstance(CommonUtility.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.ERR07,
						e.getMostSpecificCause().toString().replace("\"", "'"), "Pass valid JSON String"));
	}

	/**
	 * Handles {@link ConstraintViolationException} thrown when the database
	 * operation fails.
	 * 
	 * <p>
	 * This method catches constraint violated while performing insert operations in
	 * the data base. It returns the error message for the corresponding error and
	 * returns them in the {@link ResponseEntity} response wrapped in
	 * {@link AppError} class.
	 * </p>
	 * 
	 * @param e Exception thrown when the constraints defined in the system for
	 *          insert operation does not match the constraints defined in the
	 *          database
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<AppError> entityCheck(ConstraintViolationException e) {
		return ResponseEntity.internalServerError().body(AppError.getInstance(CommonUtility.getRefNo(),
				CommonUtility.getCurrentTimeStamp(), Codes.ERR01, checkNullMessage(e), GENERAL_ERROR_MESSAGE));
	}

	/**
	 * Handles {@link DataIntegrityViolationException} thrown when the database
	 * operation fails.
	 * 
	 * <p>
	 * This method catches data integrity violated while performing insert
	 * operations in the data base. It returns the error message for the
	 * corresponding error and returns them in the {@link ResponseEntity} response
	 * wrapped in {@link AppError} class.
	 * </p>
	 * 
	 * @param e Exception thrown when the insert operation does not adhere to the
	 *          constraints defined in the database
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<AppError> dataCheck(DataIntegrityViolationException e) {
		return ResponseEntity.internalServerError().body(AppError.getInstance(CommonUtility.getRefNo(),
				CommonUtility.getCurrentTimeStamp(), Codes.ERR01, checkNullMessage(e), GENERAL_ERROR_MESSAGE));
	}

	/**
	 * Handles {@link UsernameNotFoundException} thrown when the credentials
	 * provided by the user is not found in the database.
	 * 
	 * <p>
	 * This method catches user name not found while checking the credentials
	 * provided. It returns the error message for the corresponding error and
	 * returns them in the {@link ResponseEntity} response wrapped in
	 * {@link AppError} class.
	 * </p>
	 * 
	 * @param e UserNameNotFound Thrown when the credentials provided by the user is
	 *          not found in the database
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(UserNameNotFound.class)
	public ResponseEntity<AppError> dataCheck(UserNameNotFound e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(AppError.getInstance(CommonUtility.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.ERR07,
						checkNullMessage(e), "Kindly contact the administrator to register this user"));
	}

	/**
	 * Handles {@link AppException} thrown when the business logic fails.
	 * 
	 * <p>
	 * This method catches the business failure in the system and returns the error
	 * message for the corresponding error and returns them in the
	 * {@link ResponseEntity} response wrapped in {@link AppError} class.
	 * </p>
	 * 
	 * @param e {@link AppException} Thrown in the application where business logic
	 *          fails
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(AppException.class)
	public ResponseEntity<AppError> customExceptionCheck(AppException e) {
		return ResponseEntity.status(e.getStatus()).body(AppError.getInstance(CommonUtility.getRefNo(),
				CommonUtility.getCurrentTimeStamp(), e.getCode(), checkNullMessage(e), e.getDescription()));
	}

	/**
	 * Handles {@link RuntimeException} thrown when the business logic fails.
	 * 
	 * <p>
	 * This method catches the run-time errors in the system and returns them in the
	 * {@link ResponseEntity} response wrapped in {@link AppError} class.
	 * </p>
	 * 
	 * @param e {@link RuntimeException} Thrown when an unchecked exception is
	 *          thrown by the system
	 * @return A {@link ResponseEntity} containing the error description wrapped in
	 *         the {@link AppError} class
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<AppError> runtimeExceptionCheck(RuntimeException e) {
		return ResponseEntity.internalServerError().body(AppError.getInstance(CommonUtility.getRefNo(),
				CommonUtility.getCurrentTimeStamp(), Codes.ERR01, checkNullMessage(e), GENERAL_ERROR_MESSAGE));
	}

	/**
	 * Method to check and get the error message
	 * 
	 * @param e {@link Throwable} object
	 * @return Error message if present or {@code null} value
	 */
	private String checkNullMessage(Throwable e) {
		return Objects.nonNull(e.getMessage()) ? e.getMessage().replace("\"", "'") : e.getMessage();
	}
}
