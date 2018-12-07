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
		category.setGroupCategoryId(rs.getLong("group_category_id"));
		category.setTaxChargeId(rs.getLong("tax_charge_id"));
		category.setBackendId(rs.getString("backend_id"));
		category.setName(rs.getString("category_name"));
		category.setDescription(rs.getString("category_description"));
		category.setImagePath(rs.getString("category_image_path"));
		category.setSequence(rs.getInt("category_sequence"));
		category.setActive(rs.getBoolean("is_active"));
		return category;
	};

	public List<Category> findAllActiveCategory() {
		return jdbcTemplate.query("SELECT * FROM category WHERE is_active = 1", rowMapper);
	}

	public List<Category> findAllCategory() {
		return jdbcTemplate.query("SELECT * FROM category", rowMapper);
	}

	public List<Category> findCategoriesByGroupCategoryId(Long groupCategoryId) {
		return jdbcTemplate.query("SELECT * FROM category WHERE group_category_id = ?", rowMapper);
	}

	public Category findCategoryById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM category WHERE id = ?", new Object[] { id }, rowMapper);
	}

	public int updateCategoryStatus(Long id, boolean activeFlag) {
		return jdbcTemplate.update("UPDATE category SET is_active = ? WHERE id = ?", new Object[] { activeFlag, id },
				rowMapper);
	}

	public int createCategory(Category category) {
		return jdbcTemplate.update(
				"INSERT into category(group_category_id, tax_charge_id, backend_id, category_name, category_description, category_image_path, category_sequence, is_active) VALUES (?,?,?,?,?,?,?,?)",
				new Object[] { category.getGroupCategoryId(), category.getTaxChargeId(), category.getBackendId(),
						category.getName(), category.getDescription(), category.getImagePath(), category.getSequence(),
						category.isActive() });
	}

	public int editCategory(Long id, Category category) {
		return jdbcTemplate.update(
				"UPDATE category SET group_category_id =? , tax_charge_id =?, backend_id = ? , category_name = ?, category_description = ?, category_image_path =?, category_sequence = ?,is_active = ? WHERE id = ?",
				new Object[] { category.getGroupCategoryId(), category.getTaxChargeId(), category.getBackendId(),
						category.getName(), category.getDescription(), category.getImagePath(), category.getSequence(),
						category.isActive(), id });
	}

	public int removeCategory(Long id) {
		return jdbcTemplate.update("DELETE FROM category WHERE id = ?", new Object[] { id });
	}

	public int findCategorySequence(Long id) {
		return jdbcTemplate.queryForObject(
				"SELECT category_sequence FROM category WHERE id = ? ORDER BY category_sequence DESC LIMIT 1",
				new Object[] { id }, Integer.class);
	}

}
