package managepay.com.admin.byod.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Discount {

	public String id;
	public String backendId;
	public String name;
	public String description;
	public String code;
	public BigDecimal amount;
	public int percentage;
	public Date validUntil;
	public boolean isRedeemable;
	
	public Discount() {
		
	}
	
}
