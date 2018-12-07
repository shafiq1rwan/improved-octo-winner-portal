package com.managepay.admin.byod.entity;

public class Staff {

	private Long id;
	private Long storeId;
	private String name;
	private String username;
	private String password;
	private String hpNumber;
	private String email;

	public Staff() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHpNumber() {
		return hpNumber;
	}

	public void setHpNumber(String hpNumber) {
		this.hpNumber = hpNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
