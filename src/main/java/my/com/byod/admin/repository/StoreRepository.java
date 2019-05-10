package my.com.byod.admin.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import my.com.byod.admin.entity.Location;
import my.com.byod.admin.entity.Store;
import my.com.byod.admin.util.DbConnectionUtil;

@Repository
public class StoreRepository {

	private DbConnectionUtil dbConnectionUtil;
	private HttpServletRequest request;

	@Autowired
	public StoreRepository(DbConnectionUtil dbConnectionUtil, HttpServletRequest request) {
		this.dbConnectionUtil = dbConnectionUtil;
		this.request = request;
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
		store.setPublish(rs.getBoolean("is_publish"));
		store.setCreatedDate(rs.getDate("created_date"));
		store.setOperatingStartTime(rs.getTime("store_start_operating_time"));
		store.setOperatingEndTime(rs.getTime("store_end_operating_time"));
		store.setEcpos(rs.getBoolean("ecpos"));
		store.setEcposUrl(rs.getString("ecpos_url"));
		store.setContactPerson(rs.getString("store_contact_person"));
		store.setMobileNumber(rs.getString("store_contact_hp_number"));
		store.setEmail(rs.getString("store_contact_email"));
		store.setStoreTypeId(rs.getLong("store_type_id"));
		store.setKioskPaymentDelayId(rs.getLong("kiosk_payment_delay_id"));
		store.setByodPaymentDelayId(rs.getLong("byod_payment_delay_id"));
		store.setStoreTaxTypeId(rs.getLong("store_tax_type_id"));
		return store;
	};

	public List<Store> findAllStore() {	
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.query("SELECT * FROM store",
				rowMapper);
	}

	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.query("SELECT * FROM store WHERE group_category_id = ?", new Object[] { groupCategoryId },
				rowMapper);
	}

	public Store findStoreById(Long id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
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
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update(
				"INSERT INTO store(backend_id,store_name,store_logo_path,store_address,store_longitude,store_latitude,store_country,store_currency, "
				+ "is_publish, store_start_operating_time, store_end_operating_time, ecpos, store_contact_person, store_contact_hp_number, store_contact_email, store_type_id, kiosk_payment_delay_id, byod_payment_delay_id, store_tax_type_id, ecpos_url, created_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE());",
				new Object[] { store.getBackendId(), store.getName(), store.getLogoPath(),
						store.getLocation().getAddress(), store.getLocation().getLongitude(),
						store.getLocation().getLatitude(), store.getLocation().getCountry(), store.getCurrency(),
						store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos(), store.getContactPerson(), store.getMobileNumber(), store.getEmail(), store.getStoreTypeId(), store.getKioskPaymentDelayId(), store.getByodPaymentDelayId(), store.getStoreTaxTypeId(), store.getEcposUrl()});
		
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
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update(
				"UPDATE store SET store_name = ?,store_logo_path = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ?, store_contact_person = ?, store_contact_hp_number = ?, store_contact_email = ?, store_type_id = ?, kiosk_payment_delay_id = ?, byod_payment_delay_id = ?, store_tax_type_id = ?, ecpos_url = ? WHERE id = ?",
				new Object[] { store.getName(), image, store.getLocation().getAddress(),
						store.getLocation().getLongitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(),
						store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos(), store.getContactPerson(), store.getMobileNumber(), store.getEmail(), store.getStoreTypeId(), store.getKioskPaymentDelayId(), store.getByodPaymentDelayId(), store.getStoreTaxTypeId(), store.getEcposUrl(), id });
	}
	
	public int editStore(Long id, Store store) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update(
				"UPDATE store SET store_name = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ?, store_contact_person = ?, store_contact_hp_number = ?, store_contact_email = ?, store_type_id = ?, kiosk_payment_delay_id = ?, byod_payment_delay_id = ?, store_tax_type_id = ?, ecpos_url = ? WHERE id = ?",
				new Object[] { store.getName(), store.getLocation().getAddress(),
						store.getLocation().getLongitude(), store.getLocation().getLatitude(),
						store.getLocation().getCountry(), store.getCurrency(),
						store.isPublish(), store.getOperatingStartTime(), store.getOperatingEndTime(), store.getEcpos(), store.getContactPerson(), store.getMobileNumber(), store.getEmail(), store.getStoreTypeId(), store.getKioskPaymentDelayId(), store.getByodPaymentDelayId(), store.getStoreTaxTypeId(), store.getEcposUrl(), id });
	}

	public int editStoreGroupCategoryId(Long groupCategoryId, Long id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update("UPDATE store SET group_category_id = ? WHERE id = ?",
				new Object[] { groupCategoryId, id });
	}

	public int editStoreGroupCategoryIdInBatch(Long groupCategoryId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update("UPDATE store SET group_category_id = 0 WHERE group_category_id = ?",
				new Object[] { groupCategoryId });
	}

	public int removeStore(Long id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.update("DELETE FROM store WHERE id = ?", new Object[] { id });
	}

}
