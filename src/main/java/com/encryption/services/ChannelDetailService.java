package com.encryption.services;

import java.util.List;

import com.encryption.exception.AppException;
import com.model.dto.Channel;
import com.model.entity.ChannelDetails;

/**
 * This Interface defines the contract for {@code ChannelDetailService} service.
 * The service provides functionalities for storing and managing channel details
 * including channel id and public keys.
 * 
 * <p>
 * The implementation of this service can be used to fetch public keys mapped to
 * a channel id to be used in the application.
 * </p>
 * 
 * 
 * @author Kabir Akware
 */
public interface ChannelDetailService {

	/**
	 * Declaration of {@code register(Channel channel)} to register a channel in the
	 * system
	 * 
	 * @param channel {@link Channel} object
	 * @return {@code Channel} object with response details
	 * @throws AppException Thrown when a custom exception occurs
	 */
	Channel register(Channel channel) throws AppException;

	/**
	 * Declaration of {@code channelDetails} to get a list of {@link ChannelDetails}
	 * object containing channel details
	 * 
	 * @return List of {@link ChannelDetails} object
	 */
	List<ChannelDetails> channelDetails();

	/**
	 * Declaration of {@code channelDetails} to get a list of {@link ChannelDetails}
	 * object containing channel details
	 * @param channelId Channel id
	 * 
	 * @return List of {@link ChannelDetails} object
	 * @throws AppException Thrown when a custom exception occurs
	 */
	ChannelDetails channelDetails(String channelId) throws AppException;
}
