package managepay.com.admin.byod.entity;

import java.math.BigDecimal;

public class ItemPricing {

	private Long id;
	private Long submenuId;
	private Long itemId;
	private BigDecimal price;

	public ItemPricing() {
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
