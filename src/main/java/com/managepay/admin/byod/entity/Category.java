package com.managepay.admin.byod.entity;

import java.util.List;

public class Category {

	public Long id;
	public Long groupCategoryId;
	public Long taxChargeId;
	public String backendId;
	public String name;
	public String description;
	public String imagePath;
	public int sequence;
	public boolean isActive;

	public List<MenuItem> menuItems;

	public Category() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBackendId() {
		return backendId;
	}

	public void setBackendId(String backendId) {
		this.backendId = backendId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Long getGroupCategoryId() {
		return groupCategoryId;
	}

	public void setGroupCategoryId(Long groupCategoryId) {
		this.groupCategoryId = groupCategoryId;
	}

	public Long getTaxChargeId() {
		return taxChargeId;
	}

	public void setTaxChargeId(Long taxChargeId) {
		this.taxChargeId = taxChargeId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

}
