package com.managepay.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.ItemAvailability;

@Repository
public class SubmenuItemRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public SubmenuItemRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<ItemAvailability> itemAvailabilityRowMapper = (rs, rowNum) -> {
		ItemAvailability itemAvailability = new ItemAvailability();
		itemAvailability.setId(rs.getLong("id"));
		itemAvailability.setItemId(rs.getLong("item_id"));
		itemAvailability.setSubmenuId(rs.getLong("submenu_id"));
		itemAvailability.setStatus(rs.getString("item_status"));
		return itemAvailability;
	};

	public List<ItemAvailability> findAllItemAvailability(Long submenuId, Long itemId) {
		return jdbcTemplate.query("SELECT * FROM item_availability WHERE submenu_id = ? AND item_id = ?",
				new Object[] { submenuId, itemId }, itemAvailabilityRowMapper);
	}

	public int createItemAvailability(ItemAvailability itemAvailability) {
		return jdbcTemplate.update("INSERT INTO item_availability(submenu_id,item_id,item_status) VALUES(?,?,?)",
				new Object[] { itemAvailability.getSubmenuId(), itemAvailability.getItemId(),
						itemAvailability.getStatus() });

	}

	public int editItemAvailability(Long id, ItemAvailability itemAvailability) {
		return jdbcTemplate.update(
				"UPDATE item_availability SET submenu_id = ?, item_id = ?, item_status = ? WHERE id = ?",
				new Object[] { itemAvailability.getSubmenuId(), itemAvailability.getItemId(),
						itemAvailability.getStatus(), id });
	}
	
	//Item Inventory
	
	

}
