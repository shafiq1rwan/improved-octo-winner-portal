package my.com.byod.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping(value = "/admin-panel")
	public ModelAndView adminPanel() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/home");
		return model;
	}

	@GetMapping(value = "/views/brand-management")
	public ModelAndView brandView() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/views/brand-management");
		return model;
	}

	@GetMapping(value = "/views/user-management")
	public ModelAndView userManagementView() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/views/user-management");
		return model;
	}

}
