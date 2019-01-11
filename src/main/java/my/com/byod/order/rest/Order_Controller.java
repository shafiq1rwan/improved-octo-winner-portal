package my.com.byod.order.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import my.com.byod.order.configuration.LanguageConfiguration;
import my.com.byod.order.configuration.OrderConfiguration;

@RestController
@RequestMapping("/order")
public class Order_Controller {
	
	@Autowired
	private OrderConfiguration orderConfiguration;
	
	@RequestMapping(value = { "" }, method = { RequestMethod.GET })
	public ModelAndView OrderDefaultPage() {
		ModelAndView model = new ModelAndView();
		model.addObject("applicationData", orderConfiguration.applicationData());
		model.setViewName("/order/home");
		return model;
	}
	
	@RequestMapping(value = { "/views/pleaseScanQR" }, method = { RequestMethod.GET })
	public ModelAndView pleaseScanQRPage() {
		ModelAndView model = new ModelAndView();
		model.addObject("applicationData", orderConfiguration.applicationData());
		model.setViewName("order/views/pleaseScanQR");
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