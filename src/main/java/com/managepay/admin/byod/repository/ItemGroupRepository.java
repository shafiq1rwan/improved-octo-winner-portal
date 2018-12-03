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

import com.managepay.admin.byod.entity.Category;
import com.managepay.admin.byod.entity.CategoryItemGroup;
import com.managepay.admin.byod.entity.ChargeConfig;
import com.managepay.admin.byod.entity.Item;
import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.entity.ModifierGroup;

@Repository
public class ItemGroupRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ItemGroupRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<ItemGroup> rowMapper = (rs, rowNum) -> {
		ItemGroup itemGroup = new ItemGroup();
		itemGroup.setId(rs.getLong("id"));
		itemGroup.setBackendId(rs.getString("backend_id"));
		itemGroup.setName(rs.getString("item_group_name"));
		return itemGroup;
	};

	public List<ItemGroup> findItemGroupByCategoryId(Long categoryId) {
		return jdbcTemplate.query(
				"SELECT * FROM item_group ig INNER JOIN category_item_group cig ON cig.item_group_id = ig.id WHERE cig.category_id = ? ORDER_BY cig.category_item_group_sequence",
				new Object[] { categoryId },rowMapper);
	}
	
	public List<ItemGroup> findAllItemGroup(){
		return jdbcTemplate.query("SELECT * FROM item_group", rowMapper);
	}
	
	public ItemGroup findItemGroupById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM item_group WHERE id =? ", new Object[] {id}, rowMapper);
	}
	
	public int createItemGroup(ItemGroup itemGroup) {
		return jdbcTemplate.update("INSERT into item_group(backend_id, item_group_name) VALUES(?,?)", new Object[] {
				itemGroup.getBackendId(), itemGroup.getName()
		});
	}
	
	public int editItemGroup(Long id, ItemGroup itemGroup) {
		return jdbcTemplate.update("UPDATE item_group SET backend_id = ?, item_group_name = ? WHERE id = ?",
				new Object[] { itemGroup.getBackendId(), itemGroup.getName(), id });
	}
	
	public int removeItemGroup(Long id) {
		return jdbcTemplate.update("DELETE FROM item_group WHERE id = ?", new Object[] {id});
	}
	
	//CategoryItemGroup CRUD
	private ResultSetExtractor<List<CategoryItemGroup>> resultSetExtractor = (rs) -> {
		Map<Long, CategoryItemGroup> map = new HashMap<Long, CategoryItemGroup>();

		while (rs.next()) {
			Long categoryItemGroupId = rs.getLong("id");
			CategoryItemGroup categoryItemGroup = map.get(categoryItemGroupId);

			if (categoryItemGroup == null) {
				categoryItemGroup = new CategoryItemGroup();
				categoryItemGroup.setId(rs.getLong("id"));
				categoryItemGroup.setCategoryId(rs.getLong("category_id"));
				categoryItemGroup.setItemGroupId(rs.getLong("item_group_id"));
				categoryItemGroup.setSequence(rs.getInt("category_item_group_sequence"));
				map.put(categoryItemGroupId, categoryItemGroup);
			}	
		}
		return new ArrayList<CategoryItemGroup>(map.values());
	};

	public List<CategoryItemGroup>findCategoryItemGroupByItemGroupId(Long itemGroupId) {
		return jdbcTemplate.query("SELECT * FROM category_item_group WHERE item_group_id = ?", new Object[] {itemGroupId}, resultSetExtractor);
	}
	
	public int addCategoryItemGroup(Long itemGroupId, Long categoryId, int sequenceNumber) {
		return jdbcTemplate.update("INSERT INTO category_item_group(category_id, item_group_id, category_item_group_sequence) VALUES(?,?,?)", new Object[] {categoryId,itemGroupId,sequenceNumber});
	}
	
	public int editCategoryItemGroup(Long id, int sequenceNumber) {
		return jdbcTemplate.update("UPDATE category_item_group SET category_item_group_sequence = ? WHERE id = ?", new Object[] {sequenceNumber, id});
	}
	
	public int removeCategoryItemGroup(Long id) {
		return jdbcTemplate.update("DELETE FROM category_item_group WHERE id = ?", new Object[] {id});
	}
	
	public int removeCategoryItemGroupByCategoryId(Long categoryId) {
		return jdbcTemplate.update("DELETE FROM category_item_group WHERE category_id = ?", new Object[] {categoryId});
	}
	
	public int getCategoryItemGroupSequence(Long categoryId) {
		return jdbcTemplate.queryForObject("SELECT category_item_group_sequence FROM category_item_group WHERE category_item_group = ? ORDER BY DESC LIMIT 1", new Object[] {categoryId},Integer.class);
	}

}
