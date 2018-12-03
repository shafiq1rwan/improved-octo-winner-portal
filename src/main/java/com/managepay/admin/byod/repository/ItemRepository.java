package com.managepay.admin.byod.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.Item;
import com.managepay.admin.byod.entity.ItemGroup;
import com.managepay.admin.byod.entity.ItemSet;
import com.managepay.admin.byod.entity.Tag;

@Repository
public class ItemRepository {
	
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ItemRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private RowMapper<Item> rowMapper = (rs, rowNum) -> {
		Item item = new Item();
		item.setId(rs.getLong("id"));
		item.setBackendId(rs.getString("backend_id"));
		item.setModifierGroupId(rs.getLong("modifier_group_id"));
		item.setName(rs.getString("item_name"));
		item.setDescription(rs.getString("item_description"));
		item.setImagePath(rs.getString("item_image_path"));
		item.setBasePrice(rs.getBigDecimal("item_base_price"));
		item.setTaxable(rs.getBoolean("taxable"));
		item.setModifiable(rs.getBoolean("modifiable"));
		item.setDiscountable(rs.getBoolean("discountable"));
		item.setPublished(rs.getBoolean("published"));
		return item;
	};
	
	public List<Item> findItemByItemGroupId(Long itemGroupId){
		return jdbcTemplate.query("SELECT i.* FROM item i INNER JOIN item_group_item igi ON i.id = igi.item_id WHERE igi.item_group_id = ? AND igi.item_group_id IS NOT NULL AND igi.item_id IS NOT NULL", new Object[] {itemGroupId},rowMapper);	
	}
	
	public List<Item> findAllItem(){
		return jdbcTemplate.query("SELECT * FROM item", rowMapper);
	}
	
	public Item findItemById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM item WHERE id = ?", new Object[] {id}, rowMapper);
	}
	
	public Long createItem(Item item) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection-> {PreparedStatement ps = connection.prepareStatement("INSERT INTO item(backend_id, modifier_group_id, item_name, item_description, item_image_path, item_base_price, taxable, modifiable,discountable, published) VALUES(?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, item.getBackendId());
			ps.setLong(2, item.getModifierGroupId());
			ps.setString(3, item.getName());
			ps.setString(4, item.getDescription());
			ps.setString(5, item.getImagePath());
			ps.setBigDecimal(6, item.getBasePrice());
			ps.setBoolean(7, item.isTaxable());
			ps.setBoolean(8, item.isModifiable());
			ps.setBoolean(9, item.isDiscountable());
			ps.setBoolean(10, item.isPublished());
			return ps;
		},keyHolder);

		return (Long)keyHolder.getKey();
	}
	
	public int editItem(Long id, Item item) {
		return jdbcTemplate.update("UPDATE item SET backend_id = ?, modifier_group_id = ?, item_name = ?, item_description = ?, item_image_path = ?, item_base_price = ?, taxable = ?, modifiable = ?,discountable = ?, published = ? WHERE id = ?", new Object[] {
				item.getBackendId(), item.getModifierGroupId(), item.getName(), item.getDescription(), item.getImagePath(), item.getBasePrice(),item.isTaxable(), item.isModifiable(), item.isDiscountable(), item.isPublished(), id
		});
	}
	
	public int removeItem(Long id) {
		return jdbcTemplate.update("DELETE FROM item WHERE id = ?", new Object[] {id});
	}
	
	//ItemGroupItem
	public int addItemIntoItemGroup(Long itemId, Long itemGroupId, int sequenceNumber) {
		return jdbcTemplate.update("INSERT INTO item_group_item(item_id, item_group_id, item_group_item_sequence) VALUES(?,?,?)", new Object[] {
				itemId, itemGroupId, sequenceNumber
		});
	}
	
	public int editItemIntoItemGroup(Long id, Long itemGroupId, int sequenceNumber) {
		return jdbcTemplate.update("UPDATE item_group_item SET item_group_id = ?, item_group_item_sequence =? WHERE id = ?) VALUES(?,?,?)", new Object[] {
				itemGroupId, sequenceNumber, id
		});
	}
	
	public int removeItemGroupItem(Long id) {
		return jdbcTemplate.update("DELETE FROM item_group_item WHERE id = ?", new Object[] {
				id
		});
	}
	
	//ItemModifierGroup (Not yet handle)
	public int addModifierItemIntoModifierGroup(Long itemId, Long modifierGroupId) {
		return jdbcTemplate.update("INSERT INTO item_modifier_group (item_id, modifier_group_id) VALUES (?,?)", new Object[] {
			itemId, modifierGroupId
		});
	}
	
	public int editItemModifierGroup(Long id, Long modifierGroupId) {
		return jdbcTemplate.update("UPDATE item_modifier_group SET modifier_group_id = ? WHERE id = ?", new Object[] {
				modifierGroupId, id
			});
	}
	
	public int removeItemModifierGroup(Long id) {
		return jdbcTemplate.update("DELETE FROM item_modifier_group WHERE id = ?", new Object[] {
				id
		});
	}
	
	//Item Set
	public List<ItemGroup> findItemGroupFromItemSet(Long id) {
		return jdbcTemplate.query("SELECT * FROM item_group WHERE id IN (SELECT item_group_id FROM item_set WHERE item_id = ?)", new Object[] {id},	
			(rs, rowNum) -> {
				ItemGroup itemGroup = new ItemGroup();
				itemGroup.setId(rs.getLong("id"));
				itemGroup.setBackendId(rs.getString("backend_id"));
				itemGroup.setName(rs.getString("item_group_name"));
				return itemGroup;
			});
	}

	public int addItemSet(Long itemId, Long itemGroupId, int sequenceNumber) {
		return jdbcTemplate.update("INSERT INTO item_set(item_id, item_group_id, item_set_sequence) VALUES (?,?,?)", new Object[] {
				itemId, itemGroupId, sequenceNumber
		});
	}
	
	public int editItemSet(Long id, int sequenceNumber) {
		return jdbcTemplate.update("UPDATE item_set SET item_group_id = ? , item_set_sequence = ? WHERE id = ?", new Object[] {
				sequenceNumber,id
			});
	}
	
	public int removeItemSet(Long id) {
		return jdbcTemplate.update("DELETE FROM item_set WHERE id = ?", new Object[] {
				id
		});
	}

}
