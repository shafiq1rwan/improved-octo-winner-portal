package com.managepay.admin.byod.entity;

import java.math.BigDecimal;
import java.util.List;

public class MenuItem {

	public Long id;
	public String backendId;
	public Long modifierGroupId;
	public String name;
	public String description;
	public String imagePath;
	public BigDecimal basePrice;
	public int type;
	public boolean isTaxable;
	public boolean isDiscountable;

	public List<MenuItem> items;
	public List<ItemGroup> itemGroups;
	public List<ModifierGroup> modifierGroups;

	public MenuItem() {
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

	public Long getModifierGroupId() {
		return modifierGroupId;
	}

	public void setModifierGroupId(Long modifierGroupId) {
		this.modifierGroupId = modifierGroupId;
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

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isTaxable() {
		return isTaxable;
	}

	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	public boolean isDiscountable() {
		return isDiscountable;
	}

	public void setDiscountable(boolean isDiscountable) {
		this.isDiscountable = isDiscountable;
	}

	public List<MenuItem> getItems() {
		return items;
	}

	public void setItems(List<MenuItem> items) {
		this.items = items;
	}

	public List<ItemGroup> getItemGroups() {
		return itemGroups;
	}

	public void setItemGroups(List<ItemGroup> itemGroups) {
		this.itemGroups = itemGroups;
	}

	public List<ModifierGroup> getModifierGroups() {
		return modifierGroups;
	}

	public void setModifierGroups(List<ModifierGroup> modifierGroups) {
		this.modifierGroups = modifierGroups;
	}

}
