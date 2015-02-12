package com.globant.carrito.cart;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.globant.carrito.client.Clients;
import com.globant.carrito.product.Items;


@Entity
public class Carts {
	
	// ATTRIBUTES
	
	@Id
	@GeneratedValue
	private int cartId;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Clients client;
	
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Items> items;
	
	@Column
	private Date date;
	
	@Column
	private double cartPrice;
	
	@Column
	@GeneratedValue
	private int transactionNumber;
	
	@Column
	@Enumerated(EnumType.STRING)
	private PaymentTypes paymentType;
	
	@Column
	private boolean status;
	
	// CONSTRUCTORS
	
	public Carts(){
		
	}
	
	public Carts(Clients client) {
		this.client = client;
		items = new HashSet<Items>();
		status = true;
	}

	public Carts(Items... initialItems) {
		items = new HashSet<Items>();
		for (Items item : initialItems) {
			addItem(item);
		}
	}
	
	// ADITIONAL METHODS
	/**
	 * Method called from ItemsService to add items to client´s cart
	 * @param item
	 */
	public void addItem(Items item) {
		items.add(item);
		// Esto es necesario para que cargue la "clave foranea" (Lautaro)
		item.setCart(this);
	}
	
	/**
	 * Method called from ItemsService to remove items from client´s cart when item quantity is equals to 0
	 * @param item
	 */
	public void removeItem(Items item) {
		items.remove(item);
		item.setCart(this);
	}

	// GETTERS
	
	public Set<Items> getItems() {
		return items;
	}

	public Date getDate() {
		return date;
	}

	public double getCartPrice() {
		return cartPrice;
	}

	public PaymentTypes getPaymentType() {
		return paymentType;
	}

	public int getCartId() {
		return cartId;
	}

	public int getTransactionNumber() {
		return transactionNumber;
	}

	public boolean isStatus() {
		return status;
	}
	
	// SETTERS
	
	public void setStatus(boolean status) {
		this.status = status;
	}
}
