package com.managepay.admin.byod.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.managepay.admin.byod.entity.ChargeConfig;

@Repository
public class ChargeConfigRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ChargeConfigRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private RowMapper<ChargeConfig> rowMapper = (rs, rowNum) -> {
		ChargeConfig chargeConfig = new ChargeConfig();
		chargeConfig.setId(rs.getLong("id"));
		chargeConfig.setName(rs.getString("charge_config_name"));
		chargeConfig.setTaxRate(rs.getInt("tax_rate"));
		chargeConfig.setServiceChargeRate(rs.getInt("service_charge_rate"));
		return chargeConfig;
	};
	
	public List<ChargeConfig> findAllChargeConfig(){
		return jdbcTemplate.query("SELECT * FROM charge_config", rowMapper);
	}
	
	public ChargeConfig findChargeConfigById(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM charge_config WHERE id = ?", new Object[] {id}, rowMapper);
	}

	public int createChargeConfig(ChargeConfig chargeConfig) {
		return jdbcTemplate.update(
				"INSERT INTO charge_config(charge_config_name, tax_rate, service_charge_rate) VALUES(?,?,?)",
				new Object[] { chargeConfig.getName(), chargeConfig.getTaxRate(),
						chargeConfig.getServiceChargeRate() });
	}

	public int editChargeConfig(Long id, ChargeConfig chargeConfig) {
		return jdbcTemplate.update(
				"UPDATE charge_config SET charge_config_name=?, tax_rate = ?, service_charge_rate = ? WHERE id = ?",
				new Object[] { chargeConfig.getName(), chargeConfig.getTaxRate(), chargeConfig.getServiceChargeRate(),
						id });
	}

	public int removeChargeConfig(Long id) {
		return jdbcTemplate.update("DELETE FROM charge_config WHERE id = ?", new Object[] { id });
	}
}
