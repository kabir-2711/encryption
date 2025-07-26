package com.encryption.services;

import java.util.Properties;

import com.encryption.exception.AppException;
import com.model.dto.AppProps;

/**
 * This Interface defines the contract for {@code PropertyService} service. The
 * service provides functionalities for fetching and updating property values
 * used in the application.
 * 
 * <p>
 * The implementation of this service can be used to fetch property values used
 * in the application.
 * </p>
 * 
 * 
 * @author Kabir Akware
 */
public interface PropertyService {

	/**
	 * Declaration of method to fetch property values used in the application
	 * 
	 * @return Properties stored in the application
	 */
	Properties props();

	/**
	 * Declaration of method to update property values used in the application
	 * 
	 * @return Updated properties that the application will use moving forward
	 * @throws AppException Custom Exception
	 */
	Properties update(AppProps appProps) throws AppException;

	/**
	 * Declaration of method to refresh property values used in the application
	 * 
	 * @return Properties that the application will use moving forward
	 */
	Properties refresh();

	Object props(String key);

}
