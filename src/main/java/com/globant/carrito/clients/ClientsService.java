package com.globant.carrito.clients;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.globant.carrito.LoginStatusDto;

@RestController
public class ClientsService {

	public static final String USERNAME = "username";

	EntityManagerFactory emf = Persistence.createEntityManagerFactory("db");

	@RequestMapping(value = "/service/getClient", method = RequestMethod.GET)
	@ResponseBody
	public Clients getClient(String username) {
		EntityManager em = emf.createEntityManager();
		Clients c = em.find(Clients.class, username);
		em.close();
		emf.close();
		return c;
	}

	@RequestMapping(value = "/service/newClient", method = RequestMethod.POST)
	@ResponseBody
	public LoginStatusDto createClient(@RequestBody ClientsDto clientDto,
			HttpSession session) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("db");

		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			Clients emc = new Clients(clientDto.getUsername(),
					clientDto.getPassword(), clientDto.getEmail());
			em.persist(emc);
			tx.commit();
			session.setAttribute(USERNAME, clientDto.getUsername());
			return new LoginStatusDto(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			return new LoginStatusDto(false);
		} finally {
			em.close();
		}
	}
}
