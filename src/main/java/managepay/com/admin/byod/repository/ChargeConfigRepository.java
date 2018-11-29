package managepay.com.admin.byod.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import managepay.com.admin.byod.entity.ChargeConfig;

public class ChargeConfigRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ChargeConfigRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private RowMapper<ChargeConfig> rowMapper = (rs, rowNum) -> {
		ChargeConfig chargeConfig = new ChargeConfig();
		chargeConfig.setTaxRate(rs.getInt("tax_rate"));
		chargeConfig.setServiceChargeRate(rs.getInt("servive_charge_rate"));
		return chargeConfig;
	};
	
	public ChargeConfig findChargeConfig() {
		return jdbcTemplate.queryForObject("SELECT * FROM charge_config", rowMapper);
	}

	public int editChargeConfig(ChargeConfig chargeConfig) {
		return jdbcTemplate.update("UPDATE charge_config SET tax_rate = ?, service_charge_rate = ?",
				new Object[] { chargeConfig.getTaxRate(), chargeConfig.getServiceChargeRate() });
	}
}
