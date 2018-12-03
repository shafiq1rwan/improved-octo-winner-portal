package com.managepay.admin.byod.entity;

import java.util.Date;

public class Store {

	private Long id;
	private String backendId;
	private String name;
	private String logoPath;
	private String backgroundImagePath;
	private Location location;
	private String currency;
	private boolean isPublished;

	public Store() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBackendId() {
		return backendId;
	}

	public void setBackendId(String backendId) {
		this.backendId = backendId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public String getBackgroundImagePath() {
		return backgroundImagePath;
	}

	public void setBackgroundImagePath(String backgroundImagePath) {
		this.backgroundImagePath = backgroundImagePath;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

}
