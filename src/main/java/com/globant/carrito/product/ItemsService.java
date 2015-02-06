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

import com.globant.carrito.StatusDto;
import com.globant.carrito.cart.Carts;
import com.globant.carrito.client.Clients;
import com.globant.carrito.security.SecurityService;

@RestController
public class ItemsService {

	EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("db");
	
	@RequestMapping(value = "/service/getItem", method = RequestMethod.GET)
	@ResponseBody
	public Items getItem(String name) {
		EntityManager em = emf.createEntityManager();
		Items i = em.find(Items.class, name);
		em.close();
		return i;
	}
	
	@RequestMapping(value = "/service/newItem/{prodId}", method = RequestMethod.GET)
	@ResponseBody
	public StatusDto newItem(@PathVariable("prodId") Integer prodId,HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);

			boolean found = false;
			for(Items i : cart.getItems()){
				if(i.getProduct().getId() == (prodId)){
					i.setQty(i.getQty()+1);
					found = true;
					break;
				}
			}
			if(!found){
				Product product = em.find(Product.class, prodId);
				cart.addItem(new Items(product.getPrice(), product, 1));
			}
			
			
			em.persist(cart);
			tx.commit();
			return new StatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new StatusDto(false);
		} finally {
			em.close();
		}
	}
	
	@RequestMapping(value = "/service/removeItem/{prodId}", method = RequestMethod.GET)
	@ResponseBody
	public StatusDto removeItem(@PathVariable("prodId") Integer prodId,HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {			
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);

			for(Items i : cart.getItems()){
				if(i.getProduct().getId() == (prodId) && i.getQty() > 0){
					i.setQty(i.getQty()-1);
					break;
				}
			}
			
			em.persist(cart);
			tx.commit();
			return new StatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new StatusDto(false);
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
			resp.getResults().add(new ItemsDto(i.getQty(), i.getProduct().getName(), i.getPrice(), i.getProduct().getId()));
		}
		em.close();
		return resp;
	}
	
	private String getUserName(HttpSession session) {
		return (String)session.getAttribute(SecurityService.USERNAME);
	}
	
	private Carts getCart(HttpSession session, EntityManager em) {
		try {
			TypedQuery<Carts> query = em.createQuery("select c from Carts c where c.client.userName = :username and c.status = true", Carts.class);
			query.setParameter("username", getUserName(session));
			return query.getSingleResult();
		} catch (NoResultException e) {
			TypedQuery <Clients> query = em.createQuery("select c from Clients c where c.userName = :username", Clients.class);
			query.setParameter("username", getUserName(session));
			return new Carts(query.getSingleResult());
		}
	}
	
	@RequestMapping(value = "/service/checkout", method = RequestMethod.GET)
	@ResponseBody
	public StatusDto checkOut(HttpSession session) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		
		try {			
			tx = em.getTransaction();
			tx.begin();
			
			Carts cart = getCart(session, em);
			cart.setStatus(false);
			em.persist(cart);
			tx.commit();
			return new StatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return new StatusDto(false);
		} finally {
			em.close();
		}
	}
}