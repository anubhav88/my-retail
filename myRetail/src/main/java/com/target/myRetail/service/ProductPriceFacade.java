package com.target.myRetail.service;

import java.io.IOException;

import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.target.myRetail.client.TargetProductService;
import com.target.myRetail.client.model.TargetProduct;
import com.target.myRetail.db.couchbase.ProductPriceCouchDAO;
import com.target.myRetail.db.model.ProductPriceDB;
import com.target.myRetail.model.convertors.ProductPriceToProductPriceDBConvertor;
import com.target.myRetail.model.convertors.TargetProductToProductConvertor;
import com.target.myRetail.pojo.Product;

import retrofit2.Response;

public class ProductPriceFacade {

	private Logger log = LoggerFactory.getLogger(ProductPriceFacade.class);

	private ProductPriceCouchDAO productPriceCouchDAO;

	private TargetProductService targetProductService;

	private ProductPriceToProductPriceDBConvertor productPriceToProductPriceDBConvertor = new ProductPriceToProductPriceDBConvertor();

	private TargetProductToProductConvertor targetProductToProductConvertor = new TargetProductToProductConvertor();

	public ProductPriceFacade(ProductPriceCouchDAO productPriceCouchDAO, TargetProductService targetProductService) {
		this.productPriceCouchDAO = productPriceCouchDAO;
		this.targetProductService = targetProductService;
	}

	public void updateProductPrice(Integer productId, ProductPriceDB productPrice) throws IOException {
		getTargetProduct(productId);
		productPriceCouchDAO.updatePrice(productId, productPrice);
	}

	public Product getProduct(Integer productId) throws IOException {
		TargetProduct targetProduct = getTargetProduct(productId);
		Product product = targetProductToProductConvertor.convert(targetProduct);
		ProductPriceDB productPriceDB = productPriceCouchDAO.getProductPrice(productId);
		product.setCurrentPrice(productPriceToProductPriceDBConvertor.convert(productPriceDB));
		return product;

	}

	private TargetProduct getTargetProduct(Integer productId) throws IOException {
		Response<TargetProduct> response = targetProductService.getProduct(productId).execute();
		if (!response.isSuccessful()) {
			log.error(response.errorBody().string());
			log.error("Received error code:" + response.code());
			log.error("Got Error while fetching product detalils for target service for product id:" + productId);
			if (response.code() == 404) {
				throw new NotFoundException();
			}

			throw new RuntimeException(response.message());
		}

		TargetProduct targetProduct = response.body();
		return targetProduct;
	}

}
