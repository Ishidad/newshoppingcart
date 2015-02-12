package com.globant.carrito.cart;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.globant.carrito.clients.Clients;
import com.globant.carrito.product.Items;


@Entity
public class Carts {
	
	@Id
	@GeneratedValue
	private int cartId;
	
	// One User may have many carts because, every checked out cart is related
	// to that user for purchases follow up
	@ManyToOne(cascade = CascadeType.ALL)
	private Clients client;
	
	//Set a collection of item that will be in the shopping cart
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Items> items;
	
	//Set a final price to track how much a person spend on a checked out cart
	//Ideally this should be in another class that manages the recipes of the purchases
	@Column
	private double cartFinalPrice;
	
	//This is the cart status, if true, cart is in use, if false, cart was checked out
	@Column
	private boolean status;
	
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
	

	//This method add items to clients cart, also used to load the item FK
	public void addItem(Items item) {
		items.add(item);
		item.setCart(this);
	}
	
	//This method removes items from the cart
	public void removeItem(Items item) {
		items.remove(item);
		item.setCart(this);
	}

	public Set<Items> getItems() {
		return items;
	}

	public double getCartPrice() {
		return cartFinalPrice;
	}

	public int getCartId() {
		return cartId;
	}

	public boolean isStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
}
