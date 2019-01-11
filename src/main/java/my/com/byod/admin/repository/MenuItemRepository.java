package my.com.byod.admin.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import my.com.byod.admin.entity.ItemGroup;
import my.com.byod.admin.entity.MenuItem;
import my.com.byod.admin.entity.Tag;

@Repository
public class MenuItemRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public MenuItemRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<MenuItem> menuItemRowMapper = (rs, rowNum) -> {
		MenuItem menuItem = new MenuItem();
		menuItem.setId(rs.getLong("id"));
		menuItem.setBackendId(rs.getString("backend_id"));
		menuItem.setModifierGroupId(rs.getLong("modifier_group_id"));
		menuItem.setName(rs.getString("menu_item_name"));
		menuItem.setDescription(rs.getString("menu_item_description"));
		menuItem.setImagePath(rs.getString("menu_item_image_path"));
		menuItem.setBasePrice(rs.getBigDecimal("menu_item_base_price"));
		menuItem.setType(rs.getInt("menu_item_type"));
		menuItem.setTaxable(rs.getBoolean("is_taxable"));
		menuItem.setDiscountable(rs.getBoolean("is_discountable"));
		return menuItem;
	};

	public List<MenuItem> findAllMenuItem() {
		return jdbcTemplate.query("SELECT * FROM menu_item", menuItemRowMapper);
	}

	public MenuItem findMenuItemById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM menu_item WHERE id = ?", new Object[] { id }, menuItemRowMapper);
	}

	public Long createMenuItem(MenuItem menuItem) {

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO menu_item(backend_id, modifier_group_id, menu_item_name, menu_item_description, menu_item_image_path, menu_item_base_price, menu_item_type,is_taxable, is_discountable) VALUES(?,?,?,?,?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, menuItem.getBackendId());
			ps.setLong(2, menuItem.getModifierGroupId());
			ps.setString(3, menuItem.getName());
			ps.setString(4, menuItem.getDescription());
			ps.setString(5, menuItem.getImagePath());
			ps.setBigDecimal(6, menuItem.getBasePrice());
			ps.setInt(7, menuItem.getType());
			ps.setBoolean(8, menuItem.isTaxable());
			ps.setBoolean(9, menuItem.isDiscountable());
			return ps;
		}, keyHolder);

		return (Long) keyHolder.getKey();
	}

	public int removeMenuItem(Long id) {
		return jdbcTemplate.update("DELETE FROM menu_item WHERE id = ?", new Object[] { id });
	}

	public List<MenuItem> findItemByItemGroupId(Long itemGroupId) {
		return jdbcTemplate.query(
				"SELECT i.* FROM item i INNER JOIN item_group_item igi ON i.id = igi.item_id WHERE igi.item_group_id = ? AND igi.item_group_id IS NOT NULL AND igi.item_id IS NOT NULL",
				new Object[] { itemGroupId }, menuItemRowMapper);
	}

	/*public List<MenuItem> findItemByModifierGroupId(Long modifierGroupId) {
		return jdbcTemplate.query("SELECT * FROM item WHERE modifier_group_id = ?", new Object[] { modifierGroupId },
				rowMapper);
	}

	public int editItem(Long id, MenuItem item) {
		return jdbcTemplate.update(
				"UPDATE item SET backend_id = ?, modifier_group_id = ?, item_name = ?, item_description = ?, item_image_path = ?, item_base_price = ?, taxable = ?, modifiable = ?,discountable = ?, published = ? WHERE id = ?",
				new Object[] { item.getBackendId(), item.getModifierGroupId(), item.getName(), item.getDescription(),
						item.getImagePath(), item.getBasePrice(), item.isTaxable(), item.isModifiable(),
						item.isDiscountable(), item.isPublished(), id });
	}

	// ItemGroupItem
	public int addItemIntoItemGroup(Long itemId, Long itemGroupId, int sequenceNumber) {
		return jdbcTemplate.update(
				"INSERT INTO item_group_item(item_id, item_group_id, item_group_item_sequence) VALUES(?,?,?)",
				new Object[] { itemId, itemGroupId, sequenceNumber });
	}*/

	// settle
	public int editItemIntoItemGroup(Long id, int sequenceNumber) {
		return jdbcTemplate.update("UPDATE item_group_item SET item_group_item_sequence =? WHERE id = ?",
				new Object[] { sequenceNumber, id });
	}

	// settle
	public int removeItemGroupItem(Long id) {
		return jdbcTemplate.update("DELETE FROM item_group_item WHERE id = ?", new Object[] { id });
	}

	// settle
	public int removeItemGroupItemByItemGroupId(Long itemGroupId) {
		return jdbcTemplate.update("DELETE FROM item_group_item WHERE item_group_id = ?", new Object[] { itemGroupId });
	}

	// settle
	public int removeItemGroupItemByItemId(Long itemId) {
		return jdbcTemplate.update("DELETE FROM item_group_item WHERE item_id = ?", new Object[] { itemId });
	}

	// settle (not yet test)
	public int getItemGroupItemSequence(Long itemGroupId) {
		return jdbcTemplate.queryForObject(
				"SELECT item_group_item_sequence FROM item_group_item WHERE item_group_id = ? ORDER BY item_group_item_sequence DESC LIMIT 1",
				Integer.class);
	}

	// ItemModifierGroup (Not yet handle)
	public int addModifierItemIntoModifierGroup(Long itemId, Long modifierGroupId) {
		return jdbcTemplate.update("INSERT INTO item_modifier_group (item_id, modifier_group_id) VALUES (?,?)",
				new Object[] { itemId, modifierGroupId });
	}

	/*
	 * public int editItemModifierGroup(Long id, Long modifierGroupId) { return
	 * jdbcTemplate.
	 * update("UPDATE item_modifier_group SET modifier_group_id = ? WHERE id = ?",
	 * new Object[] { modifierGroupId, id }); }
	 */

	public int removeItemModifierGroup(Long id) {
		return jdbcTemplate.update("DELETE FROM item_modifier_group WHERE id = ?", new Object[] { id });
	}

	public int removeItemModifierGroupByItemId(Long itemId) {
		return jdbcTemplate.update("DELETE FROM item_modifier_group WHERE item_id = ?", new Object[] { itemId });
	}

	public int removeItemModifierGroupByModifierGroupId(Long modifierGroupId) {
		return jdbcTemplate.update("DELETE FROM item_modifier_group WHERE modifier_group_id = ?",
				new Object[] { modifierGroupId });
	}

	// Item Set
	public List<ItemGroup> findItemGroupByItemSetItemId(Long id) {
		return jdbcTemplate.query(
				"SELECT * FROM item_group WHERE id IN (SELECT item_group_id FROM item_set WHERE item_id = ?)",
				new Object[] { id }, (rs, rowNum) -> {
					ItemGroup itemGroup = new ItemGroup();
					itemGroup.setId(rs.getLong("id"));
					itemGroup.setBackendId(rs.getString("backend_id"));
					itemGroup.setName(rs.getString("item_group_name"));
					return itemGroup;
				});
	}

	public int findItemSetSequence(Long itemId) {
		return jdbcTemplate.queryForObject(
				"SELECT item_set_sequence WHERE item_id = ? ORDER BY item_set_sequence DESC LIMIT 1",
				new Object[] { itemId }, Integer.class);

	}

	public int addItemSet(Long itemId, Long itemGroupId, int sequenceNumber) {
		return jdbcTemplate.update("INSERT INTO item_set(item_id, item_group_id, item_set_sequence) VALUES (?,?,?)",
				new Object[] { itemId, itemGroupId, sequenceNumber });
	}

	public int editItemSet(Long id, int sequenceNumber) {
		return jdbcTemplate.update("UPDATE item_set SET item_group_id = ? , item_set_sequence = ? WHERE id = ?",
				new Object[] { sequenceNumber, id });
	}

	public int removeItemSet(Long id) {
		return jdbcTemplate.update("DELETE FROM item_set WHERE id = ?", new Object[] { id });
	}

	public int removeItemSetByItemId(Long itemId) {
		return jdbcTemplate.update("DELETE FROM item_set WHERE item_id = ?", new Object[] { itemId });
	}

	public int removeItemSetByItemGroupId(Long itemGroupId) {
		return jdbcTemplate.update("DELETE FROM item_set WHERE item_group_id = ?", new Object[] { itemGroupId });
	}

	public void removeItemModifierGroupId(Long modifierGroupId) {
		jdbcTemplate.update("UPDATE item SET modifier_group_id = NULL WHERE modifier_group_id = ?",
				new Object[] { modifierGroupId });
	}

}
