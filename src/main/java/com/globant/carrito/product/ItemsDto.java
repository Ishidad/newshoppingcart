package com.globant.carrito.product;

public class ItemsDto {

	private int quantity;
	private String description;
	private double price;
	private int id;
	
	public ItemsDto(int quantity, String productDesc, double price,
			int productId) {
		this.quantity = quantity;
		this.description = productDesc;
		this.price = price;
		this.id = productId;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public String getProductName() {
		return description;
	}
	
	public double getAmount() {
		return price;
	}
	
	public int getProductId() {
		return id;
	}
	

}
