package com.encryption.v1.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.encryption.exception.AppException;
import com.encryption.services.ChannelDetailService;
import com.model.dto.Channel;
import com.model.entity.ChannelDetails;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * The {@code ChannelController} class handles HTTP requests related to user
 * management operations
 * 
 * <p>
 * This class is a Spring Boot REST controller, which maps HTTP requests to
 * handler methods of REST controllers.
 * </p>
 * 
 * It provides end points to register a user
 * ({@code /enc-service/channels/register}) for {@literal POST Requests} the
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
@RequestMapping("v1/channels")
@AllArgsConstructor
public class ChannelController {

	/**
	 * {@link ChannelDetailService} interface parameter
	 */
	private ChannelDetailService channelDetailService;

	/**
	 * Registers the channel with end point ({@code /encrypt})
	 * 
	 * @param channel Channel details to be registered in the system (in JSON :
	 *                {@code {"channelId": "channel id","publicKey": "public key
	 *                value"}})
	 * @return Registration success response in JSON
	 *         ({@code {"timestamp": "yyyy-MM-dd
	 *         HH:mm:ss.SSSSSS>", "code": "code", "channelId": "channel id", "publicKey":
	 *         "public key value"}})
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@PostMapping("register")
	public ResponseEntity<Channel> register(@Valid @RequestBody Channel channel) throws AppException {
		return ResponseEntity.status(HttpStatus.CREATED).body(channelDetailService.register(channel));
	}

	/**
	 * Method to get list of registered channel in the system
	 * 
	 * @return List of {@link ChannelDetails} containing list of registered channel
	 */
	@GetMapping
	public ResponseEntity<List<ChannelDetails>> channels() {
		return ResponseEntity.ok().body(channelDetailService.channelDetails());
	}

	/**
	 * Method to get list of registered channel in the system
	 * 
	 * @return List of {@link ChannelDetails} containing list of registered channel
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@GetMapping("/{channelId}")
	public ResponseEntity<ChannelDetails> channels(@PathVariable(required = false) String channelId)
			throws AppException {
		return ResponseEntity.ok().body(channelDetailService.channelDetails(channelId));
	}

}
