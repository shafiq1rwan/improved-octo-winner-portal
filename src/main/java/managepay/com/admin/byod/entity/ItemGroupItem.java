package managepay.com.admin.byod.entity;

public class ItemGroupItem {

	public Long id;
	public Long itemId;
	public Long itemGroupId;
	public int sequence;

	public ItemGroupItem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(Long itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
