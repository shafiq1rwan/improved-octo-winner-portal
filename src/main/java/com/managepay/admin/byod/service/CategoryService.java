package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.Category;

public interface CategoryService {

	public List<Category> findAllCategory();
	
	public List<Category> findCategoriesByGroupCategoryId(Long groupCategoryId);

	public Category findCategoryById(Long id);

	public int addCategory(Category category);

	public int editCategory(Long id, Category category);

	public int removeCategory(Long id);

}
