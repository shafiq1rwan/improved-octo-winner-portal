package com.managepay.admin.byod.entity;

public class Submenu {

	private Long id;
	private Long storeId;
	private Long submenuCreatorId;

	public Submenu() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public Long getSubmenuCreatorId() {
		return submenuCreatorId;
	}

	public void setSubmenuCreatorId(Long submenuCreatorId) {
		this.submenuCreatorId = submenuCreatorId;
	}

}
