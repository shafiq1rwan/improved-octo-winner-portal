package managepay.com.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import managepay.com.admin.byod.entity.Category;

@Repository
public class CategoryRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public CategoryRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<Category> rowMapper = (rs, rowNum) -> {
		Category category = new Category();
		category.setId(rs.getLong("id"));
		category.setBackendId(rs.getString("backend_id"));
		category.setName(rs.getString("category_name"));
		category.setDescription(rs.getString("category_description"));
		category.setImagePath(rs.getString("category_image_path"));
		category.setPublished(rs.getBoolean("published"));
		return category;
	};

	public List<Category> findAllCategory() {
		return jdbcTemplate.query("SELECT * FROM category", rowMapper);
	}

	public Category findCategoryById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM category WHERE id = ?", new Object[] { id }, rowMapper);
	}

	public int createCategory(Category category) {
		return jdbcTemplate.update(
				"INSERT into category(backend_id, category_name, category_description, category_image_path, published) VALUES (?,?,?,?,?)",
				new Object[] { category.getBackendId(), category.getName(), category.getDescription(),category.getImagePath(), category.isPublished()});
	}

	public int editCategory(Long id, Category category) {
		return jdbcTemplate.update(
				"UPDATE category SET backend_id = ? , category_name = ?, category_description = ?, category_image_path =?, published = ? WHERE id = ?",
				new Object[] { category.getBackendId(), category.getName(), category.getDescription(),
						category.getImagePath(), category.isPublished(), id });
	}

	public int removeCategory(Long id) {
		return jdbcTemplate.update("DELETE FROM category WHERE id = ?", new Object[] { id });
	}
	
	
	
	
	
	
	

}
