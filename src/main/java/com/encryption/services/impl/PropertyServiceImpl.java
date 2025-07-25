package com.encryption.services.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import com.encryption.exception.AppException;
import com.encryption.services.PropertyService;
import com.encryption.utility.AppPropertiesInit;
import com.model.dto.AppProps;
import com.utilities.property.AppProperties;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PropertyServiceImpl implements PropertyService {

	private AppPropertiesInit appProperties;

	@Override
	public Properties props() {
		return AppProperties.getLoadedProperties();
	}

	@Override
	public Properties update(AppProps appProps) throws AppException {
		return appProperties.reload(appProps.getProperties());
	}

	@Override
	public Properties refresh() {
		appProperties.refreshProperties();
		return AppProperties.getLoadedProperties();
	}

	@Override
	public Object props(String key) {
		return AppProperties.getLoadedProperties().get(key);
	}

}
