package com.managepay.admin.byod.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.Tag;

@Repository
public class TagRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public TagRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private RowMapper<Tag> rowMapper = (rs, rowNum) -> {
		Tag tag = new Tag();
		tag.setId(rs.getLong("id"));
		tag.setBackendId(rs.getString("backend_id"));
		tag.setName(rs.getString("tag_name"));
		return tag;
	};
	
	private ResultSetExtractor<List<Tag>> resultSetExtractor = (rs) -> {
		Map<Long, Tag> map = new HashMap<Long, Tag>();

		while (rs.next()) {
			Long tagId = rs.getLong("id");
			Tag tag = map.get(tagId);

			if (tag == null) {
				tag = new Tag();
				tag.setId(rs.getLong("id"));
				tag.setBackendId(rs.getString("backend_id"));
				tag.setName(rs.getString("tag_name"));
				map.put(tagId, tag);
			}
		}
		return new ArrayList<Tag>(map.values());
	};
	
	public List<Tag> findAllTag(){
		return jdbcTemplate.query("SELECT * FROM tag", resultSetExtractor);
	}
	
	public Tag findTagById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM tag WHERE id = ?",new Object[] {id}, rowMapper);
	}

	public int createTag(Tag tag) {
		return jdbcTemplate.update("INSERT INTO tag(backend_id, tag_name) VALUES(?,?)", new Object[] { tag.getBackendId(), tag.getName() });
	}

	public int editTag(Long id, Tag tag) {
		return jdbcTemplate.update("UPDATE tag SET backend_id = ?,tag_name = ? WHERE id = ?", new Object[] { tag.getBackendId(), tag.getName(), id });
	}

	public int removeTag(Long id) {
		return jdbcTemplate.update("DELETE FROM tag WHERE id = ?", new Object[] { id });
	}
	
	//ItemTag
	public int addTagToItem(Long itemId, Long tagId) {
		return jdbcTemplate.update("INSERT INTO item_tag(item_id, tag_id) VALUES (?,?)", new Object[] {
				itemId, tagId
		});
	}
	
	public int removeItemTag(Long id) {
		return jdbcTemplate.update("DELETE FROM item_tag WHERE id = ?", new Object[] {
				id
		});
	}
	
}
