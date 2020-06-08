package com.target.myRetail.pojo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class Product {

	@NotNull
	Integer id;

	@Length(min = 2, max = 255)
	String name;

	@NotNull
	ProductPrice currentPrice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProductPrice getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(ProductPrice currentPrice) {
		this.currentPrice = currentPrice;
	}

	public static class ProductPrice {
		Double price;
		String currencyCode;

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

	}

}
