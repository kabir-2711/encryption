package com.encryption.utility;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.encryption.exception.AppException;
import com.model.enums.Codes;
import com.utilities.property.AppProperties;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class provides the logic for common utility methods used through out the
 * application
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html">
 *      Component </a>
 * @author Kabir Akware
 */
@Component
public class CommonUtility {

	public CommonUtility(HttpServletRequest req) {
		request = req;
	}

	/**
	 * {@link HttpServletRequest} parameter
	 */
	private static HttpServletRequest request;

	/**
	 * Method to encode input data
	 * 
	 * @param data Input data
	 * @return Encoded data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public static String encodedString(byte[] data) throws AppException {
		try {
			return Base64.getEncoder().encodeToString(data);
		} catch (IllegalArgumentException e) {
			throw AppException.getInstance("Error occured while encoding the data", e.getMessage(), Codes.ERR01,
					HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to decode encoded data
	 * 
	 * @param data Encoded data
	 * @return Decoded data
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public static byte[] decode(String data) throws AppException {
		try {
			return Base64.getDecoder().decode(data);
		} catch (IllegalArgumentException e) {
			throw AppException.getInstance("Error occured while decoding the data", e.getMessage(), Codes.ERR01,
					HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Method to separate the string based on the {@code seperator} value in
	 * configurations
	 * 
	 * @param data               String value
	 * @param expectedSeparation expected separation count
	 * @return Array of string with separated string
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public static String[] splitString(String data, int expectedSeparation) throws AppException {
		String[] speratedText;
		try {
			speratedText = data.split(AppProperties.strProperty("seperator"));
			if (speratedText.length != expectedSeparation)
				throw AppException.getInstance("Data Seperation Failiure!",
						new StringBuilder().append("Not all data is provided. Data seperated by ")
								.append(speratedText.length - 1).append(" '")
								.append(AppProperties.strProperty("period")).append("'").toString(),
						Codes.ERR01, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			throw AppException
					.getInstance(
							new StringBuilder().append("Data is not '").append(AppProperties.strProperty("period"))
									.append("' seperated").toString(),
							e.getMessage(), Codes.ERR01, HttpStatus.BAD_REQUEST);
		}
		return speratedText;
	}

	/**
	 * Method to generate random alphanumeric string
	 * 
	 * @param length length of the required string
	 * @return Random alphanumeric string of input length
	 * @throws AppException Thrown when a custom exception occurs
	 */
	public static String generateRandomString(int length) throws AppException {
		try {
			return SecureRandom.getInstance(AppProperties.strProperty("secure.instance"))
					.ints(48/* character 0 */, 122/* character z */ + 1)
					.filter(i -> (i < 58 || i > 66) && (i < 91 || i > 98)).limit(length)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		} catch (NoSuchAlgorithmException e) {
			throw AppException.getInstance("Error in initiating SecureRandom to generate Random String", e.getMessage(),
					Codes.ERR01, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method to get the current time stamp string in a pre-defined format
	 * 
	 * @return Current time stamp
	 */
	public static String getCurrentTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppProperties.strProperty("time.stamp.format")));
	}

	/**
	 * Method to convert byte array to string with {@code StandardCharsets.UTF_8}
	 * transformation format
	 * 
	 * @param bytes Byte array
	 * @return String value
	 */
	public static String bytesToString(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Method to check and get the reference number provided in the request
	 * 
	 * @return Reference number if present or {@code null} value
	 */
	public static String getRefNo() {
		return Objects.nonNull(request.getAttribute("ref-no")) ? request.getAttribute("ref-no").toString() : null;
	}

	/**
	 * Method to get Servlet-Path of the request
	 * 
	 * @return Servlet-Path
	 */
	public static String getServletPath() {
		return request.getServletPath();
	}

	/**
	 * Method to check and get the request cached
	 * 
	 * @return Cached request if present or {@code null} value
	 */
	public static Object getCachedReq() {
		return request.getAttribute("cached-req");
	}
}
