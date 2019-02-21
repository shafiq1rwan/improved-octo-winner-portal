package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.springframework.http.MediaType;
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

import my.com.byod.admin.entity.Store;
import my.com.byod.admin.service.StoreService;
import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/menu/store")
public class StoreRestController {
	
	@Value("${get-upload-path}")
	private String displayFilePath;
	
	@Autowired
	private StoreService storeService;
	
/*	@Autowired
	private DataSource dataSource;*/
	
	@Autowired 
	private ByodUtil byodUtil;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	// Store
	@GetMapping("")
	public ResponseEntity<List<Store>> findAllStore(HttpServletRequest request, HttpServletResponse response) {
		List<Store> stores = storeService.findAllStore();
		try {
			Connection connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			for(Store store: stores) {
				store.setLogoPath(displayFilePath + brandId + "/" + store.getLogoPath());
			}
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Store>>(stores, HttpStatus.OK);
	}

	@GetMapping("/storeById")
	public ResponseEntity<Store> findStoreById(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		Store existingStore = storeService.findStoreById(id);
		try {
			Connection connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			if (existingStore.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			existingStore.setLogoPath(displayFilePath + brandId + "/" + existingStore.getLogoPath());
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<Store>(existingStore, HttpStatus.OK);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createStore(@RequestBody Store store, HttpServletRequest request, HttpServletResponse response) {
		try {
			Connection connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			int rowAffected = storeService.createStore(store, brandId);
			connection.close();
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editStore(@RequestBody Store store, HttpServletRequest request, HttpServletResponse response) {
		try {
			Connection connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			Store existingStore = storeService.findStoreById(store.getId());
			if (existingStore.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			int rowAffected = storeService.editStore(store.getId(), store, brandId);
			connection.close();
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/edit/groupCategory")
	public ResponseEntity<Void> editStoreGroupCategoryId(@RequestParam("storeId") Long storeId,
			@RequestParam("groupCategoryId") Long groupCategoryId, HttpServletRequest request, HttpServletResponse response) {
		storeService.editStoreGroupCategoryId(groupCategoryId, storeId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> removeStore(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		int rowAffected = storeService.removeStore(id);
		if (rowAffected == 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*Start ECPOS API*/
	@GetMapping(value = {"/ecposByStoreId"}, produces = "application/json")
	public String getEcpos(@RequestParam(value = "store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);

			stmt = connection.prepareStatement("SELECT * FROM store WHERE id = ?");
			stmt.setLong(1, store_id);
			rs = (ResultSet) stmt.executeQuery();
			
			jsonObj = new JSONObject();
			if(rs.next()) {
				jsonObj.put("brand_id", byodUtil.getGeneralConfig(connection, "BRAND_ID"));		
				jsonObj.put("id", rs.getLong("id"));		
				jsonObj.put("backend_id", rs.getString("backend_id"));
				jsonObj.put("store_name", rs.getString("store_name"));				
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
	
	@GetMapping(value = {"/ecpos/getStaffRole"}, produces = "application/json")
	public String getStaffRole(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);

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
	
	@GetMapping(value = {"/ecpos/getAllStaff"}, produces = "application/json")
	public String getAllStaff(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
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
	
	@GetMapping(value = {"/ecpos/staffById"}, produces = "application/json")
	public String getStaffById(@RequestParam("store_id") Long store_id, @RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
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
	
	@PostMapping(value = {"/ecpos/createStaff"}, produces = "application/json")
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
			
			connection = dbConnectionUtil.retrieveConnection(request);
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
	
	@PostMapping(value = {"/ecpos/updateStaff"}, produces = "application/json")
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
			
			connection = dbConnectionUtil.retrieveConnection(request);
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
	
	@GetMapping(value = {"/ecpos/activate"}, produces = "application/json")
	public ResponseEntity<?> activateECPOS(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			if(checkDeviceInfoExist(1, store_id, request))
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("ECPOS has already been activated.");
			
			int rowAffected = createDeviceInfo(1, store_id, request);
			if(rowAffected==0)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate ECPOS activation info.");
					
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
	
	@GetMapping(value = {"/ecpos/getInfo"}, produces = "application/json")
	public ResponseEntity<?> getEcposInfo(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			if(!getEcposStatus(store_id, request))
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
				
			jsonArray = getDeviceInfoByStoreId(1, store_id, request);
			if(jsonArray.length()==0) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			else {
				// ecpos only one record
				jsonObj = jsonArray.getJSONObject(0);
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
		return new ResponseEntity<String>(jsonObj.toString(), HttpStatus.OK);
	}
	
	@GetMapping(value = {"/ecpos/terminate"}, produces = "application/json")
	public ResponseEntity<?> terminateEcpos(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			if(!getEcposStatus(store_id, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS is disabled.");
			
			jsonArray = getDeviceInfoByStoreId(1, store_id, request);
			if(jsonArray.length()==0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find ECPOS info.");
			}
			else {
				// ecpos only 1 record
				jsonObj = jsonArray.getJSONObject(0);
			}

			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==3)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("ECPOS is already inactive.");

			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 3, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to terminate ECPOS.");
		
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(value = {"/ecpos/reactivate"}, produces = "application/json")
	public ResponseEntity<?> reactivateEcpos(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			if(!getEcposStatus(store_id, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS is disabled.");
			
			jsonArray = getDeviceInfoByStoreId(1, store_id, request);
			if(jsonArray.length()==0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find ECPOS info.");
			}
			else {
				// ecpos only 1 record
				jsonObj = jsonArray.getJSONObject(0);
			}
	
			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==1)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("ECPOS is already reactivated.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 1, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to reactivate ECPOS.");		
		
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*End ECPOS API*/
	
	/*Start BYOD API*/
	
	@GetMapping(value = {"/byodByStoreId"}, produces = "application/json")
	public String getAllByod(@RequestParam(value = "store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);

			stmt = connection.prepareStatement("SELECT * FROM store WHERE id = ?");
			stmt.setLong(1, store_id);
			rs = (ResultSet) stmt.executeQuery();
			 
			jsonObj = new JSONObject();
			if(rs.next()) {
				jsonObj.put("brand_id", byodUtil.getGeneralConfig(connection, "BRAND_ID"));
				jsonObj.put("id", rs.getLong("id"));		
				jsonObj.put("backend_id", rs.getString("backend_id"));
				jsonObj.put("store_name", rs.getString("store_name"));				
				jsonObj.put("byod", getDeviceInfoByStoreId(2, store_id, request));
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
	
	@GetMapping(value = {"/byod/activate"}, produces = "application/json")
	public ResponseEntity<?> activateByod(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			int rowAffected = createDeviceInfo(2, store_id, request);
			if(rowAffected==0)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate BYOD activation info.");
					
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
	
	@GetMapping(value = {"/byod/terminate"}, produces = "application/json")
	public ResponseEntity<?> terminateByod(@RequestParam("activation_id") String activation_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj = getDeviceInfoByActivationId(activation_id, request);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find BYOD info.");
			}

			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==3)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("BYOD is already inactive.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 3, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to terminate BYOD.");
		
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(value = {"/byod/reactivate"}, produces = "application/json")
	public ResponseEntity<?> reactivateByod(@RequestParam("activation_id") String activation_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj = getDeviceInfoByActivationId(activation_id, request);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find BYOD info.");
			}
	
			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==1)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("BYOD is already reactivated.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 1, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to reactivate BYOD.");			
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*End BYOD API*/
	
	/*Start KIOSK API*/
	
	@GetMapping(value = {"/kioskByStoreId"}, produces = "application/json")
	public String getAllKiosk(@RequestParam(value = "store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);

			 stmt = connection.prepareStatement("SELECT * FROM store WHERE id = ?");
			 stmt.setLong(1, store_id);
			 rs = (ResultSet) stmt.executeQuery();
			
			jsonObj = new JSONObject();
			if(rs.next()) {
				jsonObj.put("brand_id", byodUtil.getGeneralConfig(connection, "BRAND_ID"));		
				jsonObj.put("id", rs.getLong("id"));		
				jsonObj.put("backend_id", rs.getString("backend_id"));
				jsonObj.put("store_name", rs.getString("store_name"));				
				jsonObj.put("kiosk", getDeviceInfoByStoreId(3, store_id, request));
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
	
	@GetMapping(value = {"/kiosk/activate"}, produces = "application/json")
	public ResponseEntity<?> activateKiosk(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			int rowAffected = createDeviceInfo(3, store_id, request);
			if(rowAffected==0)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate KIOSK activation info.");
					
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
	
	@GetMapping(value = {"/kiosk/terminate"}, produces = "application/json")
	public ResponseEntity<?> terminateKiosk(@RequestParam("activation_id") String activation_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj = getDeviceInfoByActivationId(activation_id, request);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find KIOSK info.");
			}

			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==3)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("KIOSK is already inactive.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 3, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to terminate KIOSK.");
		
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(value = {"/kiosk/reactivate"}, produces = "application/json")
	public ResponseEntity<?> reactivateKiosk(@RequestParam("activation_id") String activation_id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj = getDeviceInfoByActivationId(activation_id,request);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find KIOSK info.");
			}
	
			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==1)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("KIOSK is already reactivated.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(id, 1, request))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to reactivate KIOSK.");			
			
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
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/*End KIOSK API*/
	
	public boolean getEcposStatus(long store_id, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String prefix = "";
		boolean flag = false;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT ecpos FROM store WHERE id = ? ");
			
			stmt.setLong(count++, store_id);
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				flag = rs.getBoolean("ecpos");
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
		return flag;
	}
	
	private String getDevicePrefix(long deviceTypeId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String prefix = "";
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT prefix FROM device_type_lookup WHERE id = ? ");
			
			stmt.setLong(count++, deviceTypeId);
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				prefix = rs.getString("prefix");
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
		return prefix;
	}
	
	private boolean checkDeviceInfoExist(long deviceTypeId, long referenceId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		boolean exist = false;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM device_info WHERE device_type_lookup_id = ? AND ref_id = ? ");
			
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				exist = true;
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
		return exist;
	}
	
	private int createDeviceInfo(long deviceTypeId, long referenceId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		int result = 0;
		
		try {
			String prefix = getDevicePrefix(deviceTypeId, request);
			
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("INSERT INTO device_info(activation_id, activation_key, status_lookup_id, device_type_lookup_id, ref_id) "
					+ "VALUES (?,?,?,?,?); SELECT SCOPE_IDENTITY();");
			
			stmt.setString(count++, byodUtil.createUniqueActivationId(prefix));
			stmt.setString(count++, byodUtil.createRandomDigit(16));
			stmt.setInt(count++, 1);
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);

			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
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
		return result;
	}
	
	private JSONArray getDeviceInfoByStoreId(long deviceTypeId, long referenceId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonObj = null;
		JSONArray jsonArray = new JSONArray();
		int count = 1;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT a.*, b.name AS status FROM device_info a "
					+ "INNER JOIN status_lookup b ON a.status_lookup_id = b.id WHERE a.device_type_lookup_id = ? AND a.ref_id = ? ORDER BY a.created_date DESC");
			
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);

			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("activation_id", rs.getString("activation_id"));
				jsonObj.put("activation_key", rs.getString("activation_key"));
				jsonObj.put("mac_address", rs.getString("mac_address"));
				jsonObj.put("created_date", rs.getString("created_date"));
				jsonObj.put("status_lookup_id", rs.getLong("status_lookup_id"));
				jsonObj.put("status", rs.getString("status"));
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
		return jsonArray;
	}
	
	private JSONObject getDeviceInfoByActivationId(String activationId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonObj = null;
		//JSONArray jsonArray = new JSONArray();
		int count = 1;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT a.*, b.name AS status FROM device_info a "
					+ "INNER JOIN status_lookup b ON a.status_lookup_id = b.id WHERE a.activation_id = ?");
			
			stmt.setString(count++, activationId);

			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("activation_id", rs.getString("activation_id"));
				jsonObj.put("activation_key", rs.getString("activation_key"));
				jsonObj.put("mac_address", rs.getString("mac_address"));
				jsonObj.put("created_date", rs.getString("created_date"));
				jsonObj.put("status_lookup_id", rs.getLong("status_lookup_id"));
				jsonObj.put("status", rs.getString("status"));
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
		return jsonObj;
	}
	
	private boolean updateDeviceStatus(long id, long statusTypeId, HttpServletRequest request) {	
		Connection connection = null;
		PreparedStatement stmt = null;
		int count = 1;
		boolean flag = false;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("UPDATE device_info SET status_lookup_id = ?, mac_address = NULL, last_update_date = GETDATE() WHERE id = ? ;");
			
			stmt.setLong(count++, statusTypeId);
			stmt.setLong(count++, id);

			int rowAffected = stmt.executeUpdate();
			
			if(rowAffected==1) {
				flag = true;
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
		return flag;
	}
	
}
