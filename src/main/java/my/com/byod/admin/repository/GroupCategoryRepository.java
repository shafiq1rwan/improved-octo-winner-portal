package my.com.byod.admin.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import my.com.byod.admin.entity.GroupCategory;

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
		groupCategory.setCreatedDate(rs.getDate("created_date"));
		return groupCategory;
	};

	public List<GroupCategory> findAllGroupCategory() {
		return jdbcTemplate.query("SELECT * FROM group_category", groupCategoryRowMapper);
	}

	public GroupCategory findGroupCategoryById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM group_category WHERE id = ?", new Object[] { id },
				groupCategoryRowMapper);
	}

	public Long createGroupCategory(GroupCategory groupCategory) {

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO group_category(group_category_name) VALUES(?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, groupCategory.getName());
			return ps;
		}, keyHolder);

		System.out.println(keyHolder.getKey().longValue());
		
		return keyHolder.getKey().longValue();
	}

	public int editGroupCategory(Long id, GroupCategory groupCategory) {
		return jdbcTemplate.update("UPDATE group_category SET group_category_name = ? WHERE id = ?",
				new Object[] { groupCategory.getName(), id });
	}

	public int removeGroupCategory(Long id) {
		return jdbcTemplate.update("DELETE FROM group_category WHERE id = ?", new Object[] { id });
	}

	public int checkGroupCategoryNameDuplication(String name) {
		return jdbcTemplate.queryForObject(
				"SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ?",
				new Object[] { name }, Integer.class);
	}

}
