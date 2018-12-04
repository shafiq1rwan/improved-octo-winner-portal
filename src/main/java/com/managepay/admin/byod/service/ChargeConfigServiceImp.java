package com.managepay.admin.byod.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.managepay.admin.byod.entity.ChargeConfig;
import com.managepay.admin.byod.repository.ChargeConfigRepository;

@Service
public class ChargeConfigServiceImp implements ChargeConfigService {

	private ChargeConfigRepository chargeConfigRepo;

	@Autowired
	public ChargeConfigServiceImp(ChargeConfigRepository chargeConfigRepo) {
		this.chargeConfigRepo = chargeConfigRepo;
	}

	@Override
	public List<ChargeConfig> findAllChargeConfig() {
		try {
			return chargeConfigRepo.findAllChargeConfig();
		} catch(Exception ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public ChargeConfig findChargeConfigById(Long id) {
		try {
			return chargeConfigRepo.findChargeConfigById(id);
		} catch(Exception ex) {
			ex.printStackTrace();
			return new ChargeConfig();
		}
	}

	@Override
	public int createChargeConfig(ChargeConfig chargeConfig) {
		try {
			return chargeConfigRepo.createChargeConfig(chargeConfig);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	public int editChargeConfig(Long id, ChargeConfig chargeConfig) {
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
