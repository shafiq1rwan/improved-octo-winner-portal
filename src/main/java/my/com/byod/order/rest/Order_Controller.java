package my.com.byod.order.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import my.com.byod.order.configuration.OrderConfiguration;
import my.com.byod.order.util.AESEncryption;

@RestController
@RequestMapping("/order")
public class Order_Controller {
	
	@Autowired
	private OrderConfiguration orderConfiguration;
	
	@RequestMapping(value = { "" }, method = { RequestMethod.GET })
	public ModelAndView OrderDefaultPage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/order/pleaseScanQR");
		return model;
	}
	
	@RequestMapping(value = { "{token}" }, method = { RequestMethod.GET })
	public ModelAndView OrderDefaultPage(@PathVariable("token") String token) {
		String brandId = null;
		
		String decryptedTokenString = AESEncryption.decrypt(token);
		
		ModelAndView model = new ModelAndView();
		if (decryptedTokenString != null) {
			String[] tokenSplitArry = decryptedTokenString.split("\\|;");
			brandId = tokenSplitArry[0];
			
			model.addObject("applicationData", orderConfiguration.applicationData());
			model.setViewName("/order/home");
		} else {
			model.setViewName("/order/invalidQR");
		}
		
		return model;
	}
	
	@RequestMapping(value = { "/views/error" }, method = { RequestMethod.GET })
	public ModelAndView errorPage() {
		ModelAndView model = new ModelAndView();
		model.addObject("applicationData", orderConfiguration.applicationData());
		model.setViewName("order/views/error");
		return model;
	}
	
	@RequestMapping(value = { "/views/singleOrderPage" }, method = { RequestMethod.GET })
	public ModelAndView categoryPage() {
		ModelAndView model = new ModelAndView();
		model.addObject("applicationData", orderConfiguration.applicationData());
		model.setViewName("order/views/singleOrderPage");
		return model;
	}
}