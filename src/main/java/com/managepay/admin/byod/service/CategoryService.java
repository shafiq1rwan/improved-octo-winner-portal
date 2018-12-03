package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.repository.CategoryRepository;

@Service
public class CategoryService implements CategoryServiceImp {

	private CategoryRepository categoryRepo;

	public CategoryService(CategoryRepository categoryRepo) {
		this.categoryRepo = categoryRepo;
	}

	@Override
	public List<Category> findAllCategory() {
		try {
			return categoryRepo.findAllCategory();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Category findCategoryById(Long id) {
		try {
			return categoryRepo.findCategoryById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new Category();
		}
	}

	@Override
	public int addCategory(Category category) {
		try {
			return categoryRepo.createCategory(category);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeCategory(Long categoryId) {
		try {
			return categoryRepo.removeCategory(categoryId);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editCategory(Long id, Category category) {
		try {
			return categoryRepo.editCategory(id, category);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
