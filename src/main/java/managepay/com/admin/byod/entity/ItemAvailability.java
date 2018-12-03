package managepay.com.admin.byod.entity;

public class ItemAvailability {
	private Long id;
	private Long submenuId;
	private Long itemId;
	private String status;

	public ItemAvailability() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubmenuId() {
		return submenuId;
	}

	public void setSubmenuId(Long submenuId) {
		this.submenuId = submenuId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
