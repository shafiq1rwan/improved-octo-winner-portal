package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.entity.Store;
import my.com.byod.admin.service.StoreService;
import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;
import my.com.byod.admin.util.UserEmailUtil;
import my.com.byod.order.util.AESEncryption;

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
	
	@Autowired
	private UserEmailUtil userEmailUtil;
	
	// Store
	@GetMapping("")
	public ResponseEntity<List<Store>> findAllStore(HttpServletRequest request, HttpServletResponse response) {
		List<Store> stores = storeService.findAllStore();
		try {
			Connection connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			connection.close();
			for(Store store: stores) {
				store.setLogoPath(displayFilePath + brandId + "/" + store.getLogoPath());
			}
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
			connection.close();
			if (existingStore.getId() == 0)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			existingStore.setLogoPath(displayFilePath + brandId + "/" + existingStore.getLogoPath());
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
			connection.close();
			int rowAffected = storeService.createStore(store, brandId);
			if (rowAffected == 0)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editStore(@RequestBody Store store, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		try {
			System.out.println("my email:" + store.getEmail());
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			Store existingStore = storeService.findStoreById(store.getId());
			if (existingStore.getId() == 0) {
				connection.close();
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			int rowAffected = storeService.editStore(store.getId(), store, brandId, existingStore.getLogoPath());
			if (rowAffected == 0) {
				connection.close();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			else {
				// terminate ecpos for ecpos status 0 at store	
				JSONArray jsonArray = getDeviceInfoByStoreId(connection, 1, store.getId());
				if(jsonArray.length()!=0) {
					// ecpos only one record
					JSONObject jsonObj = jsonArray.getJSONObject(0);
					if(!getEcposStatus(connection, store.getId())) {
						updateDeviceStatus(connection, jsonObj.getLong("id") ,3);
					}
				}
			}
			connection.close();
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping(value = "/getAllStoreLookup", produces = "application/json")
	public ResponseEntity<?> getAllStoreLookup(HttpServletRequest request, HttpServletResponse response){
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			
			jsonResult.put("storeType", getStoreType(connection));
			jsonResult.put("paymentDelayType", getPaymentDelayType(connection));
			jsonResult.put("storeTaxType", getStoreTaxType(connection));
			
			return ResponseEntity.ok(jsonResult.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
				String decPassword = AESEncryption.decrypt(rs.getString("staff_password"));
				jsonObj.put("password", decPassword);	
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
				String decPassword = AESEncryption.decrypt(rs.getString("staff_password"));
				jsonObj.put("password", decPassword);	
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
			String encPassword = AESEncryption.encrypt(jsonObj.getString("password"));
			
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("INSERT INTO staff (store_id, staff_name, staff_username, staff_password, staff_role, staff_contact_hp_number,"
			 		+ "staff_contact_email, is_active, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE()); SELECT SCOPE_IDENTITY();");
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setString(count++, jsonObj.getString("name"));
			stmt.setString(count++, jsonObj.getString("username"));
			stmt.setString(count++, encPassword);
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
			String encPassword = AESEncryption.encrypt(jsonObj.getString("password"));
			
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("UPDATE staff SET staff_name = ?, staff_username = ?, staff_password = ?, staff_role = ?, staff_contact_hp_number = ?,"
			 		+ "staff_contact_email = ?, is_active = ?, last_update_date = GETDATE() WHERE store_id = ? AND id = ?; SELECT SCOPE_IDENTITY();");
			
			stmt.setString(count++, jsonObj.getString("name"));
			stmt.setString(count++, jsonObj.getString("username"));
			stmt.setString(count++, encPassword);
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
		JSONObject jsonObj = null;
		Connection connection = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			if(checkDeviceInfoExist(connection, 1, store_id))
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("ECPOS has already been activated.");
			
			String activationId = createDeviceInfo(connection, 1, store_id);
			if(activationId==null || activationId.equals(""))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate ECPOS activation info.");
			/*else {
				Store store = storeService.findStoreById(store_id);
				// send email
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String email = store.getEmail();
				JSONObject activationInfo = getDeviceInfoByActivationId(connection, activationId);
				if(!userEmailUtil.sendActivationInfo(store.getContactPerson(), activationInfo, brandId, email))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Activation success but failed to send activation email.");
			}*/
					
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
			connection = dbConnectionUtil.retrieveConnection(request);
			if(!getEcposStatus(connection, store_id))
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
				
			jsonArray = getDeviceInfoByStoreId(connection, 1, store_id);
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
			connection = dbConnectionUtil.retrieveConnection(request);
			if(!getEcposStatus(connection, store_id))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS is disabled.");
			
			jsonArray = getDeviceInfoByStoreId(connection, 1, store_id);
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
			if(!updateDeviceStatus(connection, id, 3))
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
			connection = dbConnectionUtil.retrieveConnection(request);
			if(!getEcposStatus(connection, store_id))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS is disabled.");
			
			jsonArray = getDeviceInfoByStoreId(connection, 1, store_id);
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
			if(!updateDeviceStatus(connection, id, 1))
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
				jsonObj.put("byod", getDeviceInfoByStoreId(connection, 2, store_id));
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
			connection = dbConnectionUtil.retrieveConnection(request);
			String activationId = createDeviceInfo(connection, 2, store_id);
			if(activationId==null || activationId.equals(""))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate BYOD activation info.");
			/*else {
				Store store = storeService.findStoreById(store_id);
				// send email
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String email = store.getEmail();
				JSONObject activationInfo = getDeviceInfoByActivationId(connection, activationId);
				if(!userEmailUtil.sendActivationInfo(store.getContactPerson(), activationInfo, brandId, email))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Activation success but failed to send activation email.");
			}*/	
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
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonObj = getDeviceInfoByActivationId(connection, activation_id);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find BYOD info.");
			}

			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==3)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("BYOD is already inactive.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(connection, id, 3))
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
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonObj = getDeviceInfoByActivationId(connection, activation_id);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find BYOD info.");
			}
	
			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==1)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("BYOD is already reactivated.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(connection, id, 1))
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
				jsonObj.put("kiosk", getDeviceInfoByStoreId(connection, 3, store_id));
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
			connection = dbConnectionUtil.retrieveConnection(request);
			String activationId = createDeviceInfo(connection, 3, store_id);
			if(activationId==null || activationId.equals(""))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to generate KIOSK activation info.");
			/*else {
				Store store = storeService.findStoreById(store_id);
				// send email
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String email = store.getEmail();
				JSONObject activationInfo = getDeviceInfoByActivationId(connection, activationId);
				if(!userEmailUtil.sendActivationInfo(store.getContactPerson(), activationInfo, brandId, email))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Activation success but failed to send activation email.");
			}	*/		
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
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonObj = getDeviceInfoByActivationId(connection, activation_id);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find KIOSK info.");
			}

			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==3)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("KIOSK is already inactive.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(connection, id, 3))
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
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonObj = getDeviceInfoByActivationId(connection, activation_id);
			if(jsonObj == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find KIOSK info.");
			}
	
			Long statusLookupId = jsonObj.getLong("status_lookup_id");
			if(statusLookupId==1)
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("KIOSK is already reactivated.");
			
			Long id = jsonObj.getLong("id");
			if(!updateDeviceStatus(connection, id, 1))
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
	
	@PostMapping(value = {"/resendAct"}, produces = "application/json")
	public ResponseEntity<?> resendActivationInfo(@RequestParam("store_id") Long store_id, @RequestParam("activation_id") String activationId, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			Store store = storeService.findStoreById(store_id);
			// send email
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String email = store.getEmail();
			JSONObject activationInfo = getDeviceInfoByActivationId(connection, activationId);
			if(!userEmailUtil.sendActivationInfo(store.getContactPerson(), activationInfo, brandId, email))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to resend activation email.");
			
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Server error. Please try again later.");
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
	
	public boolean getEcposStatus(Connection connection, long store_id) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String prefix = "";
		boolean flag = false;
		
		try {
			stmt = connection.prepareStatement("SELECT ecpos FROM store WHERE id = ? ");	
			stmt.setLong(count++, store_id);
			rs = stmt.executeQuery();			 
			if(rs.next()) {
				flag = rs.getBoolean("ecpos");
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
	
	private String getDevicePrefix(Connection connection, long deviceTypeId) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String prefix = "";
		
		try {
			stmt = connection.prepareStatement("SELECT prefix FROM device_type_lookup WHERE id = ? ");		
			stmt.setLong(count++, deviceTypeId);
			rs = stmt.executeQuery();		 
			if(rs.next()) {
				prefix = rs.getString("prefix");
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return prefix;
	}
	
	private boolean checkDeviceInfoExist(Connection connection, long deviceTypeId, long referenceId) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		boolean exist = false;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM device_info WHERE device_type_lookup_id = ? AND ref_id = ? ");	
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);
			rs = stmt.executeQuery();			
			if(rs.next()) {
				exist = true;
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return exist;
	}
	
	private String createDeviceInfo(Connection connection, long deviceTypeId, long referenceId) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String result = null;
		
		try {
			String prefix = getDevicePrefix(connection, deviceTypeId);				
			String activationId = byodUtil.createUniqueActivationId(prefix);
			stmt = connection.prepareStatement("INSERT INTO device_info (activation_id, activation_key, status_lookup_id, device_type_lookup_id, ref_id, created_date, group_category_id) "
					+ "VALUES (?, ? ,? ,? ,? , GETDATE(), 0); SELECT SCOPE_IDENTITY();");	
			stmt.setString(count++, activationId);
			stmt.setString(count++, byodUtil.createRandomDigit(16));
			stmt.setInt(count++, 1);
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);
			rs = stmt.executeQuery();		
			if(rs.next()) {
				result = activationId;
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return result;
	}
	
	private JSONArray getDeviceInfoByStoreId(Connection connection, long deviceTypeId, long referenceId) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonObj = null;
		JSONArray jsonArray = new JSONArray();
		int count = 1;
		
		try {
			stmt = connection.prepareStatement("SELECT a.*, b.name AS status FROM device_info a "
					+ "INNER JOIN status_lookup b ON a.status_lookup_id = b.id WHERE a.device_type_lookup_id = ? AND a.ref_id = ? ORDER BY a.created_date DESC");	
			stmt.setLong(count++, deviceTypeId);
			stmt.setLong(count++, referenceId);
			rs = stmt.executeQuery();	
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
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return jsonArray;
	}
	
	private JSONObject getDeviceInfoByActivationId(Connection connection, String activationId) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonObj = null;
		//JSONArray jsonArray = new JSONArray();
		int count = 1;
		
		try {
			stmt = connection.prepareStatement("SELECT a.*, b.name AS status, c.name AS device FROM device_info a "
					+ "INNER JOIN status_lookup b ON a.status_lookup_id = b.id "
					+ "INNER JOIN device_type_lookup c ON a.device_type_lookup_id = c.id "
					+ "WHERE a.activation_id = ?");			
			stmt.setString(count++, activationId);
			rs = stmt.executeQuery();	
			if(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("activation_id", rs.getString("activation_id"));
				jsonObj.put("activation_key", rs.getString("activation_key"));
				jsonObj.put("mac_address", rs.getString("mac_address"));
				jsonObj.put("created_date", rs.getString("created_date"));
				jsonObj.put("status_lookup_id", rs.getLong("status_lookup_id"));
				jsonObj.put("status", rs.getString("status"));
				jsonObj.put("device", rs.getString("device"));
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return jsonObj;
	}
	
	private boolean updateDeviceStatus(Connection connection, long id, long statusTypeId) throws Exception {	
		PreparedStatement stmt = null;
		int count = 1;
		boolean flag = false;
		String sqlStatement = "";
		
		try {
			if(statusTypeId==3) {
				// deactivate
				// update group_category_id to 0
				sqlStatement = "UPDATE device_info SET status_lookup_id = ?, mac_address = NULL, last_update_date = GETDATE(), group_category_id = 0 WHERE id = ?;";
			}
			else {
				sqlStatement = "UPDATE device_info SET status_lookup_id = ?, mac_address = NULL, last_update_date = GETDATE() WHERE id = ?;";
			}
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(count++, statusTypeId);
			stmt.setLong(count++, id);
			int rowAffected = stmt.executeUpdate();		
			if(rowAffected==1) {
				flag = true;
			}
			
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
	
	public JSONArray getStoreType(Connection connection) throws Exception{
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM store_type_lookup");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));				
				jsonObj.put("store_type_name", rs.getString("store_type_name"));							
				jsonArr.put(jsonObj);
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return jsonArr;
	}
	
	public JSONArray getPaymentDelayType(Connection connection) throws Exception{
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM payment_delay_lookup ORDER BY id");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));				
				jsonObj.put("payment_delay_name", rs.getString("payment_delay_name"));							
				jsonArr.put(jsonObj);
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return jsonArr;
	}
	
	public JSONArray getStoreTaxType(Connection connection) throws Exception{
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM store_tax_type_lookup");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("store_tax_type_id"));				
				jsonObj.put("store_tax_type_name", rs.getString("store_tax_type_name"));							
				jsonArr.put(jsonObj);
			}
		} catch(Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return jsonArr;
	}
}
