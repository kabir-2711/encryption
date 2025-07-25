package com.encryption.v1.controllers;

import java.util.Properties;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.encryption.exception.AppException;
import com.encryption.services.PropertyService;
import com.model.dto.AppProps;

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
@RequestMapping("v1/properties")
@AllArgsConstructor
public class PropertyController {

	/**
	 * {@code PropertyService} interface parameter
	 */
	private PropertyService propertyService;



	@GetMapping
	public ResponseEntity<Properties> properties() {
		return ResponseEntity.ok().body(propertyService.props());
	}

	@GetMapping("{key}")
	public ResponseEntity<Object> properties(@PathVariable String key) {
		return ResponseEntity.ok().body(propertyService.props(key));
	}

	@PostMapping
	public ResponseEntity<Properties> update(@RequestBody AppProps appProps) throws AppException {
		return ResponseEntity.ok().body(propertyService.update(appProps));
	}

	@PostMapping("refresh")
	public ResponseEntity<Properties> refresh() throws AppException {
		return ResponseEntity.ok().body(propertyService.refresh());
	}

}
