package my.com.byod.login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ByodController {

	@GetMapping(value = {"","/","/byod-panel"})
	public ModelAndView adminPanel(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute("brand_id");
		request.getSession().removeAttribute("access_rights");
		
		//System.out.println("My brand " + (Long)request.getSession().getAttribute("brand_id"));
		//System.out.println("My accessRights " + (JSONObject)request.getSession().getAttribute("access_rights"));
		
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
	
	@GetMapping(value = "/views/password_management")
	public ModelAndView passwordManagementView() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/views/password_management");
		return model;
	}

}
