package my.com.byod.admin.entity;

import java.sql.Time;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Store {

	
	private Long id;
	
	@JsonProperty("group_category_id")
	private Long groupCategoryId;
	
	@JsonProperty("backend_id")
	private String backendId;
	
	@JsonProperty("store_name")
	private String name;
	
	@JsonProperty("store_logo_path")
	private String logoPath;
	
	private Location location;
	
	@JsonProperty("store_currency")
	private String currency;
	
	@JsonProperty("store_table_count")
	private int tableCount;

	@JsonProperty("is_publish")
	private boolean isPublish;

	@JsonProperty("created_date")
	private Date createdDate;
	
	@JsonProperty("store_start_operating_time")
	private Date operatingStartTime;
	
	@JsonProperty("store_end_operating_time")
	private Date operatingEndTime;
	
	@JsonProperty("store_ecpos")
	private boolean ecpos;
	
	@JsonProperty("store_contact_person")
	private String contactPerson;
	
	@JsonProperty("store_contact_hp_number")
	private String mobileNumber;
	
	@JsonProperty("store_contact_email")
	private String email;
	
	public Store() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupCategoryId() {
		return groupCategoryId;
	}

	public void setGroupCategoryId(Long groupCategoryId) {
		this.groupCategoryId = groupCategoryId;
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

	public int getTableCount() {
		return tableCount;
	}

	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
	}

	public boolean isPublish() {
		return isPublish;
	}

	public void setPublish(boolean isPublish) {
		this.isPublish = isPublish;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public Date getOperatingStartTime() {
		return operatingStartTime;
	}

	public void setOperatingStartTime(Date operatingStartTime) {
		this.operatingStartTime = operatingStartTime;
	}
	
	public Date getOperatingEndTime() {
		return operatingEndTime;
	}

	public void setOperatingEndTime(Date operatingEndTime) {
		this.operatingEndTime = operatingEndTime;
	}
	
	public boolean getEcpos() {
		return ecpos;
	}

	public void setEcpos(boolean ecpos) {
		this.ecpos = ecpos;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
