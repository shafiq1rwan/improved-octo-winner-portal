package com.managepay.admin.byod.service;

import java.util.List;

import com.managepay.admin.byod.entity.TaxCharge;

public interface ChargeConfigService {

	public List<TaxCharge> findAllChargeConfig();
	
	public TaxCharge findChargeConfigById(Long id);
	
	public int createChargeConfig(TaxCharge chargeConfig);
	
	public int editChargeConfig(Long id, TaxCharge chargeConfig);
	
	public int removeChargeConfig(Long id);
	
}
