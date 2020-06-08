package com.target.myRetail.model.convertors;

import com.target.myRetail.client.model.TargetProduct;
import com.target.myRetail.pojo.Product;

public class TargetProductToProductConvertor {

	public Product convert(TargetProduct targetProduct) {
		Product product = new Product();
		product.setId(targetProduct.getId());
		product.setName(targetProduct.getName());
		return product;
	}
}
