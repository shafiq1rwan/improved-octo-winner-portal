package com.managepay.admin.byod.entity;

public class CategoryTenderType {

	private Long id;
	private Long categoryId;
	private Long tenderTypeId;

	public CategoryTenderType() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getTenderTypeId() {
		return tenderTypeId;
	}

	public void setTenderTypeId(Long tenderTypeId) {
		this.tenderTypeId = tenderTypeId;
	}

}
