package com.managepay.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.GroupCategory;

@Repository
public class GroupCategoryRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public GroupCategoryRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<GroupCategory> groupCategoryRowMapper = (rs, rowNum) -> {
		GroupCategory groupCategory = new GroupCategory();
		groupCategory.setId(rs.getLong("id"));
		groupCategory.setName(rs.getString("group_category_name"));
		return groupCategory;
	};

	public List<GroupCategory> findAllGroupCategory() {
		return jdbcTemplate.query("SELECT * FROM group_category", groupCategoryRowMapper);
	}

	public GroupCategory findGroupCategoryById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM group_category WHERE id = ?", new Object[] { id },
				groupCategoryRowMapper);
	}

	public int createGroupCategory(GroupCategory groupCategory) {
		return jdbcTemplate.update("INSERT INTO group_category(group_category_name) VALUES(?)",
				new Object[] { groupCategory.getName() });
	}

	public int editGroupCategory(Long id, GroupCategory groupCategory) {
		return jdbcTemplate.update("UPDATE group_category SET group_category_name = ? WHERE id = ?",
				new Object[] { groupCategory.getName(), id });
	}

	public int removeGroupCategory(Long id) {
		return jdbcTemplate.update("DELETE FROM group_category WHERE id = ?", new Object[] { id });
	}

}
