package com.target.myRetail.model.convertors;

import com.couchbase.client.java.document.json.JsonObject;
import com.target.myRetail.db.model.ProductPriceDB;

public class JsonObjectToProductPriceConveter {
	
	public ProductPriceDB convert(JsonObject jsonObject) {
		ProductPriceDB productPriceDB = new ProductPriceDB();
		productPriceDB.setCurrencyCode(jsonObject.getString("currencyCode"));
		productPriceDB.setPrice(jsonObject.getDouble("price"));
		return productPriceDB;
	}
	
	public JsonObject convert(ProductPriceDB productPriceDB) {
		JsonObject jsonObject = JsonObject.create();
		jsonObject.put("currencyCode", productPriceDB.getCurrencyCode());
		jsonObject.put("price", productPriceDB.getPrice());
		return jsonObject;
	}
}
