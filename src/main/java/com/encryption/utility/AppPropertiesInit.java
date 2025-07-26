package com.encryption.utility;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.encryption.exception.AppException;
import com.model.dto.AppProps.AppProperty;
import com.model.enums.Codes;
import com.utilities.exceptions.ConfigException;
import com.utilities.property.AppProperties;
import com.utilities.property.PropertyFileUpdater;

import jakarta.annotation.PreDestroy;

/**
 * The {@code AppProperties} class is responsible to load property file from the
 * path mentioned in {@code application.properties} using {@link InputStream}
 * and caching the properties in memory for further use in the application.
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/stereotype/Component.html">
 *      Component </a>
 * @see Properties
 * @author Kabir Akware
 */
@Component
public class AppPropertiesInit {

	private AppProperties appRoperties;

	private String configPath;

	/**
	 * Custom constructor for {@code AppPropertiesInit}
	 * 
	 * @param configPath Configuration property file path injected from
	 *                   {@code application.properties} with property
	 *                   'config.property.path'
	 */
	public AppPropertiesInit(@Value("${config.property.path}") String configPath) {
		this.appRoperties = AppProperties.initateLoadingProperties(configPath);
		this.configPath = configPath;
	}

	public Properties reload(List<AppProperty> properties) throws AppException {
		try {
			properties.stream().filter(property -> Objects.nonNull(property.getKey()))
					.forEach(prop -> PropertyFileUpdater.updateProperty(configPath, prop.getKey(), prop.getValue()));
		} catch (ConfigException e) {
			throw AppException.getInstance(e.getMessage(), "Something went wrong!!", Codes.ERR05, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		refreshProperties();
		return AppProperties.getLoadedProperties();
	}

	/**
	 * Method to refresh the cached property file at a given interval mentioned in
	 * the {@code application.properties} file with property
	 * 'config.cache.reload.hour'
	 * 
	 * 
	 * @see <a href =
	 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Scheduled.html">Scheduled</a>
	 */
	@Scheduled(cron = "${config.cache.reload.hour}")
	public void refreshProperties() {
		appRoperties.refreshProperties(configPath);
	}

	/**
	 * Custom method to clear the properties before destroying the container
	 */
	@PreDestroy
	protected synchronized void clean() {
		appRoperties.clean();
	}
}
