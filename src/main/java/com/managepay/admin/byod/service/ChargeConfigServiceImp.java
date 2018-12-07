package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.TaxCharge;
import com.managepay.admin.byod.repository.TaxChargeRepository;

@Service
public class ChargeConfigServiceImp implements ChargeConfigService {

	private TaxChargeRepository chargeConfigRepo;

	@Autowired
	public ChargeConfigServiceImp(TaxChargeRepository chargeConfigRepo) {
		this.chargeConfigRepo = chargeConfigRepo;
	}

	@Override
	public List<TaxCharge> findAllChargeConfig() {
		try {
			return chargeConfigRepo.findAllChargeConfig();
		} catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public TaxCharge findChargeConfigById(Long id) {
		try {
			return chargeConfigRepo.findChargeConfigById(id);
		} catch(Exception ex) {
			ex.printStackTrace();
			return new TaxCharge();
		}
	}

	@Override
	public int createChargeConfig(TaxCharge chargeConfig) {
		try {
			return chargeConfigRepo.createChargeConfig(chargeConfig);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editChargeConfig(Long id, TaxCharge chargeConfig) {
		try {
			return chargeConfigRepo.editChargeConfig(id, chargeConfig);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int removeChargeConfig(Long id) {
		try {
			return chargeConfigRepo.removeChargeConfig(id);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
