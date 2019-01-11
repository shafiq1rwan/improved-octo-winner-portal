package my.com.byod.admin.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {

	public Long id;
	
	@JsonProperty("group_category_id")
	public Long groupCategoryId;
	
	@JsonProperty("tax_charge_id")
	public Long taxChargeId;
	
	@JsonProperty("backend_id")
	public String backendId;
	
	@JsonProperty("category_name")
	public String name;
	
	@JsonProperty("category_description")
	public String description;
	
	@JsonProperty("category_image_path")
	public String imagePath;
	
	@JsonProperty("category_sequence")
	public int sequence;
	
	@JsonProperty("is_active")
	public boolean isActive;
	
	@JsonProperty("created_date")
	public Date createdDate;

	public List<MenuItem> menuItems;

	public Category() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Long getGroupCategoryId() {
		return groupCategoryId;
	}

	public void setGroupCategoryId(Long groupCategoryId) {
		this.groupCategoryId = groupCategoryId;
	}

	public Long getTaxChargeId() {
		return taxChargeId;
	}

	public void setTaxChargeId(Long taxChargeId) {
		this.taxChargeId = taxChargeId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

}
