package com.encryption.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.encryption.exception.AppException;
import com.encryption.repo.ChannelDetailsRepo;
import com.encryption.services.ChannelDetailService;
import com.encryption.utility.CommonUtility;
import com.encryption.utility.EncryptionUtility;
import com.model.dto.Channel;
import com.model.entity.ChannelDetails;
import com.model.enums.Codes;

import lombok.AllArgsConstructor;

/**
 * This class provides the implementation of {@link ChannelDetailService}
 * interface, offering the structural logic for maintaining channel details in
 * the data base.
 * 
 * <p>
 * This uses {@link ChannelDetailsRepo} repository to fire the queries and
 * fetch/store the data
 * </p>
 * 
 * <p>
 * This implementation tracks and manages the channel details registered in the
 * system.
 * </p>
 * 
 * Features:
 * 
 * <ul>
 * <li>Manages the channel details registered in the system.</li>
 * <li>Can be integrated with various data sources (e.g., relational databases,
 * NoSQL).</li>
 * <li>Provides methods for querying and retrieving audit logs.</li>
 * </ul>
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Service.html">
 *      Service </a>
 * @author Kabir Akware
 */
@Service
@AllArgsConstructor
public class ChannelDetailServiceImpl implements ChannelDetailService {

	/**
	 * Private variable for {@link ChannelDetailsRepo} object
	 */
	private ChannelDetailsRepo channelDetailsRepo;

	@Override
	public List<ChannelDetails> channelDetails() {
		return channelDetailsRepo.findAll();
	}

	@Override
	public ChannelDetails channelDetails(String channelId) throws AppException {
		return channelDetailsRepo.findById(channelId)
				.orElseThrow(() -> AppException.getInstance("Channel id not found: " + channelId,
						"Pass registered channel id", Codes.ERR09, HttpStatus.NOT_FOUND));
	}

	@Override
	public Channel register(Channel channel) throws AppException {
		validatePublicKey(channel);
		savePublicKey(channel);

		return Channel.getInstance(channel.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.S00,
				channel.getChannelId(), channel.getPublicKey());
	}

	private void savePublicKey(Channel channel) throws AppException {
		try {
			channelDetailsRepo.save(ChannelDetails.getInstance(channel.getChannelId(), channel.getPublicKey()));
		} catch (Exception e) {
			throw AppException.getInstance("Something went wrong!! Kindly contact administrator", e.getMessage(),
					Codes.ERR01, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void validatePublicKey(Channel channel) throws AppException {
		try {
			EncryptionUtility.publicKey(channel.getPublicKey().replace("\n", ""));
		} catch (AppException e) {
			throw AppException.getInstance("Kindly provide a valid public key value", e.getDescription(), Codes.ERR04,
					HttpStatus.BAD_REQUEST);
		}
	}
}
