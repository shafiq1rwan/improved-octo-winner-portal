package my.com.byod.admin.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import my.com.byod.admin.entity.TaxCharge;

@Repository
public class TaxChargeRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public TaxChargeRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<TaxCharge> rowMapper = (rs, rowNum) -> {
		TaxCharge chargeConfig = new TaxCharge();
		chargeConfig.setId(rs.getLong("id"));
		chargeConfig.setName(rs.getString("charge_config_name"));
		chargeConfig.setTaxRate(rs.getInt("tax_rate"));
		chargeConfig.setServiceChargeRate(rs.getInt("service_charge_rate"));
		return chargeConfig;
	};
	
	public List<TaxCharge> findAllChargeConfig(){
		return jdbcTemplate.query("SELECT * FROM charge_config", rowMapper);
	}
	
	public TaxCharge findChargeConfigById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM charge_config WHERE id = ?", new Object[] {id}, rowMapper);
	}

	public int createChargeConfig(TaxCharge chargeConfig) {
		return jdbcTemplate.update(
				"INSERT INTO charge_config(charge_config_name, tax_rate, service_charge_rate) VALUES(?,?,?)",
				new Object[] { chargeConfig.getName(), chargeConfig.getTaxRate(),
						chargeConfig.getServiceChargeRate() });
	}

	public int editChargeConfig(Long id, TaxCharge chargeConfig) {
		return jdbcTemplate.update(
				"UPDATE charge_config SET charge_config_name=?, tax_rate = ?, service_charge_rate = ? WHERE id = ?",
				new Object[] { chargeConfig.getName(), chargeConfig.getTaxRate(), chargeConfig.getServiceChargeRate(),
						id });
	}

	public int removeChargeConfig(Long id) {
		return jdbcTemplate.update("DELETE FROM charge_config WHERE id = ?", new Object[] { id });
	}
}
