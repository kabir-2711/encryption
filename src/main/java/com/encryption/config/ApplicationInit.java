package com.encryption.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.encryption.services.ChannelDetailService;
import com.encryption.utility.CommonUtility;
import com.model.entity.ChannelDetails;
import com.utilities.exceptions.ConfigException;
import com.utilities.log.Log;
import com.utilities.property.AppProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * The {@code ApplicationInit} class is responsible for initializing key store
 * and channel details during the startup phase of the application and caching
 * them in memory for further use in the application.
 * 
 * <p>
 * Key Responsibilities:
 * <ul>
 * <li>Set up custom configurations or properties required for the
 * application.</li>
 * <li>Initialize resources such as Key store object from server and channel
 * details from data base.</li>
 * </ul>
 * 
 * <p>
 * This class is designed to be extended or modified to suit the specific
 * initialization requirements of the application.
 * 
 * @author Kabir Akware
 */
@Component
@DependsOn("appPropertiesInit")
public class ApplicationInit {

	/**
	 * Custom constructor to load {@link ChannelDetailService} interface in the
	 * class
	 * 
	 * @param channelDetailService {@link ChannelDetailService} interface object
	 */
	public ApplicationInit(ChannelDetailService channelDetailService) {
		this.channelDetailService = channelDetailService;
	}

	/**
	 * Key store object to store in memory
	 */
	private KeyStore keyStore;

	/**
	 * List of {@link ChannelDetails} object to store in memory
	 */
	private List<ChannelDetails> channels = new ArrayList<>();

	/**
	 * {@link ChannelDetailService} interface parameter
	 */
	private ChannelDetailService channelDetailService;

	/**
	 * Method to load and cache key store file containing private and public keys
	 * 
	 * @return {@link KeyStore} object from cached data
	 */
	private KeyStore loadKeyStore() {
		try (InputStream is = new FileInputStream(AppProperties.strProperty("keystore.path"));) {
			KeyStore keyStore = KeyStore.getInstance(AppProperties.strProperty("keyStore.instance"));
			keyStore.load(is, AppProperties.strProperty("keystore.pass").toCharArray());
			Log.info(this.getClass().getSimpleName(), "loadKeyStore", "key store loaded...");
			return keyStore;
		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			throw ConfigException.getInstance("exception occurred while loading key store: " + e.getMessage());
		}
	}

	/**
	 * Method to load and cache Channel details from {@link ChannelDetailService}
	 * 
	 * @return List of {@link ChannelDetails} consisting channel details
	 */
	private List<ChannelDetails> loadChannelDetails() {
		List<ChannelDetails> channelDetails = channelDetailService.channelDetails();
		Log.info(this.getClass().getSimpleName(), "loadChannelDetails", "%s channel(s) loaded...",
				channelDetails.size());
		return channelDetails;
	}

	/**
	 * Method to refresh the cached key store and channel keys at a given interval
	 * mentioned in the {@code application.properties} file with property
	 * 'config.cache.reload.midnight'
	 * 
	 * 
	 * @see <a href =
	 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Scheduled.html">Scheduled</a>
	 */
	@Scheduled(cron = "${config.cache.reload.midnight}")
	protected void refreshKeyStore() {
		this.keyStore = loadKeyStore();
		Log.info(this.getClass().getSimpleName(), "refreshKeyStore", "key store refreshed at %s",
				CommonUtility.getCurrentTimeStamp());

		this.channels = loadChannelDetails();
		Log.info(this.getClass().getSimpleName(), "refreshChannelDetails", "channel details refreshed at %s",
				CommonUtility.getCurrentTimeStamp());
	}

	/**
	 * Custom {@code init()} method to be called after creating the bean in the
	 * framework to load the Key-store
	 * 
	 * 
	 * @see <a href =
	 *      "https://docs.oracle.com/javase/8/docs/api/javax/annotation/PostConstruct.html">PostConstruct</a>
	 */
	@PostConstruct
	private void init() {
		this.keyStore = loadKeyStore();
		this.channels = loadChannelDetails();

	}

	/**
	 * Method to get Key store object
	 * 
	 * @return {@link KeyStore} object
	 */
	public KeyStore keyStore() {
		return keyStore;
	}

	/**
	 * Method to get list of channels
	 * 
	 * @return List of {@link ChannelDetails}
	 */
	public List<ChannelDetails> channels() {
		return channels;
	}

	/**
	 * Custom method to clear the key store object and channel details before
	 * destroying the container
	 */
	@PreDestroy
	private synchronized void clean() {
		this.keyStore = null;
		Log.info(this.getClass().getSimpleName(), "clean", "key store object destroyed");
		this.channels.clear();
		Log.info(this.getClass().getSimpleName(), "clean", "properties object destroyed");
	}
}
