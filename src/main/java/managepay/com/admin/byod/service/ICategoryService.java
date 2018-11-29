package managepay.com.admin.byod.service;

import java.util.List;

import managepay.com.admin.byod.entity.Category;

public interface ICategoryService {

	public List<Category> findAllCategory();

	public Category findCategoryById(Long id);

	public int addCategory(Category category);

	public int editCategory(Long id, Category category);

	public int removeCategory(Long id);

}
