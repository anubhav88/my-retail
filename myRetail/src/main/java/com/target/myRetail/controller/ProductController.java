package com.target.myRetail.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.target.myRetail.db.model.ProductPriceDB;
import com.target.myRetail.service.ProductPriceFacade;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {
	
	private ProductPriceFacade productPriceFacade;


	public ProductController(ProductPriceFacade productPriceFacade) {
		super();
		this.productPriceFacade = productPriceFacade;
	}

	@GET
	@Path("/{id}")
	public Response getProduct(@PathParam("id") Integer id) throws IOException {
		return Response.ok(productPriceFacade.getProduct(id)).build();
	}

	@Path("/{id}/prices")
	@PUT
	public Response updateProductPricing(@PathParam("id") Integer id, ProductPriceDB productPrice) throws IOException {
		productPriceFacade.updateProductPrice(id, productPrice);
		return Response.ok(null).build();
	}

}
