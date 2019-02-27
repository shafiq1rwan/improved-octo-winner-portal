package my.com.byod.admin.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("/user")
public class UserLoginRestController {
	
	/*@Autowired
	UserService adminService;*/
	
	@Autowired
	StoreRestController storeRestController;
	
	// SIGNIN
	@RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
	public ModelAndView signIn() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/login");
		return model;
	}
	
	// SEND HOME
	@RequestMapping(value = {""}, method = RequestMethod.GET)
	public ModelAndView home(/*Authentication authentication*/) {		
		/*User user = (User) authentication.getPrincipal();
		com.managepay.byod.model.User dbUser = adminService.getAdminDetail(user.getUsername());*/
		
		ModelAndView model = new ModelAndView();
		/*model.addObject("role", dbUser.getRoles());*/
		model.setViewName("/user/home");
		return model;
	}
	
	// USER - 403 UN-Authorize Access
	@RequestMapping(value = { "/403" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView user_403Page() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/403");
		return model;
	}
	
	// USER - CHECK SESSION
	@RequestMapping(value = { "/checksession" }, method = RequestMethod.GET)
	public ResponseEntity<String> check_session(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String status = null;
		
		if (session != null) {
			status = "exist";
		}
		return ResponseEntity.status(HttpStatus.OK).body(status);
	}
	
	@RequestMapping(value = "/signin/error/{condition}", method = RequestMethod.GET)
	public ModelAndView user_signin_fail(@PathVariable String condition) {
		ModelAndView model = new ModelAndView();
		if (condition.equals("lock")) {
			model.addObject("exceptionMsg", "Hit attempt limit. Please proceed with \"Forgot Password\"");
		} 
		else if (condition.equals("timeout")) {
			model.addObject("exceptionMsg", "Session timeout");
		}
		else if (condition.equals("not-exist")) {
			model.addObject("exceptionMsg", "User not exist");
		}
		else {
			model.addObject("exceptionMsg", "Invalid access");
		}
		
		model.setViewName("/login");
	
	return model;
	}
		
	// Store
	@RequestMapping(value = { "/views/store" }, method = RequestMethod.GET)
	public ModelAndView viewBrand() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/store");
		return model;
	}
	
	// Store - ECPOS
	@RequestMapping(value = { "/views/store/{id}/ecpos" }, method = RequestMethod.GET)
	public ModelAndView viewECPos(@PathVariable(value = "id") long id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		
		if(storeRestController.getEcposStatus(id, request)) {
			model.setViewName("/user/views/ecpos");
		}
		else {
			model.setViewName("/user/views/unauthorized");
		}
		return model;
	}
	
	// Store - BYOD
	@RequestMapping(value = { "/views/store/{id}/byod" }, method = RequestMethod.GET)
	public ModelAndView viewBYOD(@PathVariable(value = "id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/byod");
		return model;
	}		
		
	// Store - KIOSK
	@RequestMapping(value = { "/views/store/{id}/kiosk" }, method = RequestMethod.GET)
	public ModelAndView viewKIOSK(@PathVariable(value = "id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/kiosk");
		return model;
	}		
	
	// Group Category
	@RequestMapping(value = { "/views/groupCategory" }, method = RequestMethod.GET)
	public ModelAndView viewGroupCategory() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/group_category");
		return model;
	}
	
	// Profile
	@RequestMapping(value = { "/views/profile" }, method = RequestMethod.GET)
	public ModelAndView viewProfile() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/profile");
		return model;
	}
	
	// Master Menu
	@RequestMapping(value = { "/views/menu" }, method = RequestMethod.GET)
	public ModelAndView viewMenu() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/menu");
		return model;
	}
	
	// Add Menu
	@RequestMapping(value = { "/views/addMenu" }, method = RequestMethod.GET)
	public ModelAndView addMenu() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/add_menu");
		return model;
	}
	
	// Dashboard
	@RequestMapping(value = { "/views/dashboard" }, method = RequestMethod.GET)
	public ModelAndView viewMenuDashboard() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/dashboard");
		return model;
	}
	
	// Category
	@RequestMapping(value = {"/views/groupCategory/{id}/category"}, method = RequestMethod.GET)
	public ModelAndView viewCategory(@PathVariable(value = "id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/category");
		return model;
	}

	// Item Group
	@RequestMapping(value = {"/views/itemGroup"}, method = RequestMethod.GET)
	public ModelAndView viewItemGroup() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/item_group");
		return model;
	}

	// Modifier Group
	@RequestMapping(value = {"/views/modifierGroup"}, method = RequestMethod.GET)
	public ModelAndView viewModifierGroup() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/modifier_group");
		return model;
	}
	
	@RequestMapping(value = {"/views/menuItem"}, method = RequestMethod.GET)
	public ModelAndView viewMenuItem() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/menu_item");
		return model;
	}
	
	//Combo
	@RequestMapping(value = {"/views/combo/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewCombo(@PathVariable("id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/combo");
		return model;
	}
	
	//Assign Modifier
	@RequestMapping(value = {"/views/assignModifier/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewAssignModifier(@PathVariable("id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/assign_modifier");
		return model;
	}
	
	// Setting
	@RequestMapping(value = { "/views/setting" }, method = RequestMethod.GET)
	public ModelAndView viewSetting() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/setting");
		return model;
	}
}
