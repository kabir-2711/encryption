package com.encryption.v1.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.encryption.exception.AppException;
import com.encryption.services.EncryptionService;
import com.model.dto.Encryption;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * The {@code EncryptionController} class handles HTTP requests related to
 * encryption and decryption operations
 * 
 * <p>
 * This class is a Spring Boot REST controller, which maps HTTP requests to
 * handler methods of REST controllers.
 * </p>
 * 
 * It provides end points to encrypt ({@code /enc-service/encrypt}) and decrypt
 * ({@code /enc-service/decrypt}) for {@literal POST Requests} the pay-load with
 * annotation {@code} @RequestBody}
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
@RequestMapping("v1")
@AllArgsConstructor
public class EncryptionController {

	/**
	 * {@link EncryptionService} interface parameter
	 */
	private EncryptionService encService;

	/**
	 * Encrypts the input data for end point ({@code /encrypt})
	 * 
	 * @param plainBody Plain input data that is to be encrypted (in JSON :
	 *                  {@code {"data": "Plain data"}})
	 * @return Encrypted response in JSON ({@code {"timestamp": "yyyy-MM-dd
	 *         HH:mm:ss.SSSSSS>", "code": "code", "data": "Encrypted data"}})
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@PostMapping("encrypt")
	public ResponseEntity<Encryption> encrypt(@Valid @RequestBody Encryption plainBody) throws AppException {
		return ResponseEntity.ok().body(encService.encrypt(plainBody));
	}

	/**
	 * Decrypts the input data for end point ({@code /decrypt})
	 * 
	 * @param encryptedBody Encrypted input data that is to be decrypted (in JSON :
	 *                      {@code {"data": "Encrypted data"}})
	 * @return Decrypted response in JSON ({@code {"timestamp": "yyyy-MM-dd
	 *         HH:mm:ss.SSSSSS", "code": "code", "data": "Decrypted data"}})
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@PostMapping("decrypt")
	public ResponseEntity<Encryption> decrypt(@Valid @RequestBody Encryption encryptedBody) throws AppException {
		return ResponseEntity.ok().body(encService.decrypt(encryptedBody));
	}
}
