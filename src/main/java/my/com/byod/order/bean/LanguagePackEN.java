package my.com.byod.order.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="language-pack-en")
public class LanguagePackEN {
	public String packName;
	public String packShortName;
	
	public String landing_storeName;
	public String landing_table;
	public String landing_poweredBy;
	public String landing_orderNow;
	
	public String item_detail_comboPrice;
	public String item_detail_alacartePrice;
	public String item_detail_totalPrice;
	public String item_detail_select;
	public String item_detail_addToCart;
	public String item_detail_quantity;
	
	public String tier_selection_selection;
	public String tier_selection_itemRemaining;
	public String tier_selection_done;
	
	public String cart_empty_message;
	public String cart_summary;
	public String cart_total;
	public String cart_subtotal;
	public String cart_send_order;
	public String cart_pay_now;
	public String cart_pay_later;
	public String cart_checkout;
	
	public String edit_item_detail_editCart;
	
	public String dialog_reset_cart_title;
	public String dialog_cart_add_success_title;
	public String dialog_cart_edit_success_title;
	public String dialog_delete_cart_item_title;
	public String dialog_send_order_success_title;
	public String dialog_send_order_failed_title;
	public String dialog_button_ok;
	public String dialog_button_yes;
	public String dialog_button_no;
	
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public String getPackShortName() {
		return packShortName;
	}
	public void setPackShortName(String packShortName) {
		this.packShortName = packShortName;
	}
	public String getLanding_storeName() {
		return landing_storeName;
	}
	public void setLanding_storeName(String landing_storeName) {
		this.landing_storeName = landing_storeName;
	}
	public String getLanding_table() {
		return landing_table;
	}
	public void setLanding_table(String landing_table) {
		this.landing_table = landing_table;
	}
	public String getLanding_poweredBy() {
		return landing_poweredBy;
	}
	public void setLanding_poweredBy(String landing_poweredBy) {
		this.landing_poweredBy = landing_poweredBy;
	}
	public String getLanding_orderNow() {
		return landing_orderNow;
	}
	public void setLanding_orderNow(String landing_orderNow) {
		this.landing_orderNow = landing_orderNow;
	}
	public String getItem_detail_comboPrice() {
		return item_detail_comboPrice;
	}
	public void setItem_detail_comboPrice(String item_detail_comboPrice) {
		this.item_detail_comboPrice = item_detail_comboPrice;
	}
	public String getItem_detail_alacartePrice() {
		return item_detail_alacartePrice;
	}
	public void setItem_detail_alacartePrice(String item_detail_alacartePrice) {
		this.item_detail_alacartePrice = item_detail_alacartePrice;
	}
	public String getItem_detail_totalPrice() {
		return item_detail_totalPrice;
	}
	public void setItem_detail_totalPrice(String item_detail_totalPrice) {
		this.item_detail_totalPrice = item_detail_totalPrice;
	}
	public String getItem_detail_select() {
		return item_detail_select;
	}
	public void setItem_detail_select(String item_detail_select) {
		this.item_detail_select = item_detail_select;
	}
	public String getItem_detail_addToCart() {
		return item_detail_addToCart;
	}
	public void setItem_detail_addToCart(String item_detail_addToCart) {
		this.item_detail_addToCart = item_detail_addToCart;
	}
	public String getItem_detail_quantity() {
		return item_detail_quantity;
	}
	public void setItem_detail_quantity(String item_detail_quantity) {
		this.item_detail_quantity = item_detail_quantity;
	}
	public String getTier_selection_selection() {
		return tier_selection_selection;
	}
	public void setTier_selection_selection(String tier_selection_selection) {
		this.tier_selection_selection = tier_selection_selection;
	}
	public String getTier_selection_itemRemaining() {
		return tier_selection_itemRemaining;
	}
	public void setTier_selection_itemRemaining(String tier_selection_itemRemaining) {
		this.tier_selection_itemRemaining = tier_selection_itemRemaining;
	}
	public String getTier_selection_done() {
		return tier_selection_done;
	}
	public void setTier_selection_done(String tier_selection_done) {
		this.tier_selection_done = tier_selection_done;
	}
	public String getCart_empty_message() {
		return cart_empty_message;
	}
	public void setCart_empty_message(String cart_empty_message) {
		this.cart_empty_message = cart_empty_message;
	}
	public String getCart_summary() {
		return cart_summary;
	}
	public void setCart_summary(String cart_summary) {
		this.cart_summary = cart_summary;
	}
	public String getCart_total() {
		return cart_total;
	}
	public void setCart_total(String cart_total) {
		this.cart_total = cart_total;
	}
	public String getCart_subtotal() {
		return cart_subtotal;
	}
	public void setCart_subtotal(String cart_subtotal) {
		this.cart_subtotal = cart_subtotal;
	}
	public String getCart_send_order() {
		return cart_send_order;
	}
	public void setCart_send_order(String cart_send_order) {
		this.cart_send_order = cart_send_order;
	}
	public String getCart_pay_now() {
		return cart_pay_now;
	}
	public void setCart_pay_now(String cart_pay_now) {
		this.cart_pay_now = cart_pay_now;
	}
	public String getCart_pay_later() {
		return cart_pay_later;
	}
	public void setCart_pay_later(String cart_pay_later) {
		this.cart_pay_later = cart_pay_later;
	}
	public String getCart_checkout() {
		return cart_checkout;
	}
	public void setCart_checkout(String cart_checkout) {
		this.cart_checkout = cart_checkout;
	}
	public String getEdit_item_detail_editCart() {
		return edit_item_detail_editCart;
	}
	public void setEdit_item_detail_editCart(String edit_item_detail_editCart) {
		this.edit_item_detail_editCart = edit_item_detail_editCart;
	}
	public String getDialog_reset_cart_title() {
		return dialog_reset_cart_title;
	}
	public void setDialog_reset_cart_title(String dialog_reset_cart_title) {
		this.dialog_reset_cart_title = dialog_reset_cart_title;
	}
	public String getDialog_cart_add_success_title() {
		return dialog_cart_add_success_title;
	}
	public void setDialog_cart_add_success_title(String dialog_cart_add_success_title) {
		this.dialog_cart_add_success_title = dialog_cart_add_success_title;
	}
	public String getDialog_cart_edit_success_title() {
		return dialog_cart_edit_success_title;
	}
	public void setDialog_cart_edit_success_title(String dialog_cart_edit_success_title) {
		this.dialog_cart_edit_success_title = dialog_cart_edit_success_title;
	}
	public String getDialog_delete_cart_item_title() {
		return dialog_delete_cart_item_title;
	}
	public void setDialog_delete_cart_item_title(String dialog_delete_cart_item_title) {
		this.dialog_delete_cart_item_title = dialog_delete_cart_item_title;
	}
	public String getDialog_send_order_success_title() {
		return dialog_send_order_success_title;
	}
	public void setDialog_send_order_success_title(String dialog_send_order_success_title) {
		this.dialog_send_order_success_title = dialog_send_order_success_title;
	}
	public String getDialog_send_order_failed_title() {
		return dialog_send_order_failed_title;
	}
	public void setDialog_send_order_failed_title(String dialog_send_order_failed_title) {
		this.dialog_send_order_failed_title = dialog_send_order_failed_title;
	}
	public String getDialog_button_ok() {
		return dialog_button_ok;
	}
	public void setDialog_button_ok(String dialog_button_ok) {
		this.dialog_button_ok = dialog_button_ok;
	}
	public String getDialog_button_yes() {
		return dialog_button_yes;
	}
	public void setDialog_button_yes(String dialog_button_yes) {
		this.dialog_button_yes = dialog_button_yes;
	}
	public String getDialog_button_no() {
		return dialog_button_no;
	}
	public void setDialog_button_no(String dialog_button_no) {
		this.dialog_button_no = dialog_button_no;
	}
}
