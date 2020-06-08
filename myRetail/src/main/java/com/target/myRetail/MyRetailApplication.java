package com.target.myRetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.target.myRetail.client.TargetProductService;
import com.target.myRetail.controller.ProductController;
import com.target.myRetail.db.couchbase.ProductPriceCouchDAO;
import com.target.myRetail.service.ProductPriceFacade;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetailApplication extends Application<Configuration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyRetailApplication.class);

	private static String TARGET_API_BASE_URL = "http://localhost:1080";
	private static String COUCHBASE_CLUSTER = "localhost";
	private static String COUCHBASE_USERNAME = "admin";
	private static String COUCHBASE_PASSWORD = "123456";
	private static String COUCHBASE_BUCKET = "product_price";

	@Override
	public void initialize(Bootstrap<Configuration> bootstrap) {

	}

	@Override
	public void run(Configuration c, Environment e) throws Exception {
		LOGGER.info("Registering REST resources");
		

		CouchbaseEnvironment environment = DefaultCouchbaseEnvironment
			    .builder()
			    .build();

		TargetProductService targetProductService = new Retrofit.Builder().baseUrl(TARGET_API_BASE_URL)
				.addConverterFactory(GsonConverterFactory.create()).build().create(TargetProductService.class);

		ProductPriceCouchDAO productPriceCouchDAO = new ProductPriceCouchDAO(COUCHBASE_CLUSTER, COUCHBASE_USERNAME,
				COUCHBASE_PASSWORD, COUCHBASE_BUCKET, environment);
		ProductPriceFacade productPriceFacade = new ProductPriceFacade(productPriceCouchDAO, targetProductService);
		e.jersey().register(new ProductController(productPriceFacade));
	}

	public static void main(String[] args) throws Exception {
		new MyRetailApplication().run(args);
	}

}
