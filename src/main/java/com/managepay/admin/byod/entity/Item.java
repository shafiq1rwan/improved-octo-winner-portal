package com.managepay.admin.byod.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class Item {

	public Long id;
	public String backendId;
	public Long modifierGroupId;
	public String name;
	public String description;
	public String imagePath;
	public BigDecimal basePrice;
	public boolean isTaxable;
	public boolean isModifiable;
	public boolean isDiscountable;
	public boolean isPublished;

	public List<ItemGroup> itemSets;
	public List<Tag> tags;
	public List<ModifierGroup> modifierGroups;

	public Item() {
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

	public boolean isTaxable() {
		return isTaxable;
	}

	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	public boolean isModifiable() {
		return isModifiable;
	}

	public void setModifiable(boolean isModifiable) {
		this.isModifiable = isModifiable;
	}

	public boolean isDiscountable() {
		return isDiscountable;
	}

	public void setDiscountable(boolean isDiscountable) {
		this.isDiscountable = isDiscountable;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}
	
	public List<ItemGroup> getItemSets() {
		return itemSets;
	}

	public void setItemSets(List<ItemGroup> itemSets) {
		this.itemSets = itemSets;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<ModifierGroup> getModifierGroups() {
		return modifierGroups;
	}

	public void setModifierGroups(List<ModifierGroup> modifierGroups) {
		this.modifierGroups = modifierGroups;
	}

}
