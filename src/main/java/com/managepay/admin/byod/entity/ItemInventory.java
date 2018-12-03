package com.managepay.admin.byod.entity;

public class ItemInventory {

	private Long id;
	private Long submenuId;
	private Long itemId;
	private int quantity;

	public ItemInventory() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubmenuId() {
		return submenuId;
	}

	public void setSubmenuId(Long submenuId) {
		this.submenuId = submenuId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
