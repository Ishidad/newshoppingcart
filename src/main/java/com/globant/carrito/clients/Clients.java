package com.globant.carrito.clients;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.globant.carrito.cart.Carts;

@Entity
public class Clients {

	@Id
	@GeneratedValue
	private int id;

	@Column
	private String username;

	@Column
	private String password;

	@Column
	private String email;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	private List<Carts> cart;

	public Clients() {

	}

	public Clients(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public int getClientId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public List<Carts> getCart() {
		return cart;
	}
}
