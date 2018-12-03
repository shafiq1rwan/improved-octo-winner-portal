package com.managepay.admin.byod.entity;

public class ChargeConfig {

	public Long id;
	public String name;
	public int taxRate;
	public int serviceChargeRate;

	public ChargeConfig() {
	}

	public ChargeConfig(int taxRate, int serviceChargeRate) {
		this.taxRate = taxRate;
		this.serviceChargeRate = serviceChargeRate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}

	public int getServiceChargeRate() {
		return serviceChargeRate;
	}

	public void setServiceChargeRate(int serviceChargeRate) {
		this.serviceChargeRate = serviceChargeRate;
	}

}
