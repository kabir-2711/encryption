package com.encryption.utility;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.encryption.config.ApplicationInit;
import com.encryption.exception.AppException;
import com.model.entity.ChannelDetails;
import com.model.enums.Codes;
import com.utilities.property.AppProperties;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

/**
 * This class provides the core logic for encryption and decryption using
 * {@code AES} and {@code RSA} algorithms using {@code PublicKey},
 * {@code PrivateKey} and {@code SymmetricKey}
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html">
 *      Component </a>
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/DependsOn.html">
 *      DependsOn </a>
 * @author Kabir Akware
 */
@Component
@AllArgsConstructor
public class EncryptionUtility {

	/**
	 * {@link HttpServletRequest} parameter
	 */
	private HttpServletRequest request;

	/**
	 * {@link ApplicationInit} object parameter
	 */
	private ApplicationInit applicationInit;

	/**
	 * Method to encrypt the keys using RSA with ECB mode and OAEP padding scheme
	 * with SHA-256 and MGF1 padding.
	 * <p>
	 * Note: RSA with ECB mode and OAEP padding is intended for small data sizes,
	 * typically less than the RSA key size minus padding overhead. It is not
	 * suitable for encrypting large files or streams of data.
	 * </p>
	 * 
	 * @param text plain keys in string format
	 * @return RSA encrypted keys
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public String rsaEncrypt(String text) throws AppException {
		try {
			Cipher cipher = Cipher.getInstance(AppProperties.strProperty("rsa.algorithm"));
			cipher.init(Cipher.ENCRYPT_MODE, publicKey(encodedPublic()));
			return CommonUtility.encodedString(cipher.doFinal(text.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while encrypting the key", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (NoSuchPaddingException e) {
			throw AppException.getInstance("Padding mismatch while encrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while encrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (IllegalBlockSizeException e) {
			throw AppException.getInstance("Data is too large while encrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (BadPaddingException e) {
			throw AppException.getInstance("Bad padding exception occured while encrypting the key", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to decrypt the keys using RSA with ECB mode and OAEP padding scheme
	 * with SHA-256 and MGF1 padding.
	 * <p>
	 * Note: RSA with ECB mode and OAEP padding is intended for small data sizes,
	 * typically less than the RSA key size minus padding overhead. It is not
	 * suitable for decrypting large files or streams of data.
	 * </p>
	 * 
	 * @param data plain keys in string format
	 * @return RSA decrypted keys
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public String rsaDecrypt(String data) throws AppException {
		try {
			Cipher cipher = Cipher.getInstance(AppProperties.strProperty("rsa.algorithm"));
			cipher.init(Cipher.DECRYPT_MODE, privateKey());
			return CommonUtility.bytesToString(cipher.doFinal(CommonUtility.decode(data)));
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while decrypting the key", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (NoSuchPaddingException e) {
			throw AppException.getInstance("Padding mismatch while decrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while decrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (IllegalBlockSizeException e) {
			throw AppException.getInstance("Data is too large while decrypting the key", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (BadPaddingException e) {
			throw AppException.getInstance("Bad padding exception occured while decrypting the key", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to encrypt plain body using AES in GCM mode with no padding.
	 *
	 * <p>
	 * Note: AES/GCM mode requires an initialization vector (IV) and supports
	 * additional authenticated data (AAD). The IV must be unique for each
	 * encryption operation with the same key to ensure security.
	 * </p>
	 * 
	 * @param body Plain body in String format
	 * @param pass Value to generate AES Key
	 * @param salt Value to generate AES Key
	 * @return AES encrypted data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public String[] aesEncrypt(String body, char[] pass, byte[] salt) throws AppException {
		try {
			Cipher cipher = Cipher.getInstance(AppProperties.strProperty("aes.algorithm"));
			cipher.init(Cipher.ENCRYPT_MODE, symmetricKey(pass, salt));
			return new String[] { CommonUtility.encodedString(cipher.doFinal(body.getBytes())),
					CommonUtility.encodedString(cipher.getIV()) };
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while encrypting the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (NoSuchPaddingException e) {
			throw AppException.getInstance("Padding mismatch while encrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while encrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (IllegalBlockSizeException e) {
			throw AppException.getInstance("Data is too large while encrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (BadPaddingException e) {
			throw AppException.getInstance("Bad padding exception occured while encrypting the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to decrypt plain body using AES in GCM mode with no padding.
	 *
	 * <p>
	 * Note: AES/GCM mode requires an initialization vector (IV) and supports
	 * additional authenticated data (AAD). The IV must be unique for each
	 * encryption operation with the same key to ensure security.
	 * </p>
	 * 
	 * @param encText Encrypted body in String format
	 * @param pass    Value shared in pay load
	 * @param salt    Value shared in pay load
	 * @param iv      Initialization Vector (IV) shared for decrypting
	 * @return AES decrypted data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public String aesDecrypt(String encText, char[] pass, byte[] salt, String iv) throws AppException {
		try {
			Cipher cipher = Cipher.getInstance(AppProperties.strProperty("aes.algorithm"));
			cipher.init(Cipher.DECRYPT_MODE, symmetricKey(pass, salt),
					new GCMParameterSpec(AppProperties.intProperty("gcm.length"), CommonUtility.decode(iv)));
			return CommonUtility.bytesToString(cipher.doFinal(CommonUtility.decode(encText)));
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while decrypting the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (NoSuchPaddingException e) {
			throw AppException.getInstance("Padding mismatch while decrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while decrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (IllegalBlockSizeException e) {
			throw AppException.getInstance("Data is too large while decrypting the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (BadPaddingException e) {
			throw AppException.getInstance("Bad padding exception occured while decrypting the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (InvalidAlgorithmParameterException e) {
			throw AppException.getInstance("The Algorithm is invalid while decrypting the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to sign the given data using the RSA private key and the SHA-256 hash
	 * algorithm. This method generates a digital signature that can be used to
	 * verify the integrity and authenticity of the data.
	 *
	 * <p>
	 * Note: The private key used for signing must correspond to the public key that
	 * will be used for verification. The signature algorithm is a combination of
	 * the SHA-256 hash function and RSA encryption.
	 * </p>
	 *
	 * @param text Plain data
	 * @return Signed data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public String sign(String text) throws AppException {
		try {
			Signature signature = Signature.getInstance(AppProperties.strProperty("signature.instance"));
			signature.initSign(privateKey());
			signature.update(MessageDigest.getInstance(AppProperties.strProperty("message.digest.instance"))
					.digest(text.getBytes(StandardCharsets.UTF_8)));
			return CommonUtility.encodedString(signature.sign());
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while signing the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while signing the data", e.getMessage(), Codes.ERR03,
					HttpStatus.FORBIDDEN);
		} catch (SignatureException e) {
			throw AppException.getInstance("The signature is invalid while signing the data", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to verify the given signature using the RSA private key and the
	 * SHA-256 hash algorithm. This method verifies the digital signature that has
	 * been generated verifying the integrity and authenticity of the data.
	 *
	 * <p>
	 * Note: The public key used for verification must correspond to the private key
	 * that was used to generate the signature. The signature algorithm is a
	 * combination of the SHA-256 hash function and RSA encryption.
	 * </p>
	 *
	 * @param sign Signed data
	 * @param text Plain data
	 * @return Boolean value determining verification success
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public boolean verify(String sign, String text) throws AppException {
		try {
			Signature signature = Signature.getInstance(AppProperties.strProperty("signature.instance"));
			signature.initVerify(publicKey(encodedPublic()));
			signature.update(MessageDigest.getInstance(AppProperties.strProperty("message.digest.instance"))
					.digest(text.getBytes(StandardCharsets.UTF_8)));
			return signature.verify(CommonUtility.decode(sign));
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while verifying the signature", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (InvalidKeyException e) {
			throw AppException.getInstance("The key is invalid while verifying the signature", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		} catch (SignatureException e) {
			throw AppException.getInstance("The signature is invalid while verifying the signature", e.getMessage(),
					Codes.ERR03, HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to get {@code PrivateKey} object using RSA Key factory instance
	 * 
	 * @return Private key
	 * @throws AppException Thrown when a custom exception occurs
	 */
	private PrivateKey privateKey() throws AppException {
		try {
			return (PrivateKey) applicationInit.keyStore().getKey(AppProperties.strProperty("keystore.alias"),
					AppProperties.strProperty("keystore.pass").toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while generating private key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (UnrecoverableKeyException e) {
			throw AppException.getInstance("Key not recoverable whhile generating private key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (KeyStoreException e) {
			throw AppException.getInstance("The Keystore is invalid while generating private key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method to get {@code PublicKey} object using RSA Key factory instance
	 * 
	 * @param publicKey Encoded Public key string
	 * 
	 * @return Public Key
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public static PublicKey publicKey(String publicKey) throws AppException {
		try {
			return KeyFactory.getInstance(AppProperties.strProperty("rsa.instance"))
					.generatePublic(new X509EncodedKeySpec(CommonUtility.decode(publicKey)));
		} catch (InvalidKeySpecException e) {
			throw AppException.getInstance("Key spec is invalid while generating public key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while generating public key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method to get Encoded public key value from {@link ApplicationInit}
	 * 
	 * @return Encoded public key value stored in {@link ApplicationInit} object
	 * @throws AppException Thrown when the provided channel key is invalid
	 */
	private String encodedPublic() throws AppException {
		return applicationInit.channels().stream()
				.filter(channel -> channel.getChannelId()
						.equals(request.getAttribute("ref-no").toString().substring(0, 3)))
				.map(ChannelDetails::getPublicKey).findFirst()
				.orElseThrow(() -> AppException.getInstance("Channel id is either invalid or not passed!!",
						"Pass a registered channel Id at the start of the Reference number", Codes.ERR04,
						HttpStatus.BAD_REQUEST));
	}

	/**
	 * Method to get {@code SecretKey} object using {@code PBKDF2WithHmacSHA256}
	 * Secret key factory instance
	 * 
	 * @param pass Value as input password
	 * @param salt Value as input salt
	 * @return Secret key
	 * @throws AppException Thrown when a custom exception occurs
	 */
	private SecretKey symmetricKey(char[] pass, byte[] salt) throws AppException {
		try {
			return new SecretKeySpec(
					SecretKeyFactory.getInstance(AppProperties.strProperty("secret.instance"))
							.generateSecret(new PBEKeySpec(pass, salt, AppProperties.intProperty("iteration.count"),
									AppProperties.intProperty("key.size")))
							.getEncoded(),
					AppProperties.strProperty("aes.instance"));
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Invalid algorithm used while generating AES key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InvalidKeySpecException e) {
			throw AppException.getInstance("The keySpec is invalid while generating AES key", e.getMessage(),
					Codes.ERR04, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
