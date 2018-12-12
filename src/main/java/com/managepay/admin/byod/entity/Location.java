package com.managepay.admin.byod.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {

	@JsonProperty("store_address")
	private String address;
	
	@JsonProperty("store_country")
	private String country;
	
	@JsonProperty("store_longitude")
	private double longitude;
	
	@JsonProperty("store_latitude")
	private double latitude;

	public Location() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
