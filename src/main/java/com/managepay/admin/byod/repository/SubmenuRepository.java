package com.managepay.admin.byod.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.Submenu;
import com.managepay.admin.byod.entity.SubmenuCreator;

@Repository
public class SubmenuRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public SubmenuRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Submenu Creator CRUD
	public int createSubmenuCreator(SubmenuCreator submenuCreator) {
		return jdbcTemplate.update("INSERT INTO submenu_creator(submenu_name) VALUES(?)",
				new Object[] { submenuCreator.getName() });
	}

	public int editSubmenuCreator(Long id, SubmenuCreator submenuCreator) {
		return jdbcTemplate.update("UPDATE submenu_creator SET submenu_name = ? WHERE id = ?",
				new Object[] { submenuCreator.getName(), id });
	}

	public int removeSubmenuCreator(Long id) {
		return jdbcTemplate.update("DELETE FROM submenu_creator WHERE id = ?", new Object[] { id });
	}

	// Submenu CRUD
	public int createSubmenu(Submenu submenu) {
		return jdbcTemplate.update("INSERT INTO submenu(store_id, submenu_creator_id) VALUES(?,?)",
				new Object[] { submenu.getStoreId(), submenu.getSubmenuCreatorId() });
	}

	public int editSubmenu(Long id, Submenu submenu) {
		return jdbcTemplate.update("UPDATE submenu SET store_id = ?, submenu_creator_id = ? WHERE id = ?",
				new Object[] { submenu.getStoreId(), submenu.getSubmenuCreatorId(), id });
	}

	public int removeSubmenu(Long id) {
		return jdbcTemplate.update("DELETE FROM submenu WHERE id = ?", new Object[] { id });
	}
	
	public int removeSubmenuBySubmenuCreatorId(Long submenuCreatorId) {
		return jdbcTemplate.update("DELETE FROM submenu WHERE submenu_creator_id = ?", new Object[] { submenuCreatorId });
	}
	
	public int removeSubmenuByStoreId(Long storeId) {
		return jdbcTemplate.update("DELETE FROM submenu WHERE store_id = ?", new Object[] { storeId });
	}

}
