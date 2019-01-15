package my.com.byod.admin.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import my.com.byod.admin.entity.Location;
import my.com.byod.admin.entity.Store;

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
		location.setAddress(rs.getString("store_address"));
		location.setLongitude(rs.getDouble("store_longitude"));
		location.setLatitude(rs.getDouble("store_latitude"));
		location.setCountry(rs.getString("store_country"));
		store.setLocation(location);
		store.setCurrency(rs.getString("store_currency"));
		store.setTableCount(rs.getInt("store_table_count"));
		store.setPublish(rs.getBoolean("is_publish"));
		store.setCreatedDate(rs.getDate("created_date"));
		store.setOperatingStartTime(rs.getTime("store_start_operating_time"));
		store.setOperatingEndTime(rs.getTime("store_end_operating_time"));
		store.setEcpos(rs.getBoolean("ecpos"));
		return store;
	};

	public List<Store> findAllStore() {
		return jdbcTemplate.query("SELECT * FROM store",
				rowMapper);
	}

	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId) {
		return jdbcTemplate.query("SELECT * FROM store WHERE group_category_id = ?", new Object[] { groupCategoryId },
				rowMapper);
	}

	public Store findStoreById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM store WHERE id = ?", new Object[] { id }, rowMapper);
	}

	public int createStore(Store store) {
		/*System.out.println("store.getBackendId(): " + store.getBackendId());
		System.out.println("store.getName(): " + store.getName());
		System.out.println("store.getLogoPath(): " + store.getLogoPath());
		System.out.println("store.getLocation().getAddress: " + store.getLocation().getAddress());
		System.out.println("store.getLongitude(): " + store.getLocation().getLongitude());
		System.out.println("store.getLatitude(): " + store.getLocation().getLatitude());
		System.out.println("store.getCurrency(): " + store.getCurrency());
		System.out.println("store.getTableCount(): " + store.getTableCount());
		System.out.println("store.isPublish(): " + store.isPublish());
		System.out.println("store.getOperatingStartTime(): " + store.getOperatingStartTime());
		System.out.println("store.getOperatingEndTime(): " + store.getOperatingEndTime());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String INSERT_SQL = "INSERT INTO store(backend_id,store_name,store_logo_path,store_address,store_longitude,store_latitude,store_country,store_currency, "
				+ "store_table_count, is_publish, store_start_operating_time, store_end_operating_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
		*/
		return jdbcTemplate.update(
				"INSERT INTO store(backend_id,store_name,store_logo_path,store_address,store_longitude,store_latitude,store_country,store_currency, "
				+ "store_table_count, is_publish, store_start_operating_time, store_end_operating_time, ecpos) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);",
				new Object[] { store.getBackendId(), store.getName(), store.getLogoPath(),
						store.getLocation().getAddress(), store.getLocation().getLongitude(),
						store.getLocation().getLatitude(), store.getLocation().getCountry(), store.getCurrency(),
						store.getTableCount(), store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos()});
		
		/*jdbcTemplate.update( new PreparedStatementCreator() {
	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
	            PreparedStatement ps =
	                connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
	            ps.setString(1, store.getBackendId());
	            ps.setString(2, store.getName());
	            ps.setString(3, store.getLogoPath());
	            ps.setString(4, store.getLocation().getAddress());
	            ps.setDouble(5, store.getLocation().getLongitude());
	            ps.setDouble(6, store.getLocation().getLatitude());
	            ps.setString(7, store.getLocation().getCountry());
	            ps.setString(8, store.getCurrency());
	            ps.setInt(9, store.getTableCount());
	            ps.setBoolean(10, store.isPublish());
	            ps.setTime(11, new Time(store.getOperatingStartTime().getTime()));
	            ps.setTime(12, new Time(store.getOperatingEndTime().getTime()));
	            return ps;
	        }
	    },
	    keyHolder);
		System.out.println("key:" + (int) keyHolder.getKey().longValue());
		return (int) keyHolder.getKey().longValue();*/
	}

	public int editStore(Long id, Store store, String image) {
		return jdbcTemplate.update(
				"UPDATE store SET store_name = ?,store_logo_path = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, store_table_count = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ? WHERE id = ?",
				new Object[] { store.getName(), image, store.getLocation().getAddress(),
						store.getLocation().getLongitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(), store.getTableCount(),
						store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos(), id });
	}
	
	public int editStore(Long id, Store store) {
		return jdbcTemplate.update(
				"UPDATE store SET store_name = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, store_table_count = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ? WHERE id = ?",
				new Object[] { store.getName(), store.getLocation().getAddress(),
						store.getLocation().getLongitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(), store.getTableCount(),
						store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos(), id });
	}

	public int editStoreGroupCategoryId(Long groupCategoryId, Long id) {
		return jdbcTemplate.update("UPDATE store SET group_category_id = ? WHERE id = ?",
				new Object[] { groupCategoryId, id });
	}

	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId) {
		return jdbcTemplate.update("UPDATE store SET group_category_id = 0 WHERE group_category_id = ?",
				new Object[] { groupCategoryId });
	}

	public int removeStore(Long id) {
		return jdbcTemplate.update("DELETE FROM store WHERE id = ?", new Object[] { id });
	}

}