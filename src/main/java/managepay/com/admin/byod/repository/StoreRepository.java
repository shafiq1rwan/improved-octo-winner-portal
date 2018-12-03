package managepay.com.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import managepay.com.admin.byod.entity.Location;
import managepay.com.admin.byod.entity.Store;

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
		store.setBackendId(rs.getString("backend_id"));
		store.setName(rs.getString("store_name"));
		store.setLogoPath(rs.getString("store_logo_path"));
		store.setBackgroundImagePath(rs.getString("store_background_image_path"));
		Location location = new Location();
		location.setAddress(rs.getString(rs.getString("store_address")));
		location.setLat(rs.getDouble("stote_lat"));
		location.setLng(rs.getDouble("store_lng"));
		location.setCountry(rs.getString("store_country"));
		store.setLocation(location);
		store.setCurrency(rs.getString("store_currency"));
		store.setPublished(rs.getBoolean("published"));
		return store;
	};

	public List<Store> findAllStore() {
		return jdbcTemplate.query("SELECT * FROM store", rowMapper);
	}

	public Store findStoreById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM store WHERE id = ?", new Object[] { id }, rowMapper);
	}

	public int createStore(Store store) {
		return jdbcTemplate.update(
				"INSERT INTO store(backend_id,store_name,store_logo_path,store_background_image_path,store_address,store_lng,stote_lat,store_country,store_currency,published) VALUES (?,?,?,?,?,?,?,?,?,?)",
				new Object[] { store.getBackendId(), store.getName(), store.getLogoPath(),
						store.getBackgroundImagePath(), store.getLocation().getAddress(), store.getLocation().getLng(),
						store.getLocation().getLat(), store.getLocation().getCountry(), store.getCurrency(),
						store.isPublished() });
	}

	public int editStore(Long id, Store store) {
		return jdbcTemplate.update(
				"UPDATE store SET backend_id = ?,store_name =?,store_logo_path =?,store_background_image_path =?,store_address =?,store_lng = ?,stote_lat =?,store_country = ?,store_currency =?,published = ? WHERE id = ?",
				new Object[] { store.getBackendId(), store.getName(), store.getLogoPath(),
						store.getBackgroundImagePath(), store.getLocation().getAddress(), store.getLocation().getLng(),
						store.getLocation().getLat(), store.getLocation().getCountry(), store.getCurrency(),
						store.isPublished(), id });
	}
	
	public int removeStore(Long id) {
		return jdbcTemplate.update("DELETE FROM store WHERE id = ?", new Object[] {id});
	}

}
