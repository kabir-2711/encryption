package com.encryption.services;

import com.encryption.exception.AppException;
import com.model.dto.Encryption;

/**
 * This Interface defines the contract for {@code EncryptionService} service.
 * The service provides functionalities for managing encryption and decryption
 * logic using {@code AES} and {@code RSA} algorithm in the application
 * 
 * <p>
 * The implementation of this interface handles the core logic for encryption
 * and decryption operations in the application.
 * </p>
 * 
 * 
 * @author Kabir Akware
 */
public interface EncryptionService {

	/**
	 * Declaration of {@code encrypt} method for encrypting the data.
	 * 
	 * @param plainBody Plain data given by user
	 * @return {@link Encryption} object with encrypted data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	Encryption encrypt(Encryption plainBody) throws AppException;

	/**
	 * Declaration of {@code decrypt} method for decrypting the data
	 * 
	 * @param encryptedBody Encrypted data given by user
	 * @return {@link Encryption} object with decrypted data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	Encryption decrypt(Encryption encryptedBody) throws AppException;

}
