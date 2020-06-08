package com.target.myretail.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.target.myRetail.client.TargetProductService;
import com.target.myRetail.controller.ProductController;
import com.target.myRetail.db.couchbase.ProductPriceCouchDAO;
import com.target.myRetail.db.model.ProductPriceDB;
import com.target.myRetail.pojo.Product;
import com.target.myRetail.service.ProductPriceFacade;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ProductControllerTest {
	
	private ProductController productController;
	
	private static String COUCHBASE_BUCKET = "product_price";
	private static BucketDefinition bucketDefinition = new BucketDefinition("product_price");
	private CouchbaseEnvironment environment = null;
	
	
	
	@Rule
	public MockServerContainer mockServer = new MockServerContainer("5.10.0");;
	
	
	@Rule
	public CouchbaseContainer couchbaseContainer = new CouchbaseContainer().withBucket(bucketDefinition).waitingFor(Wait.forHealthcheck());

	@Before
	public void setup() throws InterruptedException {
		
		createMockserverExpectations();
		setUpCouchbaseData();
		
		ProductPriceCouchDAO productPriceCouchDAO = new ProductPriceCouchDAO(couchbaseContainer.getHost(), couchbaseContainer.getUsername(),
				couchbaseContainer.getPassword(), COUCHBASE_BUCKET, environment);
		
		
		
		TargetProductService targetProductService = new Retrofit.Builder().baseUrl(mockServer.getEndpoint())
				.addConverterFactory(GsonConverterFactory.create()).build().create(TargetProductService.class);

		ProductPriceFacade productPriceFacade = new ProductPriceFacade(productPriceCouchDAO, targetProductService);
		productController = new ProductController(productPriceFacade);
	    
	}


	@Test
	public void testGetProduct() throws IOException {
		Response response = productController.getProduct(1000);
		Product product = (Product)response.getEntity();
		assertEquals(200, response.getStatus());
		assertEquals("product1", product.getName());
		assertEquals("INR", product.getCurrentPrice().getCurrencyCode());
		assertEquals(new Double(100.1), product.getCurrentPrice().getPrice());
	}
	
	
	@Test(expected = NotFoundException.class)
	public void getProductNotFound() throws IOException {
		productController.getProduct(1001);
	}
	
	
	@Test(expected = NotFoundException.class)
	public void getProductNotFoundInUpdatePrice() throws IOException {
		ProductPriceDB productPrice = new ProductPriceDB();
		productPrice.setCurrencyCode("USA");
		productPrice.setPrice(100.1);
		productController.updateProductPricing(10001, productPrice);
	}
	
	
	@Test
	public void updatePrice() throws IOException {
		ProductPriceDB productPrice = new ProductPriceDB();
		productPrice.setCurrencyCode("INR");
		productPrice.setPrice(100.1);
		Response response = productController.updateProductPricing(1002, productPrice);
		assertEquals(200, response.getStatus());
	
	        
	}
	
	
	



	@SuppressWarnings("resource")
	private void createMockserverExpectations() {
		MockServerClient mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
		mockServerClient
	    .when(HttpRequest.request().withMethod("GET")
	            .withPath("/products/1000")
	    )
	    .respond(
	    	HttpResponse.response()
	            .withBody("{\n" + 
	            		"    	\"name\" :\"product1\",\n" + 
	            		"    	\"id\" : 1000\n" + 
	            		"    }")
	    );
		
		
		mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
		mockServerClient
	    .when(HttpRequest.request().withMethod("GET")
	            .withPath("/products/1002")
	    )
	    .respond(
	    	HttpResponse.response()
	            .withBody("{\n" + 
	            		"    	\"name\" :\"product2\",\n" + 
	            		"    	\"id\" : 1002\n" + 
	            		"    }")
	    );
	}

	
	private void setUpCouchbaseData() {
		environment = DefaultCouchbaseEnvironment
			    .builder()
			    .bootstrapCarrierDirectPort(couchbaseContainer.getBootstrapCarrierDirectPort())
			    .bootstrapHttpDirectPort(couchbaseContainer.getBootstrapHttpDirectPort())
			    .build();

			Cluster cluster = CouchbaseCluster.create(
			    environment,
			    couchbaseContainer.getHost()
			);
			
			cluster.authenticate(couchbaseContainer.getUsername(), couchbaseContainer.getPassword());
			
			
			
	        Bucket bucket = cluster.openBucket(bucketDefinition.getName());
	        JsonObject product1 = JsonObject.create()
	                .put("currencyCode", "INR")
	                .put("price", 100.1);
	        
	        JsonObject product2 = JsonObject.create()
	                .put("currencyCode", "USA")
	                .put("price", 102.1);
	               

	            // Store the Document
	            bucket.upsert(JsonDocument.create("myretail:1000", product1));
	            bucket.upsert(JsonDocument.create("myretail:1002", product2));
	}
	
	
	
	
	
	@After
	public void done() {
		mockServer.stop();
		couchbaseContainer.stop();
	    
	}
}
