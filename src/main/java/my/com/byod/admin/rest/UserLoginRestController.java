package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import my.com.byod.admin.util.DbConnectionUtil;


@RestController
@RequestMapping("/user")
public class UserLoginRestController {
	
	/*@Autowired
	UserService adminService;*/
	
	@Autowired
	StoreRestController storeRestController;
	
	@Autowired
	DbConnectionUtil dbConnectionUtil;
	
	// SIGNIN
	@RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
	public ModelAndView signIn() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/login");
		return model;
	}
	
	// SEND HOME
	@RequestMapping(value = {""}, method = RequestMethod.GET)
	public ModelAndView home(/*Authentication authentication*/HttpServletRequest request) {		
		/*User user = (User) authentication.getPrincipal();
		com.managepay.byod.model.User dbUser = adminService.getAdminDetail(user.getUsername());*/
		
		HttpSession session = request.getSession();
		ModelAndView model = new ModelAndView();
		/*model.addObject("role", dbUser.getRoles());*/

		if(session != null) {
			JSONObject accessRight = (JSONObject) session.getAttribute("access_rights");
			if(accessRight!=null)
				model.setViewName("/user/home");
			else 
				model.setViewName("/admin/home");
		} else {
			model.setViewName("/admin/home");
		}
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
	
	//USER - CHECK ACCESS RIGHTS
	@RequestMapping(value = {"/checkaccessrights/{access}"}, method = RequestMethod.GET)
	public ResponseEntity<String> check_access_rights(HttpServletRequest request, @PathVariable("access") String access) throws JSONException{
		HttpSession session = request.getSession();
		String result = null;
		
		if(session != null) {
			JSONObject accessRight = (JSONObject) session.getAttribute("access_rights");
/*			System.out.println("AccessRights :" + accessRight.toString());
			System.out.println("Access :" + access);
			System.out.println("Access Result:" + accessRight.getJSONObject("accessRights").getBoolean(access));*/

			if(accessRight!=null) {
				if(accessRight.getJSONObject("accessRights").getBoolean(access)) {
					result = "authorized";
				}
			}
		} else {
			result = "session_expired";
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@RequestMapping(value = { "/views/unauthorized" }, method = RequestMethod.GET)
	public ModelAndView viewUnauthorized() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/unauthorized");
		return model;
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
		Connection connection = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			if(storeRestController.getEcposStatus(connection, id)) {
				model.setViewName("/user/views/ecpos");
			}
			else {
				model.setViewName("/user/views/unauthorized");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	// Store - Transaction
	@RequestMapping(value = { "/views/store/{id}/transaction" }, method = RequestMethod.GET)
	public ModelAndView viewTransaction(@PathVariable(value = "id") long id) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/transaction");
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
	
	//Report
	@RequestMapping(value = {"/views/report"}, method = RequestMethod.GET)
	public ModelAndView viewReport() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/user/views/report");
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
