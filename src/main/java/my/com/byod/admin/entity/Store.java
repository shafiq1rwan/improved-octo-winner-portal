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
	
	@JsonProperty("ecpos_takeaway_detail_flag")
	private boolean ecpos_takeaway_detail_flag;
	
	@JsonProperty("login_type_id")
	private Long login_type_id;
	
	@JsonProperty("login_switch_flag")
	private boolean login_switch_flag;
	
	@JsonProperty("store_contact_person")
	private String contactPerson;
	
	@JsonProperty("store_contact_hp_number")
	private String mobileNumber;
	
	@JsonProperty("store_contact_email")
	private String email;
	
	@JsonProperty("store_type_id")
	private Long storeTypeId;
	
	@JsonProperty("kiosk_payment_delay_id")
	private Long kioskPaymentDelayId;
	
	@JsonProperty("byod_payment_delay_id")
	private Long byodPaymentDelayId;
	
	@JsonProperty("store_tax_type_id")
	private Long storeTaxTypeId;
	
	@JsonProperty("store_state")
	private Integer storeState; 
	
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
	
	public boolean getEcposTakeawayDetailFlag() {
		return ecpos_takeaway_detail_flag;
	}
	
	public void setEcposTakeawayDetailFlag(boolean ecpos_takeaway_detail_flag) {
		this.ecpos_takeaway_detail_flag = ecpos_takeaway_detail_flag;
	}
	
	public Long getLoginTypeId() {
		return login_type_id;
	}
	
	public void setLoginTypeId(Long login_type_id) {
		this.login_type_id = login_type_id;
	}
	
	public boolean getLoginSwitchFlag() {
		return login_switch_flag;
	}
	
	public void setLoginSwitchFlag(boolean login_switch_flag) {
		this.login_switch_flag = login_switch_flag;
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
	
	public Long getStoreTypeId() {
		return storeTypeId;
	}

	public void setStoreTypeId(Long storeTypeId) {
		this.storeTypeId = storeTypeId;
	}
	
	public Long getKioskPaymentDelayId() {
		return kioskPaymentDelayId;
	}

	public void setKioskPaymentDelayId(Long kioskPaymentDelayId) {
		this.kioskPaymentDelayId = kioskPaymentDelayId;
	}
	
	public Long getByodPaymentDelayId() {
		return byodPaymentDelayId;
	}

	public void setByodPaymentDelayId(Long byodPaymentDelayId) {
		this.byodPaymentDelayId = byodPaymentDelayId;
	}
	
	public Long getStoreTaxTypeId() {
		return storeTaxTypeId;
	}

	public void setStoreTaxTypeId(Long storeTaxTypeId) {
		this.storeTaxTypeId = storeTaxTypeId;
	}

	public Integer getStoreState() {
		return storeState;
	}

	public void setStoreState(Integer storeState) {
		this.storeState = storeState;
	}

}
