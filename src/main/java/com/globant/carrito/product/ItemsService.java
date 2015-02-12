package com.globant.carrito.product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.globant.carrito.LoginStatusDto;
import com.globant.carrito.cart.Carts;
import com.globant.carrito.clients.Clients;
import com.globant.carrito.security.SecurityService;

@RestController
public class ItemsService {

	EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("db");
	
	@RequestMapping(value = "/service/newItem/{prodId}", method = RequestMethod.GET)
	@ResponseBody
	public LoginStatusDto newItem(@PathVariable("prodId") Integer prodId,HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);

			boolean found = false; // Flag for checking if the item to add already exists in cart.
			for(Items i : cart.getItems()){
				if(i.getProduct().getId() == (prodId)){
					i.setStock(i.getStock()+1);
					found = true;
					break;
				}
			}
			if(!found){ // If the item to add is not currently in the cart, creates a new instance
				Product product = em.find(Product.class, prodId);
				cart.addItem(new Items(product.getPrice(), product, 1));
			}
			em.persist(cart);
			tx.commit();
			return new LoginStatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new LoginStatusDto(false);
		} finally {
			em.close();
		}
	}
	
	@RequestMapping(value = "/service/removeItem/{prodId}", method = RequestMethod.GET)
	@ResponseBody
	public LoginStatusDto removeItem(@PathVariable("prodId") Integer prodId,HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);

			for(Items i : cart.getItems()){
				if(i.getProduct().getId() == (prodId) && i.getStock() > 0){
					i.setStock(i.getStock()-1);
					if(i.getStock() == 0){ // If the item removed has quantity = 0, then remove it from the client cart
						cart.removeItem(i);
					}
					break;
				}
			}
			em.persist(cart);
			tx.commit();
			return new LoginStatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new LoginStatusDto(false);
		} finally {
			em.close();
		}
	}
	
	@RequestMapping(value = "/service/getCart", method = RequestMethod.GET)
	@ResponseBody
	public ItemsResponse getCart(HttpSession session) {
		EntityManager em = emf.createEntityManager();
		Carts c = getCart(session, em);
		ItemsResponse resp = new ItemsResponse();
		for(Items i : c.getItems()){
			resp.getResults().add(new ItemsDto(i.getStock(), i.getProduct().getName(), i.getPrice(), i.getProduct().getId()));
		}
		em.close();
		return resp;
	}
	
	private String getUsername(HttpSession session) {
		return (String)session.getAttribute(SecurityService.USERNAME);
	}
	
	//Returns the last cart assigned to a Client. 
	//If not found, it creates a new one and is assigned to the Client.
	private Carts getCart(HttpSession session, EntityManager em) {
		try {
			TypedQuery<Carts> query = em.createQuery("select c from Carts c where c.client.username = :username and c.status = true", Carts.class);
			query.setParameter("username", getUsername(session));
			return query.getSingleResult();
		} catch (NoResultException e) {
			TypedQuery <Clients> query = em.createQuery("select c from Clients c where c.username = :username", Clients.class);
			query.setParameter("username", getUsername(session));
			return new Carts(query.getSingleResult());
		}
	}
	
	@RequestMapping(value = "/service/checkout", method = RequestMethod.GET)
	@ResponseBody
	public LoginStatusDto checkOut(HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		
		try {			
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);
			cart.setStatus(false);
			em.persist(cart);
			tx.commit();
			return new LoginStatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new LoginStatusDto(false);
		} finally {
			em.close();
		}
	}
}