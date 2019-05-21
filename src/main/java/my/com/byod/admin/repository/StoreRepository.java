package my.com.byod.admin.repository;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
		store.setEcposTakeawayDetailFlag(rs.getBoolean("ecpos_takeaway_detail_flag"));
		store.setLoginTypeId(rs.getLong("login_type_id"));
		store.setLoginSwitchFlag(rs.getBoolean("login_switch_flag"));
		store.setContactPerson(rs.getString("store_contact_person"));
		store.setMobileNumber(rs.getString("store_contact_hp_number"));
		store.setEmail(rs.getString("store_contact_email"));
		store.setStoreTypeId(rs.getLong("store_type_id"));
		store.setKioskPaymentDelayId(rs.getLong("kiosk_payment_delay_id"));
		store.setByodPaymentDelayId(rs.getLong("byod_payment_delay_id"));
		store.setStoreTaxTypeId(rs.getLong("store_tax_type_id"));
		return store;
	};

	public List<Store> findStoresByGroupCategoryId(Long groupCategoryId) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		return jdbcTemplate.query("SELECT * FROM store WHERE group_category_id = ?", new Object[] { groupCategoryId },
				rowMapper);
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
