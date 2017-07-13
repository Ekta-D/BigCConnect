package com.bigc.general.classes;

import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import roboguice.util.temp.Ln;
import android.app.Application;
import android.util.Log;

import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;

public class RoboSpiceService extends SpringAndroidSpiceService {

	public RoboSpiceService() {
		/***
		 * When we compile for release probably we want that RoboSpice will log
		 * just errors.
		 */
		Ln.getConfig().setLoggingLevel(Log.ERROR);
	}

	@Override
	public CacheManager createCacheManager(Application application)
			throws CacheCreationException {
		CacheManager cacheManager = new CacheManager();
		cacheManager
				.addPersister(new JacksonObjectPersisterFactory(application));
		return cacheManager;
	}

	@Override
	public RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		// web services support json responses
		MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		jsonConverter
				.getObjectMapper()
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate
				.getMessageConverters();

		listHttpMessageConverters.add(jsonConverter);
		restTemplate.setMessageConverters(listHttpMessageConverters);

		return restTemplate;
	}

}