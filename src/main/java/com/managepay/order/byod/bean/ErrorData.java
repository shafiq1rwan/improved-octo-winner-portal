package com.managepay.order.byod.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="error-data")
public class ErrorData {
	public String e1;
	public String e2;
	
	public String getE1() {
		return e1;
	}
	public void setE1(String e1) {
		this.e1 = e1;
	}
	public String getE2() {
		return e2;
	}
	public void setE2(String e2) {
		this.e2 = e2;
	}
}
