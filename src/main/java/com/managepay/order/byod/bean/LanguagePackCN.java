package com.managepay.order.byod.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="language-pack-cn")
public class LanguagePackCN {
	public String packName;
	public String packShortName;
	
	public String landing_storeName;
	public String landing_table;
	public String landing_poweredBy;
	public String landing_orderNow;
	
	
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public String getPackShortName() {
		return packShortName;
	}
	public void setPackShortName(String packShortName) {
		this.packShortName = packShortName;
	}
	public String getLanding_storeName() {
		return landing_storeName;
	}
	public void setLanding_storeName(String landing_storeName) {
		this.landing_storeName = landing_storeName;
	}
	public String getLanding_table() {
		return landing_table;
	}
	public void setLanding_table(String landing_table) {
		this.landing_table = landing_table;
	}
	public String getLanding_poweredBy() {
		return landing_poweredBy;
	}
	public void setLanding_poweredBy(String landing_poweredBy) {
		this.landing_poweredBy = landing_poweredBy;
	}
	public String getLanding_orderNow() {
		return landing_orderNow;
	}
	public void setLanding_orderNow(String landing_orderNow) {
		this.landing_orderNow = landing_orderNow;
	}
}
