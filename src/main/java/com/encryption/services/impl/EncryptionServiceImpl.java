package com.encryption.services.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.encryption.exception.AppException;
import com.encryption.services.EncryptionService;
import com.encryption.utility.CommonUtility;
import com.encryption.utility.EncryptionUtility;
import com.model.dto.Encryption;
import com.model.enums.Codes;
import com.utilities.property.AppProperties;

import lombok.AllArgsConstructor;

/**
 * This class provides the implementation of {@link EncryptionService}
 * interface, offering the structural logic for encryption and decryption
 * extending {@link encryptionUtility} class and provides relevant response to
 * the users
 * 
 * <p>
 * This implementation uses {@link encryptionUtility} to complete the task
 * </p>
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Service.html">
 *      Service </a>
 * @author Kabir Akware
 */
@Service
@AllArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

	/**
	 * Private variable of {@link EncryptionUtility} class
	 */
	private EncryptionUtility encryptionUtility;

	/**
	 * Method to encrypt the plain request body.
	 * 
	 * <p>
	 * This method is the implementation of
	 * {@link EncryptionService#encrypt(Encryption)} which encrypts the input data
	 * with a Symmetric key generated for every operation using {@code AES}
	 * algorithm, encrypts the generated random key with Public key using
	 * {@code RSA} algorithm and creates a digital signature with Private key using
	 * {@code RSA} algorithm returning a {@code Base64} encoded string containing
	 * all the values mentioned separated by a period(.).
	 * </p>
	 * 
	 */
	@Override
	public Encryption encrypt(Encryption plainBody) throws AppException {
		String pass = CommonUtility.generateRandomString(AppProperties.intProperty("aes.pass.length"));
		String salt = CommonUtility.generateRandomString(AppProperties.intProperty("aes.salt.length"));

		String[] aesEncData = encryptionUtility.aesEncrypt(plainBody.getData(), pass.toCharArray(),
				salt.getBytes(StandardCharsets.UTF_8));
		String rsaEncKeys = encryptionUtility
				.rsaEncrypt(new StringBuilder().append(pass).append(AppProperties.strProperty("period")).append(salt)
						.append(AppProperties.strProperty("period")).append(aesEncData[1]).toString());
		String sign = encryptionUtility.sign(plainBody.getData());

		return Encryption.getInstance(plainBody.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.S00,
				CommonUtility.encodedString(
						(new StringBuilder().append(rsaEncKeys).append(AppProperties.strProperty("period")).append(sign)
								.append(AppProperties.strProperty("period")).append(aesEncData[0]).toString())
								.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Method to decrypt the encrypted request body.
	 * 
	 * <p>
	 * This method is the implementation of
	 * {@link EncryptionService#decrypt(Encryption)} which decrypts the input data
	 * by decoding and separating the data, decrypts the passes Symmetric key with
	 * Private key using {@code RSA} algorithm, decrypts the encrypted pay load with
	 * the acquired Symmetric key using {@code AES} algorithm, and verifies the
	 * digital signature with Public key using {@code RSA} algorithm returning the
	 * plain text pay load.
	 * </p>
	 * 
	 */
	@Override
	public Encryption decrypt(Encryption encryptedBody) throws AppException {
		String[] seperatedData = CommonUtility
				.splitString(CommonUtility.bytesToString(CommonUtility.decode(encryptedBody.getData())), 3);

		String[] decKeys = CommonUtility.splitString(encryptionUtility.rsaDecrypt(seperatedData[0]), 3);

		String decData = encryptionUtility.aesDecrypt(seperatedData[2], decKeys[0].toCharArray(),
				decKeys[1].getBytes(StandardCharsets.UTF_8), decKeys[2]);

		if (encryptionUtility.verify(seperatedData[1], decData))
			return Encryption.getInstance(encryptedBody.getRefNo(), CommonUtility.getCurrentTimeStamp(), Codes.S00,
					decData);

		else
			throw AppException.getInstance("Verification Failed!", "Digital Signature not verified.", Codes.ERR02,
					HttpStatus.UNAUTHORIZED);
	}
}
