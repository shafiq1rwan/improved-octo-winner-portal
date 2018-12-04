package com.managepay.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.Category;

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
		category.setItemCount(rs.getInt("item_group_count"));
		return category;
	};

	public List<Category> findAllCategory() {
		return jdbcTemplate.query(
				"SELECT c.*, COALESCE(cig.item_group_count,0) as 'item_group_count' FROM category c  LEFT JOIN (SELECT category_id,COUNT(item_group_id) AS 'item_group_count' FROM category_item_group GROUP BY category_id) as cig ON c.id = cig.category_id",
				rowMapper);
	}

	public Category findCategoryById(Long id) {
		return jdbcTemplate.queryForObject(
				"SELECT c.*, COALESCE(cig.item_group_count,0) as 'item_group_count' FROM category c  LEFT JOIN (SELECT category_id,COUNT(item_group_id) AS 'item_group_count' FROM category_item_group GROUP BY category_id) as cig ON c.id = cig.category_id WHERE c.id = ?",
				new Object[] { id }, rowMapper);
	}

	public int createCategory(Category category) {
		return jdbcTemplate.update(
				"INSERT into category(backend_id, category_name, category_description, category_image_path, published) VALUES (?,?,?,?,?)",
				new Object[] { category.getBackendId(), category.getName(), category.getDescription(),
						category.getImagePath(), category.isPublished() });
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

	public int countItemGroupByCategoryId(Long categoryId) {
		return jdbcTemplate.queryForObject(
				"SELECT COUNT(cig.item_group_id) FROM category c INNER JOIN category_item_group cig ON c.id = cig.category_id WHERE cig.category_id = ?",
				new Object[] { categoryId }, Integer.class);
	}

}
