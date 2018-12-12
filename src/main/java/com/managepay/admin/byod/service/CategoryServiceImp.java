package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.repository.CategoryRepository;
import com.managepay.admin.byod.repository.ItemGroupRepository;

@Service
public class CategoryServiceImp implements CategoryService {

	private CategoryRepository categoryRepo;

	@Autowired
	public CategoryServiceImp(CategoryRepository categoryRepo) {
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
	public List<Category> findAllActiveCategory() {
		try {
			return categoryRepo.findAllActiveCategory();
		} catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<Category> findCategoriesByGroupCategoryId(Long groupCategoryId) {
		try {
			return categoryRepo.findCategoriesByGroupCategoryId(groupCategoryId);
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
	public int editCategory(Long id, Category category) {
		try {
			return categoryRepo.editCategory(id, category);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeCategory(Long categoryId) {
		try {
			
			//TODO be careful
			int affectedRow = categoryRepo.removeCategory(categoryId);
			return affectedRow;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public void updateCategoryStatus(Long categoryId, boolean activeFlag) {
		try {
			categoryRepo.updateCategoryStatus(categoryId, activeFlag);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public int updateCategorySequence(Long categoryId, int sequence) {
		try {
			return categoryRepo.updateCategorySequence(categoryId, sequence);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
