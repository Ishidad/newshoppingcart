package com.globant.carrito.security;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.globant.carrito.LoginStatusDto;
import com.globant.carrito.clients.Clients;

@RestController
public class SecurityService {
	public static final String USERNAME = "username";

	@RequestMapping(value = "/service/login", method=RequestMethod.POST)
	@ResponseBody
	public LoginStatusDto login(@RequestBody LoginDto dto,HttpSession session) {
	
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("db");

		EntityManager em = emf.createEntityManager();
		
		String username = dto.getUsername();
		
		try {
		    TypedQuery<Clients> query = em.createQuery("from Clients c where c.username = :username", Clients.class);
		        query.setParameter("username", username);
		        Clients client = query.getSingleResult();
		        boolean isValid = dto.getPassword().equals(client.getPassword());
		        if (isValid) {
		        	session.setAttribute(USERNAME, username);
		        }
		        return new LoginStatusDto(isValid);
		} catch (NoResultException e) {
			return new LoginStatusDto(false);
		} finally {
			em.close();
		}
	}
	
	@RequestMapping(value = "/service/logout", method=RequestMethod.POST)
	public void logout(HttpSession session) {
		session.invalidate();
	}
}