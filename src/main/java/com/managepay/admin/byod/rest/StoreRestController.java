package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.service.StoreService;

@RestController
@RequestMapping("/menu/store")
public class StoreRestController {
	
	@Value("${upload-path}")
	private String filePath;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;	
	
	// Store
	@GetMapping("")
	public ResponseEntity<List<Store>> findAllStore() {
		List<Store> stores = storeService.findAllStore();
		return new ResponseEntity<List<Store>>(stores, HttpStatus.OK);
	}

	@GetMapping("/storebyid")
	public ResponseEntity<Store> findStoreById(@RequestParam("id") Long id) {
		Store existingStore = storeService.findStoreById(id);
		if (existingStore.getId() == 0)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		existingStore.setLogoPath(filePath + existingStore.getLogoPath());
		return new ResponseEntity<Store>(existingStore, HttpStatus.OK);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createStore(@RequestBody Store store) {
		try {
			int rowAffected = storeService.createStore(store);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DuplicateKeyException ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editStore(@RequestBody Store store) {
		try {
			Store existingStore = storeService.findStoreById(store.getId());
			if (existingStore.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			int rowAffected = storeService.editStore(store.getId(), store);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (DuplicateKeyException ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/edit/groupcategory")
	public ResponseEntity<Void> editStoreGroupCategoryId(@RequestParam("storeId") Long storeId,
			@RequestParam("groupCategoryId") Long groupCategoryId) {
		storeService.editStoreGroupCategoryId(groupCategoryId, storeId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> removeStore(@RequestParam("id") Long id) {
		int rowAffected = storeService.removeStore(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*Start ECPOS API*/
	
	@GetMapping(value = {"/{id}/ecpos"}, produces = "application/json")
	public String getAllCategory(@PathVariable(value = "id") Long id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();

			 stmt = connection.prepareStatement("SELECT * FROM store WHERE id = ?");
			 stmt.setLong(1, id);
			 rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("group_category_id", rs.getLong("group_category_id"));			
				jsonObj.put("backend_id", rs.getString("backend_id"));
				jsonObj.put("store_name", rs.getString("store_name"));
				//jsonArray.put(jsonObj);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonObj.toString();
	}
	
	@GetMapping(value = {"/ecpos/getstaffrole"}, produces = "application/json")
	public String getStaffRole(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();

			 stmt = connection.prepareStatement("SELECT * FROM role_lookup");
			 rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("role_name", rs.getString("role_name"));			
				jsonArray.put(jsonObj);
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonArray.toString();
	}
	
	@GetMapping(value = {"/ecpos/getallstaff"}, produces = "application/json")
	public String getAllStaff(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			 stmt = connection.prepareStatement("SELECT * FROM staff WHERE store_id = ? ");
			 stmt.setLong(1, store_id);
			 rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("staff_name"));
				jsonObj.put("username", rs.getString("staff_username"));	
				jsonObj.put("password", rs.getString("staff_password"));	
				jsonObj.put("role_id", rs.getLong("staff_role"));	
				jsonObj.put("mobilePhone", rs.getString("staff_contact_hp_number"));	
				jsonObj.put("email", rs.getString("staff_contact_email"));
				jsonObj.put("isActive", rs.getString("is_active"));
				jsonObj.put("createdDate", rs.getString("created_date"));
				jsonArray.put(jsonObj);
			}
			
			jsonObjResult = new JSONObject();
			jsonObjResult.put("data", jsonArray);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonObjResult.toString();
	}
	
	@GetMapping(value = {"/ecpos/staffbyid"}, produces = "application/json")
	public String getStaffById(@RequestParam("store_id") Long store_id, @RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			 stmt = connection.prepareStatement("SELECT * FROM staff a WHERE store_id = ? AND id = ? ");
			 stmt.setLong(1, store_id);
			 stmt.setLong(2, id);
			 rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("staff_name"));
				jsonObj.put("username", rs.getString("staff_username"));	
				jsonObj.put("password", rs.getString("staff_password"));	
				jsonObj.put("role_id", rs.getLong("staff_role"));	
				jsonObj.put("mobilePhone", rs.getString("staff_contact_hp_number"));	
				jsonObj.put("email", rs.getString("staff_contact_email"));
				jsonObj.put("isActive", rs.getString("is_active"));
				jsonObj.put("createdDate", rs.getString("created_date"));
			}			
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jsonObj.toString();
	}
	
	@PostMapping(value = {"/ecpos/createstaff"}, produces = "application/json")
	public ResponseEntity<JSONObject> createStaff(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		
		try {
			jsonObj  =  new JSONObject(formfield);
			
			if(!jsonObj.has("store_id")) {
				jsonObj.put("error", "Unable to find store detail");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.NOT_FOUND);
			}
			if(jsonObj.getString("name")==null || jsonObj.getString("name").trim().equals("")) {
				jsonObj.put("error", "Staff name cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("email")==null || jsonObj.getString("email").trim().equals("")) {
				jsonObj.put("error", "Staff email cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("mobilePhone")==null || jsonObj.getString("mobilePhone").trim().equals("")) {
				jsonObj.put("error", "Phone number cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("username")==null || jsonObj.getString("username").trim().equals("")) {
				jsonObj.put("error", "Username cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("password")==null || jsonObj.getString("password").trim().equals("")) {
				jsonObj.put("error", "Password cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(!jsonObj.has("role_id") || jsonObj.getLong("role_id")==0) {
				jsonObj.put("error", "Role cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			
			System.out.println(jsonObj.toString());
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("INSERT INTO staff (store_id, staff_name, staff_username, staff_password, staff_role, staff_contact_hp_number,"
			 		+ "staff_contact_email, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY();");
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setString(count++, jsonObj.getString("name"));
			stmt.setString(count++, jsonObj.getString("username"));
			stmt.setString(count++, jsonObj.getString("password"));
			stmt.setLong(count++, jsonObj.getLong("role_id"));
			stmt.setString(count++, jsonObj.getString("mobilePhone"));
			stmt.setString(count++, jsonObj.getString("email"));
			stmt.setLong(count++, 1);
			
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj.put("staff_id", rs.getInt(1));
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	@PostMapping(value = {"/ecpos/updatestaff"}, produces = "application/json")
	public ResponseEntity<JSONObject> updateStaff(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		
		try {
			jsonObj  =  new JSONObject(formfield);
			
			if(!jsonObj.has("store_id")) {
				jsonObj.put("error", "Unable to find store detail");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.NOT_FOUND);
			}
			if(!jsonObj.has("staff_id")) {
				jsonObj.put("error", "Unable to find staff detail");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.NOT_FOUND);
			}
			if(jsonObj.getString("name")==null || jsonObj.getString("name").trim().equals("")) {
				jsonObj.put("error", "Staff name cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("email")==null || jsonObj.getString("email").trim().equals("")) {
				jsonObj.put("error", "Staff email cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("mobilePhone")==null || jsonObj.getString("mobilePhone").trim().equals("")) {
				jsonObj.put("error", "Phone number cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("username")==null || jsonObj.getString("username").trim().equals("")) {
				jsonObj.put("error", "Username cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(jsonObj.getString("password")==null || jsonObj.getString("password").trim().equals("")) {
				jsonObj.put("error", "Password cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			if(!jsonObj.has("role_id") || jsonObj.getLong("role_id")==0) {
				jsonObj.put("error", "Role cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}		
			
			System.out.println(jsonObj.toString());
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("UPDATE staff SET staff_name = ?, staff_username = ?, staff_password = ?, staff_role = ?, staff_contact_hp_number = ?,"
			 		+ "staff_contact_email = ?, is_active = ?, last_update_date = GETDATE() WHERE store_id = ? AND id = ?; SELECT SCOPE_IDENTITY();");
			
			stmt.setString(count++, jsonObj.getString("name"));
			stmt.setString(count++, jsonObj.getString("username"));
			stmt.setString(count++, jsonObj.getString("password"));
			stmt.setLong(count++, jsonObj.getLong("role_id"));
			stmt.setString(count++, jsonObj.getString("mobilePhone"));
			stmt.setString(count++, jsonObj.getString("email"));
			stmt.setLong(count++, jsonObj.getLong("isActive"));
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setLong(count++, jsonObj.getLong("staff_id"));
			
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj.put("staff_id", rs.getInt(1));
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.OK);
	}
	
	/*End ECPOS API*/
	
}
