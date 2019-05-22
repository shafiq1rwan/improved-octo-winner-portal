package my.com.byod.admin.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.entity.Location;
import my.com.byod.admin.entity.Store;
import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;
import my.com.byod.admin.util.QRGenerate;
import my.com.byod.admin.util.UserEmailUtil;
import my.com.byod.order.util.AESEncryption;

@RestController
@RequestMapping("/menu/store")
public class StoreRestController {
	
	@Value("${get-upload-path}")
	private String displayFilePath;
	
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
	public ResponseEntity<?> findAllStore(HttpServletRequest request, HttpServletResponse response) {
		List<Store> stores = new ArrayList<Store>();
		Connection connection = null;;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String sqlStatement = "SELECT * FROM store ";
			stmt = connection.prepareStatement(sqlStatement);
			rs = stmt.executeQuery();
			while(rs.next()) {
				Store store = new Store();
				store.setId(rs.getLong("id"));
				store.setGroupCategoryId(rs.getLong("group_category_id"));
				store.setBackendId(rs.getString("backend_id"));
				store.setName(rs.getString("store_name"));
				store.setLogoPath(displayFilePath + brandId + "/" + rs.getString("store_logo_path"));
				Location location = new Location();
				location.setAddress(rs.getString("store_address"));
				location.setLongitude(rs.getDouble("store_longitude"));
				location.setLatitude(rs.getDouble("store_latitude"));
				location.setCountry(rs.getString("store_country"));
				store.setLocation(location);
				store.setCurrency(rs.getString("store_currency"));
				store.setPublish(rs.getBoolean("is_publish"));
				store.setCreatedDate(rs.getDate("created_date"));
				store.setOperatingStartTime(rs.getTime("store_start_operating_time"));
				store.setOperatingEndTime(rs.getTime("store_end_operating_time"));
				store.setEcpos(rs.getBoolean("ecpos"));
				store.setEcposUrl(rs.getString("ecpos_url"));
				store.setEcposTakeawayDetailFlag(rs.getBoolean("ecpos_takeaway_detail_flag"));
				store.setLoginTypeId(rs.getLong("login_type_id"));
				store.setLoginSwitchFlag(rs.getBoolean("login_switch_flag"));
				store.setContactPerson(rs.getString("store_contact_person"));
				store.setMobileNumber(rs.getString("store_contact_hp_number"));
				store.setEmail(rs.getString("store_contact_email"));
				store.setStoreTypeId(rs.getLong("store_type_id"));
				store.setKioskPaymentDelayId(rs.getLong("kiosk_payment_delay_id"));
				store.setByodPaymentDelayId(rs.getLong("byod_payment_delay_id"));
				store.setStoreTaxTypeId(rs.getLong("store_tax_type_id"));
				stores.add(store);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support."); 
		} finally {
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<List<Store>>(stores, HttpStatus.OK);
	}

	@GetMapping("/storeById")
	public ResponseEntity<?> findStoreById(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		Store store = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String sqlStatement = "SELECT * FROM store WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				store = new Store();
				store.setId(rs.getLong("id"));
				store.setGroupCategoryId(rs.getLong("group_category_id"));
				store.setBackendId(rs.getString("backend_id"));
				store.setName(rs.getString("store_name"));
				store.setLogoPath(displayFilePath + brandId + "/" + rs.getString("store_logo_path"));
				Location location = new Location();
				location.setAddress(rs.getString("store_address"));
				location.setLongitude(rs.getDouble("store_longitude"));
				location.setLatitude(rs.getDouble("store_latitude"));
				location.setCountry(rs.getString("store_country"));
				store.setLocation(location);
				store.setCurrency(rs.getString("store_currency"));
				store.setPublish(rs.getBoolean("is_publish"));
				store.setCreatedDate(rs.getDate("created_date"));
				store.setOperatingStartTime(rs.getTime("store_start_operating_time"));
				store.setOperatingEndTime(rs.getTime("store_end_operating_time"));
				store.setEcpos(rs.getBoolean("ecpos"));
				store.setEcposUrl(rs.getString("ecpos_url"));
				store.setEcposTakeawayDetailFlag(rs.getBoolean("ecpos_takeaway_detail_flag"));
				store.setLoginTypeId(rs.getLong("login_type_id"));
				store.setLoginSwitchFlag(rs.getBoolean("login_switch_flag"));
				store.setContactPerson(rs.getString("store_contact_person"));
				store.setMobileNumber(rs.getString("store_contact_hp_number"));
				store.setEmail(rs.getString("store_contact_email"));
				store.setStoreTypeId(rs.getLong("store_type_id"));
				store.setKioskPaymentDelayId(rs.getLong("kiosk_payment_delay_id"));
				store.setByodPaymentDelayId(rs.getLong("byod_payment_delay_id"));
				store.setStoreTaxTypeId(rs.getLong("store_tax_type_id"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support."); 
		} finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
			}
		}
		return new ResponseEntity<Store>(store, HttpStatus.OK);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createStore(@RequestBody Store store, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			
			// check for store name duplication
			if(checkStoreExistByName(connection, store.getName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Duplicate store name. Please enter a different name.");
			}	
			String backendId = byodUtil.createUniqueBackendId("S");
			// check for backend id duplication
			if(checkStoreExistByBackendId(connection, backendId)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Duplicate Backend ID. Please try again.");
			}
			
			Calendar startTime = Calendar.getInstance();
			startTime.setTime(store.getOperatingStartTime());
			startTime.set(Calendar.SECOND, 0);
			startTime.set(Calendar.MILLISECOND, 0);
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(store.getOperatingEndTime());
			endTime.set(Calendar.SECOND, 0);
			endTime.set(Calendar.MILLISECOND, 0);
			
			String sqlStatement = "INSERT INTO store(backend_id,store_name,store_logo_path,store_address,store_longitude,store_latitude,store_country,store_currency, " + 
					"is_publish, store_start_operating_time, store_end_operating_time, ecpos, store_contact_person, store_contact_hp_number, store_contact_email, store_type_id, kiosk_payment_delay_id, byod_payment_delay_id, store_tax_type_id, ecpos_url, ecpos_takeaway_detail_flag, login_type_id, login_switch_flag, created_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE());";
			int count = 1;
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(count++, backendId);
			stmt.setString(count++, store.getName());
			stmt.setString(count++, byodUtil.saveImageFile(brandId,"imgS", store.getLogoPath(), null));
			stmt.setString(count++, store.getLocation().getAddress());
			stmt.setDouble(count++, store.getLocation().getLongitude());
			stmt.setDouble(count++, store.getLocation().getLatitude());
			stmt.setString(count++, store.getLocation().getCountry());
			stmt.setString(count++, store.getCurrency());
			stmt.setBoolean(count++, store.isPublish());
			stmt.setTimestamp(count++, new java.sql.Timestamp(startTime.getTimeInMillis()));
			stmt.setTimestamp(count++, new java.sql.Timestamp(endTime.getTimeInMillis()));
			stmt.setBoolean(count++, store.getEcpos());
			stmt.setString(count++, store.getContactPerson());
			stmt.setString(count++, store.getMobileNumber());
			stmt.setString(count++, store.getEmail());
			stmt.setLong(count++, store.getStoreTypeId());
			stmt.setLong(count++, store.getKioskPaymentDelayId());
			stmt.setLong(count++, store.getByodPaymentDelayId());
			stmt.setLong(count++, store.getStoreTaxTypeId());
			stmt.setString(count++, store.getEcposUrl());
			stmt.setBoolean(count++, store.getEcposTakeawayDetailFlag());
			stmt.setLong(count++, store.getLoginTypeId());
			stmt.setBoolean(count++, store.getLoginSwitchFlag());
			stmt.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
		} finally {
			if(connection != null) {
				try {
			connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editStore(@RequestBody Store store, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String sqlStatement = "SELECT * FROM store WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, store.getId());
			rs = stmt.executeQuery();
			if(rs.next()) {
				Calendar startTime = Calendar.getInstance();
				startTime.setTime(store.getOperatingStartTime());
				startTime.set(Calendar.SECOND, 0);
				startTime.set(Calendar.MILLISECOND, 0);
				Calendar endTime = Calendar.getInstance();
				endTime.setTime(store.getOperatingEndTime());
				endTime.set(Calendar.SECOND, 0);
				endTime.set(Calendar.MILLISECOND, 0);
				
				System.out.println("test1:" + store.getEcposTakeawayDetailFlag());
				System.out.println("test2:" + store.getLoginTypeId());
				System.out.println("test3:" + store.getLoginSwitchFlag());
				
				if(store.getLogoPath()!=null) 
					sqlStatement = "UPDATE store SET store_name = ?,store_logo_path = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ?, store_contact_person = ?, store_contact_hp_number = ?, store_contact_email = ?, store_type_id = ?, kiosk_payment_delay_id = ?, byod_payment_delay_id = ?, store_tax_type_id = ?, ecpos_url = ?, ecpos_takeaway_detail_flag = ?, login_type_id = ?, login_switch_flag = ? WHERE id = ?;";			
				else 
					sqlStatement = "UPDATE store SET store_name = ?,store_address = ?,store_longitude = ?,store_latitude = ?,store_country = ?,store_currency = ?, is_publish = ?, store_start_operating_time = ?, store_end_operating_time = ?, last_update_date = GETDATE(), ecpos = ?, store_contact_person = ?, store_contact_hp_number = ?, store_contact_email = ?, store_type_id = ?, kiosk_payment_delay_id = ?, byod_payment_delay_id = ?, store_tax_type_id = ?, ecpos_url = ?, ecpos_takeaway_detail_flag = ?, login_type_id = ?, login_switch_flag = ? WHERE id = ?";
				int count = 1;
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setString(count++, store.getName());
				if(store.getLogoPath()!=null)
					stmt.setString(count++, byodUtil.saveImageFile(brandId, "imgS", store.getLogoPath(), rs.getString("store_logo_path")));
				stmt.setString(count++, store.getLocation().getAddress());
				stmt.setDouble(count++, store.getLocation().getLongitude());
				stmt.setDouble(count++, store.getLocation().getLatitude());
				stmt.setString(count++, store.getLocation().getCountry());
				stmt.setString(count++, store.getCurrency());
				stmt.setBoolean(count++, store.isPublish());
				stmt.setTimestamp(count++, new java.sql.Timestamp(startTime.getTimeInMillis()));
				stmt.setTimestamp(count++, new java.sql.Timestamp(endTime.getTimeInMillis()));
				stmt.setBoolean(count++, store.getEcpos());
				stmt.setString(count++, store.getContactPerson());
				stmt.setString(count++, store.getMobileNumber());
				stmt.setString(count++, store.getEmail());
				stmt.setLong(count++, store.getStoreTypeId());
				stmt.setLong(count++, store.getKioskPaymentDelayId());
				stmt.setLong(count++, store.getByodPaymentDelayId());
				stmt.setLong(count++, store.getStoreTaxTypeId());
				stmt.setString(count++, store.getEcposUrl());
				stmt.setBoolean(count++, store.getEcposTakeawayDetailFlag());
				stmt.setLong(count++, store.getLoginTypeId());
				stmt.setBoolean(count++, store.getLoginSwitchFlag());
				stmt.setLong(count++, store.getId());
				stmt.executeUpdate();
				
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
			else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Store info not found.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
		} finally {
			if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
		return new ResponseEntity<>(HttpStatus.OK);
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
			jsonResult.put("storeLoginType", getStoreLoginType(connection));
			
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
	public ResponseEntity<?> createStaff(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj  =  new JSONObject(formfield);
			
			if(!jsonObj.has("store_id")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Unable to find store detail.");
			}
			if(jsonObj.getString("name")==null || jsonObj.getString("name").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Staff name cannot be empty.");
			}
			if(jsonObj.getString("email")==null || jsonObj.getString("email").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Staff email cannot be empty.");
			}
			if(jsonObj.getString("mobilePhone")==null || jsonObj.getString("mobilePhone").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Phone number cannot be empty.");
			}
			if(jsonObj.getString("username")==null || jsonObj.getString("username").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Username cannot be empty.");
			}
			if(jsonObj.getString("password")==null || jsonObj.getString("password").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Password cannot be empty.");
			}
			if(!jsonObj.has("role_id") || jsonObj.getLong("role_id")==0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Role cannot be empty.");
			}
			
			String encPassword = AESEncryption.encrypt(jsonObj.getString("password"));		
			connection = dbConnectionUtil.retrieveConnection(request);
			
			// check for existing staff
			String sqlStatement = "SELECT id FROM staff WHERE staff_username = ? AND store_id = ?";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(1, jsonObj.getString("username"));
			stmt.setLong(2, jsonObj.getLong("store_id"));
			rs = stmt.executeQuery();
			if(rs.next()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Username has already existed for this store.");
			}
			
			int count = 1;
			sqlStatement = "INSERT INTO staff (store_id, staff_name, staff_username, staff_password, staff_role, staff_contact_hp_number,"
			 		+ "staff_contact_email, is_active, created_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE()); SELECT SCOPE_IDENTITY();";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setString(count++, jsonObj.getString("name"));
			stmt.setString(count++, jsonObj.getString("username"));
			stmt.setString(count++, encPassword);
			stmt.setLong(count++, jsonObj.getLong("role_id"));
			stmt.setString(count++, jsonObj.getString("mobilePhone"));
			stmt.setString(count++, jsonObj.getString("email"));
			stmt.setLong(count++, 1);
			
			rs = stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj.put("staff_id", rs.getInt(1));
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	public ResponseEntity<?> updateStaff(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	
	@GetMapping(value = {"/ecpos/getAllTables"}, produces = "application/json")
	public String getAllTables(@RequestParam("store_id") Long store_id, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			// get all active tables
			stmt = connection.prepareStatement("SELECT * FROM table_setting "
			 		+ "WHERE store_id = ? AND status_lookup_id = 2 ");
			stmt.setLong(1, store_id);
			rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("tableName", rs.getString("table_name"));
				jsonObj.put("createdDate", rs.getString("created_date"));
				jsonArray.put(jsonObj);
			}
			jsonObjResult = new JSONObject();
			jsonObjResult.put("data", jsonArray);
			/*jsonObjResult.put("tableCount", jsonArray.length());*/
			
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
	
	@GetMapping(value = {"/ecpos/tableById"}, produces = "application/json")
	public String getTableById(@RequestParam("store_id") Long store_id, @RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = null;
		//JSONObject jsonObjResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			 stmt = connection.prepareStatement("SELECT * FROM table_setting WHERE store_id = ? AND id = ? ");
			 stmt.setLong(1, store_id);
			 stmt.setLong(2, id);
			 rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("tableName", rs.getString("table_name"));
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
	
	@PostMapping(value = {"/ecpos/createTable"}, produces = "application/json")
	public ResponseEntity<?> createTable(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jsonObj  =  new JSONObject(formfield);		
			if(!jsonObj.has("store_id")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Unable to find store detail.");
			}
			if(jsonObj.getString("tableName")==null || jsonObj.getString("tableName").trim().equals("")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Table name cannot be empty.");
			}
				
			connection = dbConnectionUtil.retrieveConnection(request);		
			// check for existing table name
			String sqlStatement = "SELECT id FROM table_setting WHERE table_name = ? AND store_id = ?";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(1, jsonObj.getString("tableName"));
			stmt.setLong(2, jsonObj.getLong("store_id"));
			rs = stmt.executeQuery();
			if(rs.next()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Duplicate table name for this store.");
			}
			
			int count = 1;
			sqlStatement = "INSERT INTO table_setting (store_id, table_name, status_lookup_id, created_date) VALUES (?, ?, ?, GETDATE()); SELECT SCOPE_IDENTITY();";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setString(count++, jsonObj.getString("tableName"));
			stmt.setLong(count++, 2);	
			rs = stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj.put("id", rs.getInt(1));
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	
	@PostMapping(value = {"/ecpos/updateTable"}, produces = "application/json")
	public ResponseEntity<?> updateTable(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
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
			if(!jsonObj.has("id")) {
				jsonObj.put("error", "Unable to find table detail");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.NOT_FOUND);
			}
			if(jsonObj.getString("tableName")==null || jsonObj.getString("tableName").trim().equals("")) {
				jsonObj.put("error", "Table name cannot be empty");
				return new ResponseEntity<JSONObject>(jsonObj, HttpStatus.BAD_REQUEST);
			}
			
			connection = dbConnectionUtil.retrieveConnection(request);	
			// check for existing table name
			String sqlStatement = "SELECT id FROM table_setting WHERE table_name = ? AND store_id = ? AND id != ?";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(1, jsonObj.getString("tableName"));
			stmt.setLong(2, jsonObj.getLong("store_id"));
			stmt.setLong(3, jsonObj.getLong("id"));
			rs = stmt.executeQuery();
			if(rs.next()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Duplicate table name for this store.");
			}
			
			stmt = connection.prepareStatement("UPDATE table_setting SET table_name = ?, last_update_date = GETDATE() WHERE store_id = ? AND id = ?; SELECT SCOPE_IDENTITY();");	
			stmt.setString(count++, jsonObj.getString("tableName"));
			stmt.setLong(count++, jsonObj.getLong("store_id"));
			stmt.setLong(count++, jsonObj.getLong("id"));
			
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonObj.put("id", rs.getInt(1));
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	
	@PostMapping(value = {"/ecpos/generateStaffQR"}, produces = "application/json")
	public ResponseEntity<?> generateStaffQR(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			JSONObject requestObj = new JSONObject(formfield);
			if(!requestObj.has("staff_id")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Staff ID required.");
			}
				
			connection = dbConnectionUtil.retrieveConnection(request);
			String sqlStatement = "SELECT store_id, staff_username, staff_password FROM staff WHERE id = ?";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, requestObj.getLong("staff_id"));
			rs = stmt.executeQuery();
			if(!rs.next()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("No such staff.");
			} else {
				String qrImg = Base64.getEncoder().encodeToString(QRGenerate.generateQRImage(AESEncryption.encrypt(rs.getLong("store_id") + String.valueOf((char)28) + rs.getString("staff_username") + String.valueOf((char)28) + rs.getString("staff_password")), 300, 300));
				jsonObj.put("qrImg", qrImg);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	
	@PostMapping(value = {"/ecpos/syncTrans"}, produces = "application/json")
	public ResponseEntity<?> syncTrans(@RequestParam("store_id") Long store_id, @RequestParam("activation_id") String activationId, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;
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
			else{
				// ecpos only one record
				jsonObj = jsonArray.getJSONObject(0);
				if(jsonObj.getLong("status_lookup_id")!=2) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS is not active.");				
				}
			}
				
			String sqlStatement = "SELECT ecpos_url FROM store WHERE id = ?";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, store_id);
			rs = stmt.executeQuery();
			if(!rs.next()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Invalid ECPOS URL.");
			} else {
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String ecposUrl = rs.getString("ecpos_url");
				String url = ecposUrl + "/syncTransaction";
				
				JSONObject sendData = new JSONObject();
				sendData.put("storeId", String.valueOf(store_id));
				sendData.put("brandId", brandId);
				
				URL object = new URL(url);
				HttpURLConnection con = (HttpURLConnection) object.openConnection();
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("POST");

				OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
				wr.write(sendData.toString());
				wr.flush();

				StringBuilder sb = new StringBuilder();
				int httpResult = con.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();

					JSONObject returnObject = new JSONObject(sb.toString());
					System.out.println(returnObject);
					if (!(returnObject.has("resultCode") && returnObject.has("resultMessage") && returnObject.getString("resultCode").equals("00"))) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body(returnObject.getString("resultMessage"));
					}
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("ECPOS invalid response.");				
				}
			}
		}catch (MalformedURLException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Invalid ECPOS URL.");
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			JSONArray jsonArray = getDeviceInfoByStoreId(connection, 1, store_id);
			if(jsonArray.length()==0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find ECPOS info.");
			}
			
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String sqlStatement = "SELECT store_contact_email, store_contact_person FROM store WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, store_id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				String email = rs.getString("store_contact_email");
				String contactPerson = rs.getString("store_contact_person");
				JSONObject activationInfo = getDeviceInfoByActivationId(connection, activationId);
				// send email
				if(!userEmailUtil.sendActivationInfo(contactPerson, activationInfo, brandId, email))
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to resend activation email.");
			}
			else
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to get store info.");			
		}catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support.");
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
	
	public boolean checkStoreExistByName(Connection connection, String store_name) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM store WHERE store_name = ? ");	
			stmt.setString(1, store_name);
			rs = stmt.executeQuery();			 
			if(rs.next()) {
				flag = true;
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
	
	public boolean checkStoreExistByBackendId(Connection connection, String backend_id) throws Exception {	
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean flag = false;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM store WHERE backend_id = ? ");	
			stmt.setString(1, backend_id);
			rs = stmt.executeQuery();			 
			if(rs.next()) {
				flag = true;
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
	
	public JSONArray getStoreLoginType(Connection connection) throws Exception{
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = connection.prepareStatement("SELECT * FROM login_type_lookup ");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));				
				jsonObj.put("login_type_name", rs.getString("login_type_name"));							
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
