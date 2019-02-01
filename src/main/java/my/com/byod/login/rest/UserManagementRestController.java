package my.com.byod.login.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;
import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.service.ApplicationUserService;

@RestController
@RequestMapping("/users")
public class UserManagementRestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ApplicationUserService applicationUserService;
	
	@Autowired
	private ByodUtil byodUtil;

	@PostMapping(value = "/signup")
	public ResponseEntity<?> signupUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			ApplicationUser user = new ApplicationUser();
			user.setName(jsonData.getString("name"));
			user.setEmail(jsonData.getString("email"));
			user.setMobileNumber(jsonData.getString("mobileNumber"));
			user.setAddress(jsonData.getString("address"));
			user.setUsername(jsonData.getString("username"));
			user.setPassword(jsonData.getString("password"));
			user.setEnabled(jsonData.getBoolean("enabled"));

			if (applicationUserService.findUserByEmail(user.getEmail()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("01", user.getEmail() + " already being taken"));
			else if (applicationUserService.findUserByUsername(user.getUsername()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("02", user.getUsername() + " already being taken"));
			else if (applicationUserService.findUserByMobileNumber(user.getMobileNumber()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("03", user.getMobileNumber() + " already being taken"));

			//user.setPassword(byodUtil.createRandomString(8));

			Long userId = applicationUserService.createUser(user, jsonData.getString("role"));
			if (userId == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create new user");
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Internal Server Error");
		}
		return ResponseEntity.ok(null);
	}
	
/*	@PostMapping(value = "/edit/")
	public ResponseEntity<?> editUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		try {
			JSONObject jsonData = new JSONObject(data);
			ApplicationUser user = new ApplicationUser();
			user.setId(jsonData.getLong("id"));
			user.setName(jsonData.getString("name"));
			user.setEmail(jsonData.getString("email"));
			user.setMobileNumber(jsonData.getString("mobileNumber"));
			user.setAddress(jsonData.getString("address"));
			user.setUsername(jsonData.getString("username"));
			user.setPassword(jsonData.getString("password"));
			user.setEnabled(jsonData.getBoolean("enabled"));

			if (applicationUserService.findUserByEmail(user.getEmail()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("01", user.getEmail() + " already being taken"));
			else if (applicationUserService.findUserByUsername(user.getUsername()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("02", user.getUsername() + " already being taken"));
			else if (applicationUserService.findUserByMobileNumber(user.getMobileNumber()) != null)
				throw new IllegalArgumentException(
						constructJsonResponse("03", user.getMobileNumber() + " already being taken"));

			
			if (userId == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot edit existing user");
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Internal Server Error");
		}
		return ResponseEntity.ok(null);
	}*/
	
	
	
	

	private String constructJsonResponse(String errorCode, String errorMessage) {
		JSONObject responseData = new JSONObject();
		try {
			responseData.put("responseCode", errorCode);
			responseData.put("responseMessage", errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseData.toString();
	}

	@GetMapping("/")
	public ResponseEntity<?> findUsers(HttpServletRequest request, HttpServletResponse response){		
		JSONArray jsonUserArray = new JSONArray();
		
/*		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName(); // get logged in username
		System.out.println(auth.getAuthorities().iterator().next());*/
	
		try {
			String sql = "SELECT mu.id, mu.name, mu.email, mu.mobileNumber, mu.address, mu.username, mu.enabled, ma.authority "
					+ "FROM mpay_users mu INNER JOIN mpay_authorities ma ON mu.id = ma.mpay_user "
					+ "WHERE ma.authority != ?";
			List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, new Object[] {"ROLE_SUPER_ADMIN"});
			
			if(!users.isEmpty()) {
				for(Map<String, Object> user:users) {
					JSONObject jsonObj = new JSONObject(user);
					jsonUserArray.put(jsonObj);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		}
		return ResponseEntity.ok(jsonUserArray.toString());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> findUserById(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id){		
		JSONObject jsonResult = null;
	
		try {
			String sql = "SELECT mu.id, mu.name, mu.email, mu.mobileNumber, mu.address, mu.username, mu.enabled, ma.authority "
					+ "FROM mpay_users mu INNER JOIN mpay_authorities ma ON mu.id = ma.mpay_user "
					+ "WHERE mu.id = ?";
			Map<String, Object> user = jdbcTemplate.queryForMap(sql, new Object[] {id});
			
			if(!user.isEmpty())
				jsonResult = new JSONObject(user);
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		}
		return ResponseEntity.ok(jsonResult.toString());
	}
	


	

}
