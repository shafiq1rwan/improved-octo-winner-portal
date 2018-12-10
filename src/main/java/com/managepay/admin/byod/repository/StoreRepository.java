package com.managepay.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.Location;
import com.managepay.admin.byod.entity.Store;

@Repository
public class StoreRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public StoreRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<Store> rowMapper = (rs, rowNum) -> {
		Store store = new Store();
		store.setId(rs.getLong("id"));
		store.setGroupCategoryId(rs.getLong("group_category_id"));
		store.setBackendId(rs.getString("backend_id"));
		store.setName(rs.getString("store_name"));
		store.setLogoPath(rs.getString("store_logo_path"));
		Location location = new Location();
		location.setAddress(rs.getString(rs.getString("store_address")));
		location.setLongtitude(rs.getDouble("store_longtitude"));
		location.setLatitude(rs.getDouble("store_latitude"));
		location.setCountry(rs.getString("store_country"));
		store.setLocation(location);
		store.setCurrency(rs.getString("store_currency"));
		store.setTableCount(rs.getInt("store_table_count"));
		store.setPublished(rs.getBoolean("is_publish"));
		return store;
	};

	public List<Store> findAllStore() {
		return jdbcTemplate.query("SELECT * FROM store WHERE group_category_id = NULL OR group_category_id = 0",
				rowMapper);
	}
	
	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId){
		return jdbcTemplate.query("SELECT * FROM store WHERE group_category_id = ?", new Object[] {groupCategoryId},
				rowMapper);
	}

	public Store findStoreById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM store WHERE id = ?", new Object[] { id }, rowMapper);
	}
	
	public int createStore(Store store) {
		return jdbcTemplate.update(
				"INSERT INTO store(group_category_id, backend_id,store_name,store_logo_path,store_address,store_longtitude,stote_latitude,store_country,store_currency, store_table_count, is_published) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { store.getGroupCategoryId(), store.getBackendId(), store.getName(), store.getLogoPath(), store.getLocation().getAddress(),
						store.getLocation().getLongtitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(), store.getTableCount(),
						store.isPublished() });
	}

	public int editStore(Long id, Store store) {
		return jdbcTemplate.update(
				"UPDATE store SET group_category_id = ?, backend_id = ?,store_name = ?,store_logo_path = ?,store_address = ?,store_longtitude = ?,stote_latitude = ?,store_country = ?,store_currency = ?, store_table_count = ?, is_published = ? WHERE id = ?",
				new Object[] { store.getGroupCategoryId(), store.getBackendId(), store.getName(), store.getLogoPath(),
						 store.getLocation().getAddress(),
						store.getLocation().getLongtitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(), store.getTableCount(),
						store.isPublished(), id });
	}

	public int editStoreGroupCategoryId(Long groupCategoryId, Long id) {
		return jdbcTemplate.update("UPDATE store SET group_category_id = ? WHERE id = ?",
				new Object[] { groupCategoryId, id });
	}
	
	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId) {
		return jdbcTemplate.update("UPDATE store SET group_category_id = 0 WHERE group_category_id = ?",
				new Object[] { groupCategoryId});
	}

	public int removeStore(Long id) {
		return jdbcTemplate.update("DELETE FROM store WHERE id = ?", new Object[] { id });
	}

}
