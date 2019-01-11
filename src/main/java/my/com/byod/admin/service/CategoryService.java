package my.com.byod.admin.service;

import java.util.List;

import my.com.byod.admin.entity.Category;

public interface CategoryService {

	public List<Category> findAllCategory();
	
	public List<Category> findAllActiveCategory();
	
	public List<Category> findCategoriesByGroupCategoryId(Long groupCategoryId);

	public Category findCategoryById(Long id);

	public int addCategory(Category category);

	public int editCategory(Long id, Category category);

	public int removeCategory(Long id);
	
	public void updateCategoryStatus(Long categoryId, boolean activeFlag);
	
	public int updateCategorySequence(Long categoryId, int sequence);

}
