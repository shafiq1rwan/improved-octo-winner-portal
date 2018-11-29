package managepay.com.admin.byod.entity;

public class ChargeConfig {
	public int taxRate;
	public int serviceChargeRate;

	public ChargeConfig() {
	}

	public ChargeConfig(int taxRate, int serviceChargeRate) {
		this.taxRate = taxRate;
		this.serviceChargeRate = serviceChargeRate;
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
