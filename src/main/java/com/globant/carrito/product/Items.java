package com.globant.carrito.product;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.globant.carrito.cart.Carts;

@Entity
public class Items {
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column
	private double price;
	
	//This is not intended to work as a fully stock service
	//It just to verify if is a quantity of that item
	//The stock should be in another class
	@Column
	private int stock;

	@ManyToOne(cascade = CascadeType.ALL)
	private Carts cart;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Product product;

	public Items() {
		
	}
	
	public Items(double price, Product product, int qty) {
		this.price = price;
		this.product = product;
		this.stock = qty;
	}

	public int getItemId() {
		return id;
	}
	
	public double getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public Carts getCart() {
		return cart;
	}

	public Product getProduct() {
		return product;
	}

	//Updates quantity when adding or removing Items from the cart
	public void setStock(int qty) {
		this.stock = qty;
	}

	public void setCart(Carts cart) {
		this.cart = cart;
	}
}