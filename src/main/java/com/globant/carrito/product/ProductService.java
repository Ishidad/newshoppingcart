package com.globant.carrito.product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductService {

	EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("db");
	
	//Products are items that can be bought outside the shopping cart
	//This method shows a list of items available to sell
	@RequestMapping(value = "/service/getProducts", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getProducts() {
		
		EntityManager em = emf.createEntityManager();

		TypedQuery<Product> query = em.createQuery("select p from Product p", Product.class);
		ProductResponse response = new ProductResponse();
		for(Product p : query.getResultList()) {
			response.getResults().add(p);
		}
		em.close();
		return response;
	}
}
