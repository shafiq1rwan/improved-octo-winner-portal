package my.com.byod.login.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.UserEmailUtil;
import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.service.ApplicationUserService;

@RestController
@RequestMapping("/users")
public class UserManagementRestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationUserService applicationUserService;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private UserEmailUtil userEmailUtil;

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

			String randomPass = byodUtil.createRandomString(10);
			user.setPassword(randomPass);
//			user.setPassword(user.getUsername());
			
			Long userId = applicationUserService.createUser(user, jsonData.getString("role"));
			if (userId == 0) {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create new user");
			}
			else {
				//register with selected brand
				int rowAffected = 0;
				if(jsonData.has("brand") && !jsonData.isNull("brand")) {
					rowAffected = applicationUserService.assignedNewUserToBrand(userId, jsonData.getLong("brand"));				
					if(rowAffected == 0)
						return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot assign user to brand");
				}			
				//send email
				Thread sendMailThread = new Thread(){
				    public void run(){
				      System.out.println("Send Mail Running");
				      boolean sendStatus = userEmailUtil.sendUserRegisterPassword(user.getName(),randomPass,user.getEmail());
				      System.out.println("Send Mail End with status = " + sendStatus);
				    }
				};
				sendMailThread.start();
				/*boolean sendStatus = userEmailUtil.sendUserRegisterPassword(user.getName(),randomPass,user.getEmail());
				if(!sendStatus) {
					return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot send email to user");
				}*/
			}
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
	
	@PostMapping(value = "/edit")
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
//			int enable = jsonData.getInt("enabled");
//			System.out.println("enable Value: "+enable);
			user.setEnabled(jsonData.getBoolean("enabled"));
			System.out.println("enabled value: "+jsonData.getBoolean("enabled"));

			if (applicationUserService.findUserByEmail(user.getEmail(), user.getId()))
				throw new IllegalArgumentException(
						constructJsonResponse("01", user.getEmail() + " already being taken"));

			if (applicationUserService.findUserByUsername(user.getUsername(), user.getId()))
				throw new IllegalArgumentException(
						constructJsonResponse("02", user.getUsername() + " already being taken"));

			if (applicationUserService.findUserByMobileNumber(user.getMobileNumber(), user.getId()))
				throw new IllegalArgumentException(
						constructJsonResponse("03", user.getMobileNumber() + " already being taken"));
			
			int rowAffected = applicationUserService.editUser(user, jsonData.getString("role"));
			if (rowAffected == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot edit existing user");
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Internal Server Error");
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN)
				.body("Update Success");
	}

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
		JSONObject jsonUserResult = new JSONObject();
		JSONArray jsonUserArray = new JSONArray();
		JSONArray jsonBrandArray = new JSONArray();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		String role = auth.getAuthorities().iterator().next().toString();
	
		try {
			String sql = "";
			String brandSql = "";
			List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> brands = new ArrayList<Map<String, Object>>();
			List<String> roleList = new ArrayList<String>(Arrays.asList("ROLE_SUPER_GROUP_ADMIN","ROLE_ADMIN","ROLE_USER"));
			List<String> filteredRoleList = null;
			
			if(role.equals("ROLE_SUPER_ADMIN")) {
				sql = "SELECT u.id, u.name, u.email, u.mobileNumber, u.address, u.username, u.enabled, a.authority "
						+ "FROM users u INNER JOIN authorities a ON u.id = a.user_id "
						+ "WHERE a.authority != ?";

				users = jdbcTemplate.queryForList(sql, new Object[] {role});
				
				brandSql = "SELECT * FROM brands";
				brands = jdbcTemplate.queryForList(brandSql);
				
				filteredRoleList = roleList;
			} else if(role.equals("ROLE_ADMIN")){
				List<Long> brandIds = jdbcTemplate.queryForList("SELECT b.id FROM brands b "
						+ "INNER JOIN users_brands ub ON b.id = ub.brand_id "
						+ "INNER JOIN users u ON ub.user_id = u.id "
						+ "WHERE u.username = ?", 
						new Object[] {username}, Long.class);
				
				if(!brandIds.isEmpty()) {
					sql = "SELECT DISTINCT u.id, u.name, u.email, u.mobileNumber, u.address, u.username, u.enabled, a.authority "
							+ "FROM users u INNER JOIN authorities a ON u.id = a.user_id "
							+ "INNER JOIN users_brands ub ON u.id = ub.user_id "
							+ "WHERE a.authority NOT IN('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_SUPER_GROUP_ADMIN') "
							+ "AND ub.brand_id IN(:ids)";
					
					Map<String, List<Long>> paramMap = Collections.singletonMap("ids", brandIds);
					NamedParameterJdbcTemplate template = 
						    new NamedParameterJdbcTemplate(dataSource);
					
					users = template.queryForList(sql, paramMap);
				}
				
				brandSql = "SELECT b.* FROM brands b INNER JOIN users_brands ub "
						+ "ON b.id = ub.brand_id INNER JOIN users u "
						+ "ON ub.user_id = u.id WHERE u.username = ?";
				brands = jdbcTemplate.queryForList(brandSql, new Object[] {username});
								
				filteredRoleList = roleList.stream().filter(r -> !r.equals(role) && !r.equals("ROLE_SUPER_GROUP_ADMIN")).collect(Collectors.toList());
				System.out.println("Filter Me: " + filteredRoleList);
			} 
			else if(role.equals("ROLE_SUPER_GROUP_ADMIN")) {
				List<Long> brandIds = jdbcTemplate.queryForList("SELECT b.id FROM brands b "
						+ "INNER JOIN users_brands ub ON b.id = ub.brand_id "
						+ "INNER JOIN users u ON ub.user_id = u.id "
						+ "WHERE u.username = ?", 
						new Object[] {username}, Long.class);
				
				if(!brandIds.isEmpty()) {
					sql = "SELECT DISTINCT u.id, u.name, u.email, u.mobileNumber, u.address, u.username, u.enabled, a.authority "
							+ "FROM users u INNER JOIN authorities a ON u.id = a.user_id "
							+ "INNER JOIN users_brands ub ON u.id = ub.user_id "
							+ "WHERE a.authority NOT IN('ROLE_SUPER_ADMIN','ROLE_SUPER_GROUP_ADMIN') "
							+ "AND ub.brand_id IN(:ids)";
					
					Map<String, List<Long>> paramMap = Collections.singletonMap("ids", brandIds);
					NamedParameterJdbcTemplate template = 
						    new NamedParameterJdbcTemplate(dataSource);
					
					users = template.queryForList(sql, paramMap);
				}

				brandSql = "SELECT b.* FROM brands b INNER JOIN users_brands ub "
						+ "ON b.id = ub.brand_id INNER JOIN users u "
						+ "ON ub.user_id = u.id WHERE u.username = ?";
				brands = jdbcTemplate.queryForList(brandSql, new Object[] {username});

				filteredRoleList = roleList.stream().filter(r -> !r.equals(role)).collect(Collectors.toList());
			}
			
			if(!users.isEmpty()) {
				for(Map<String, Object> user:users) {
					JSONObject jsonObj = new JSONObject(user);
					jsonUserArray.put(jsonObj);
					}
			}

			if(!brands.isEmpty()) {
				for(Map<String, Object> brand:brands) {
					JSONObject jsonObj = new JSONObject(brand);
					jsonBrandArray.put(jsonObj);
				}
			}

			jsonUserResult.put("role", role);
			jsonUserResult.put("role_list", new JSONArray(filteredRoleList));
			jsonUserResult.put("user_list", jsonUserArray);
			jsonUserResult.put("brand_list", jsonBrandArray);
			
			System.out.println("Users Data: " + jsonUserResult.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Cannot retrive users info. Please try again later");
		}
		return ResponseEntity.ok(jsonUserResult.toString());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> findUserById(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id){		
		JSONObject jsonResult = null;
	
		try {
			String sql = "SELECT u.id, u.name, u.email, u.mobileNumber, u.address, u.username, u.enabled, a.authority "
					+ "FROM users u INNER JOIN authorities a ON u.id = a.user_id "
					+ "WHERE u.id = ?";
			Map<String, Object> user = jdbcTemplate.queryForMap(sql, new Object[] {id});
			
			if(!user.isEmpty())
				jsonResult = new JSONObject(user);
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Cannot retrieve user info. Please try again later");
		}
		return ResponseEntity.ok(jsonResult.toString());
	}
	
	@GetMapping("/{id}/brands")
	public ResponseEntity<?> findBrands(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id){
		JSONArray jsonBrandArray = new JSONArray();	
		try {
			List<Map<String, Object>> usersInBrand = jdbcTemplate.queryForList("SELECT b.id, b.name, " + 
					"CASE WHEN(SELECT COUNT(1) " + 
					"FROM users_brands ub " + 
					"WHERE ub.brand_id = b.id " + 
					"AND " + 
					"ub.user_id = ?) > 0 " + 
					"THEN 1 " + // wan - db migration trace
					"ELSE 0 END as exist " + 
					"FROM brands b", new Object[] {id});

			if(!usersInBrand.isEmpty()) {
				jsonBrandArray = new JSONArray(usersInBrand);
			}
			return ResponseEntity.ok(jsonBrandArray.toString());
		}
		catch(DataAccessException ex) {
			ex.printStackTrace();
			return ResponseEntity.ok(jsonBrandArray.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Cannot retrieve brands info. Please try again later.");
		}
	}
	
	@PostMapping("/assign-brands")
	public ResponseEntity<?> assignBrands(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		
		try {
			JSONObject jsonObject = new JSONObject(data);
			Long userId = jsonObject.getLong("userId");
			JSONArray brandArray = jsonObject.optJSONArray("brands");
			JSONArray tempArray = new JSONArray();
			ArrayList listA = new ArrayList<>();
			ArrayList listB = new ArrayList<>();
			ResultSet rs = null;
			
			connection = dataSource.getConnection();

			if(brandArray.length()!= 0) {
				String selectSql = "SELECT brand_id,permission FROM users_brands WHERE user_id = ?";
				stmt = connection.prepareStatement(selectSql);
				stmt.setLong(1,userId);
				rs = stmt.executeQuery();
				
				while(rs.next()) {
					
					//backup existing data
					JSONObject temp = new JSONObject();
					temp.put("brand_id", rs.getLong("brand_id"));
					/*temp.put("permission", rs.getString("permission"));*/
							
					/*tempArray.put(temp);*/
					listA.add(rs.getLong("brand_id"));
				}
			}

//			stmt2 = connection.prepareStatement("DELETE FROM users_brands WHERE user_id = ?");
//			stmt2.setLong(1,userId);
//			stmt2.executeUpdate();
			
			for(int i = 0; i < brandArray.length(); i++) {
				JSONObject jsonBrandObj = brandArray.getJSONObject(i);
				listB.add(jsonBrandObj.getLong("id"));
			}

			if(listB.equals(listA)) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
						.body("Brand already assigned to this user. Please check again");
			}else {
				listB.removeAll(listA);
			}
			System.out.println("result list: "+listB);
			
			if(brandArray.length() != 0) {
				connection.setAutoCommit(false);
				String insertionSql = "INSERT INTO users_brands(brand_id,user_id) VALUES (?,?)";

				for(int i = 0; i < listB.size(); i++) {
//					JSONObject jsonBrandObj = brandArray.getJSONObject(i);
					Long brandId = (Long) listB.get(i);
					
						stmt3 = connection.prepareStatement(insertionSql);
						stmt3.setLong(1, brandId);
						stmt3.setLong(2, userId);
						stmt3.executeUpdate();
		
						connection.commit();
				}
				
				//UPDATE BASED ON WHERE
//				if(tempArray.length()!=0) {
//					String updateSql = "UPDATE users_brands SET permission = ? WHERE user_id = ? AND brand_id = ?";
//					for(int j = 0; j < tempArray.length(); j++) {
//						JSONObject temp = tempArray.getJSONObject(j);
//							stmt3 = connection.prepareStatement(updateSql);
//							stmt3.setString(1, temp.getString("permission"));
//							stmt3.setLong(2, userId);
//							stmt3.setLong(3, temp.getLong("brand_id"));
//							stmt3.executeUpdate();
//			
//							connection.commit();
//					}
//				}

			}
			return ResponseEntity.ok(null);
		} catch(Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot assign user to brands. Please try again later");
		} finally {
			if(connection!=null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	@PostMapping("/assign-access-rights")
	public ResponseEntity<?> assignAccessRights(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){	
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		String userRole = "";

		try {
			JSONObject jsonObject = new JSONObject(data);
			Long userId = jsonObject.getLong("user_id");
			Long brandId = jsonObject.getLong("brand_id");
			JSONArray permissionArray = jsonObject.optJSONArray("permissions");
	
			connection = dataSource.getConnection();	
			
			//Get the user role
			String queryUserRoleSql = "SELECT authority FROM authorities WHERE user_id = ?";
			stmt = connection.prepareStatement(queryUserRoleSql);
			stmt.setLong(1, userId);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				userRole = rs.getString("authority");
			}
			
			String updateSql = "UPDATE users_brands SET permission = ? WHERE brand_id = ? AND user_id = ?";
			String binaryString = "";
				
			if(permissionArray.length()==0) {
				binaryString = String.format("%-8s", "0").replace(" ", "0");
			} else {
				//String binaryTempString = "0"; //because store is always false
				String binaryTempString = "";
				
				if(userRole.equals("ROLE_USER")) {
					//user dont have store access
					binaryTempString += "0";
					for(int i =0; i<permissionArray.length();i++) {
						JSONObject jsonPermissionObj = permissionArray.getJSONObject(i);
						binaryTempString += jsonPermissionObj.getBoolean("exist")?"1":"0";
					}
					System.out.println(binaryTempString);
				} else {
					for(int i =0; i<permissionArray.length();i++) {	
						JSONObject jsonPermissionObj = permissionArray.getJSONObject(i);
						binaryTempString += jsonPermissionObj.getBoolean("exist")?"1":"0";
					}
				}
				//Pad extra zeros to the right
				binaryString = String.format("%-8s", binaryTempString).replace(" ", "0");
			}
			int decimal = Integer.parseInt(binaryString,2);
			String hexString = Integer.toString(decimal,16);
			
			System.out.println("Binary Permission :" + binaryString);
			System.out.println("Hex Permission :" + hexString);

			stmt2 = connection.prepareStatement(updateSql);
			stmt2.setString(1, hexString);
			stmt2.setLong(2, brandId);
			stmt2.setLong(3, userId);
			stmt2.executeUpdate();

			return ResponseEntity.ok(null);
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot assign access rights to user. Please try again later");
		} finally {
			if(connection!=null) {
				try {
					connection.setAutoCommit(true);
					rs.close();
					stmt.close();
					stmt2.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@GetMapping("/access-rights")
	public ResponseEntity<?> findAccessRightsByUserAndBrand(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id, @RequestParam("brandId") Long brandId){
		JSONArray jsonAccessRightsArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
			
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT permission FROM users_brands WHERE brand_id = ? AND user_id = ?");
			stmt.setLong(1, brandId);
			stmt.setLong(2, id);
			rs = stmt.executeQuery();
			
			if(!rs.next()) {
				//new records		
				//Get the user role
				String queryUserRoleSql = "SELECT authority FROM authorities WHERE user_id = ?";
				stmt3 = connection.prepareStatement(queryUserRoleSql);
				stmt3.setLong(1, id);
				rs3 = stmt3.executeQuery();
								
				String permissionLookupQuerySql = "SELECT *, exist = 0 FROM permission_lookup"; // wan - db migration trace
				
				if(rs3.next()) { //if it is USER change the query to exclude Store permission
					if(rs3.getString("authority").equals("ROLE_USER")) {
						permissionLookupQuerySql = "SELECT *, exist = 0 FROM permission_lookup WHERE id != 1"; // wan - db migration trace
					}
				} 
				
				stmt2 = connection.prepareStatement(permissionLookupQuerySql);
				rs2 = stmt2.executeQuery();
				
				while(rs2.next()) {
					JSONObject jsonAccessRightObj = new JSONObject();
					jsonAccessRightObj.put("id", rs2.getLong("id"));
					jsonAccessRightObj.put("name", rs2.getString("perm_name"));
					jsonAccessRightObj.put("exist", rs2.getBoolean("exist"));
					
					jsonAccessRightsArray.put(jsonAccessRightObj);
				}
			} else{
				//existing records 
				String permission = rs.getString("permission");
				List<String> accessRights = new ArrayList<String>();
				
				stmt2 = connection.prepareStatement("SELECT * FROM permission_lookup");
				rs2 = (ResultSet)stmt2.executeQuery();
				
				while(rs2.next()) {
					System.out.println("name " + rs2.getString("perm_name"));
					accessRights.add(rs2.getString("perm_name"));
				}
				
				int i = Integer.parseInt(permission, 16);
			    String binaryString = String.format("%8s", Integer.toBinaryString(i)).replace(" ", "0");	
			    String[] resultArray = binaryString.split("");
			    
			    boolean[] permissionBooleans = new boolean[accessRights.size()];
			    
			    for(int j=0;j<permissionBooleans.length;j++) {
			    	permissionBooleans[j] = (!resultArray[j].equals("0"));
			    	System.out.println(permissionBooleans[j]);
			    }
			    
				//Get the user role
				//String queryUserRoleSql = "SELECT * FROM authorities WHERE user_id = ?";
				stmt3 = connection.prepareStatement("SELECT authority FROM authorities WHERE user_id = ?");
				stmt3.setLong(1, id);
				rs3 = stmt3.executeQuery();
			    
				boolean isUserRole = false;
				
				if(rs3.next()) {
					System.out.println("Existing Role: " + rs3.getString("authority"));
					if(rs3.getString("authority").equals("ROLE_USER")) {
						isUserRole = true;
					}	
				}
			    
				if(isUserRole) {
				    for(int k=1; k<accessRights.size();k++) { 
				    	JSONObject jsonObject = new JSONObject();
				    	int index = k+1;
				    	jsonObject.put("id", index);
				    	jsonObject.put("name", accessRights.get(k));
				    	jsonObject.put("exist", permissionBooleans[k]);
				    	
				    	jsonAccessRightsArray.put(jsonObject);
				    	System.out.println(jsonObject);
				    }
				} else {
				    for(int k=0; k<accessRights.size();k++) { 
				    	JSONObject jsonObject = new JSONObject();
				    	jsonObject.put("id", k);
				    	jsonObject.put("name", accessRights.get(k));
				    	jsonObject.put("exist", permissionBooleans[k]);
				    	
				    	jsonAccessRightsArray.put(jsonObject);
				    	System.out.println(jsonObject);
				    }
				}
		
			}
			return ResponseEntity.ok(jsonAccessRightsArray.toString());
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return ResponseEntity.ok(jsonAccessRightsArray.toString());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Cannot retrieve brands info. Please try again later.");
		}
		finally {
			if(connection!=null) {
				try {	
					stmt.close();
					stmt2.close();
					stmt3.close();
					rs.close();
					rs2.close();
					rs3.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@GetMapping("/assigned-brands")
	public ResponseEntity<?> findAssignedBrandByUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("userId") Long userId){
		
		JSONArray jsonAssignedBrandArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT b.* FROM brands b INNER JOIN users_brands ub ON b.id = ub.brand_id WHERE ub.user_id = ?");
			stmt.setLong(1,userId);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonAssignedBrandObj = new JSONObject();
				jsonAssignedBrandObj.put("id", rs.getLong("id"));
				jsonAssignedBrandObj.put("name", rs.getString("name"));
				
				jsonAssignedBrandArray.put(jsonAssignedBrandObj);
			}
			return ResponseEntity.ok(jsonAssignedBrandArray.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Cannot retrieve assigned brands info. Please try again later.");
		}
		finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	@PostMapping("/unassign-brands")
	public ResponseEntity<?> unassignBrands(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id, @RequestParam("brandId") Long brandId){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			
			System.out.println("id: "+id);
			System.out.println("brand_id: "+brandId);
			
			connection = dataSource.getConnection();
			
			stmt = connection.prepareStatement("DELETE FROM users_brands WHERE user_id = ? AND brand_id = ?");
			stmt.setLong(1,id);
			stmt.setLong(2, brandId);
			stmt.executeUpdate();
			
			return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.TEXT_PLAIN).body("Unassigned Success");
			
		} catch(Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot unassign user from the brands. Please try again later");
		} finally {
			if(connection!=null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
