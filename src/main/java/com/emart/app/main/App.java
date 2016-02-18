package com.emart.app.main;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emart.service.resources.ProductResource;

public class App extends Application<CauchbaseConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	
	@Override
	public void initialize(Bootstrap<CauchbaseConfiguration> b) {
	}

	@Override
	public void run(CauchbaseConfiguration config, Environment e)
			throws Exception {
		LOGGER.info("Method App#run() called");
		System.out.println("Hello world, by Dropwizard!");
		System.out.println("Coucbase Bucket : " + config.getCouchbaseBucket());
		
		 HttpClient httpClient = new HttpClientBuilder(e).using(config.getHttpClientConfiguration())
                .build("TimeOut");
		 
		e.jersey().register(new ProductResource(config));
	}

	public static void main(String[] args) throws Exception {
		new App().run(args);
	}
}