package com.target.myRetail.db.couchbase;

import javax.ws.rs.NotFoundException;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.target.myRetail.db.model.ProductPriceDB;
import com.target.myRetail.model.convertors.JsonObjectToProductPriceConveter;

public class ProductPriceCouchDAO {

	private JsonObjectToProductPriceConveter conveter = new JsonObjectToProductPriceConveter();

	private String clusterName;
	private String userName;
	private String password;
	private String bucketName;
	private CouchbaseEnvironment environment;
	

	public ProductPriceCouchDAO(String clusterName, String userName, String password, String bucket, CouchbaseEnvironment environment) {
		super();
		this.clusterName = clusterName;
		this.userName = userName;
		this.password = password;
		this.bucketName = bucket;
		this.environment = environment;
	}

	public ProductPriceDB getProductPrice(Integer productId) {
		Bucket bucket = getBucket();
		JsonDocument jsonDocument = bucket.get("myretail:" + productId);
		if(jsonDocument==null) {
			throw new NotFoundException("Not able to find pricing for product id:"+ productId);
		}
		return conveter.convert(jsonDocument.content());
	}

	public void updatePrice(Integer productId, ProductPriceDB productPriceDB) {
		Bucket bucket = getBucket();
		JsonDocument doc = JsonDocument.create("myretail:" + productId, conveter.convert(productPriceDB));
		bucket.replace(doc);
	}
	
	private Bucket getBucket() {
		Cluster cluster = CouchbaseCluster.create(environment, clusterName);
		cluster.authenticate(userName, password);
		Bucket bucket = cluster.openBucket(bucketName);
		return bucket;
	}

}
