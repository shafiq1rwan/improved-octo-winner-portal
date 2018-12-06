package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.ChargeConfig;

public interface ChargeConfigService {

	public List<ChargeConfig> findAllChargeConfig();
	
	public ChargeConfig findChargeConfigById(Long id);
	
	public int createChargeConfig(ChargeConfig chargeConfig);
	
	public int editChargeConfig(Long id, ChargeConfig chargeConfig);
	
	public int removeChargeConfig(Long id);
	
}
