package com.target.myRetail.model.convertors;

import com.target.myRetail.db.model.ProductPriceDB;
import com.target.myRetail.pojo.Product.ProductPrice;

public class ProductPriceToProductPriceDBConvertor {

	public ProductPrice convert(ProductPriceDB db) {
		ProductPrice productPrice = new ProductPrice();
		productPrice.setCurrencyCode(db.getCurrencyCode());
		productPrice.setPrice(db.getPrice());
		return productPrice;
	}
}
