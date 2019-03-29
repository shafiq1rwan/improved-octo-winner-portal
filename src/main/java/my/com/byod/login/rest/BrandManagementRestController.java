package my.com.byod.login.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.AccessRightsUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/brands")
public class BrandManagementRestController {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@Autowired
	private AccessRightsUtil accessRightsUtil;

	@GetMapping(value = "/user")
	public ResponseEntity<?> findBrandsByUsername(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray brandArray = new JSONArray();

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			String role = auth.getAuthorities().iterator().next().toString();

			List<Map<String, Object>> brands = null;

			if (role.equals("ROLE_SUPER_ADMIN")) {
				brands = jdbcTemplate.queryForList("SELECT b.* FROM brands b");
			} else {
				brands = jdbcTemplate.queryForList(
						"SELECT b.* FROM brands b " + "INNER JOIN users_brands ub ON b.id = ub.brand_id "
								+ "INNER JOIN users u ON ub.user_id = u.id " + "WHERE u.username = ?",
						new Object[] { username });
			}

			if (!brands.isEmpty()) {
				List<JSONObject> jsonObj = new ArrayList<JSONObject>();

				for (Map<String, Object> brand : brands) {
					JSONObject obj = new JSONObject(brand);
					jsonObj.add(obj);
				}
				brandArray = new JSONArray(jsonObj);
				jsonResult.put("brands", brandArray);
			}
			jsonResult.put("role", role);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
					.body("Error occured while retrieving brand selection");
		}
		return ResponseEntity.ok(jsonResult.toString());
	}

	@PostMapping(value = "/redirect")
	public ResponseEntity<?> changeConnectionDbSource(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection conn = null;

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();
			System.out.println("Hello My " + username);
			
			JSONObject jsonData = new JSONObject(data);
			conn = dbConnectionUtil.getConnection(jsonData.getLong("id"));

			if (conn != null) {
				dbConnectionUtil.setBrandId(jsonData.getLong("id"), request);
				accessRightsUtil.setupAccessRightByUsername(username, jsonData.getLong("id"),request);
			} else {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN)
						.body("Error occured while connecting to database");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot redirect to brand. Please try again later.");
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	@GetMapping(value = "")
	public ResponseEntity<?> findBrandById(@RequestParam("id") Long id, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject jsonResult = null;
		try {
			Map<String, Object> brand = jdbcTemplate.queryForMap("SELECT * FROM brands WHERE id = ?",
					new Object[] { id });
			if (!brand.isEmpty()) {
				jsonResult = new JSONObject();
				jsonResult.put("id", (Long) brand.get("id"));
				jsonResult.put("name", (String) brand.get("name"));
				jsonResult.put("dbDomain", (String) brand.get("brand_db_domain"));
				jsonResult.put("dbName", (String) brand.get("brand_db_name"));
				jsonResult.put("dbUsername", (String) brand.get("brand_db_user"));
				jsonResult.put("dbPassword", (String) brand.get("brand_db_password"));
				jsonResult.put("dbPort", (int) brand.get("brand_db_port"));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN)
						.body("Brand not found");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot retrieved selected brand info. Please try again later.");
		}
		return ResponseEntity.ok(jsonResult.toString());
	}

	@PostMapping(value = "/create")
	public ResponseEntity<?> createBrand(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String name = jsonData.getString("name");
			int rowAffected = 0;

			if (checkDuplicateDbName(name).isEmpty()) {
				SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("create_brand_db");
				SqlParameterSource input = new MapSqlParameterSource().addValue("db_name",
						jsonData.getString("db_name"));
				Map<String, Object> output = jdbcCall.execute(input);

				System.out.println(output.values().toString());

				if (!output.isEmpty()) {
					int result = (int) output.get("db_creation_result");
					if (result == 1) {
						KeyHolder keyHolder = new GeneratedKeyHolder();
						rowAffected = jdbcTemplate.update(
								new PreparedStatementCreator() {
							        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							        	PreparedStatement stmt = null;
							        	String sqlStatement = "INSERT INTO brands(name,brand_db_domain,brand_db_name,brand_db_user,brand_db_password,brand_db_port)VALUES(?,?,?,?,?,?)";
							        	try {
							        		stmt = connection.prepareStatement(sqlStatement, new String[] {"id"});
							        		stmt.setString(1, name);
							        		stmt.setString(2, jsonData.getString("db_domain"));
							        		stmt.setString(3, jsonData.getString("db_name"));
							        		stmt.setString(4, jsonData.getString("db_user"));
							        		stmt.setString(5, jsonData.getString("db_password"));
							        		stmt.setInt(6, jsonData.getInt("db_port"));							       
							        	}catch(Exception e) {
							        		e.printStackTrace();
							        	}
							        	 return stmt;
							        }
							    },
							    keyHolder);
						
						if(rowAffected!=0) {
							// insert brand id in brand db
							Long brandId = keyHolder.getKey().longValue();
							Connection connection = dbConnectionUtil.getConnection(brandId);
							String sqlStatement = "INSERT INTO general_configuration (description, parameter, value) VALUES (?, ?, ?)";
							PreparedStatement stmt = connection.prepareStatement(sqlStatement);
							stmt.setString(1, "Brand Identity Number");
							stmt.setString(2, "BRAND_ID");
							stmt.setLong(3, brandId);
							int rowAffected2 = stmt.executeUpdate();
							if(rowAffected2 == 0)
								return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Unable to create brand ID");
						}					

/*	rowAffected = jdbcTemplate.update(
								"INSERT INTO brands(name,brand_db_domain,brand_db_name,brand_db_user,brand_db_password,brand_db_port)VALUES(?,?,?,?,?,?)",
								new Object[] { name, jsonData.getString("db_domain"), jsonData.getString("db_name"),
										jsonData.getString("db_user"), jsonData.getString("db_password"),
										jsonData.getInt("db_port") });*/
					} else {
						return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN)
								.body("The database already exist");
					}
				}
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN)
						.body("Duplicate brand name found");
			}

			if (rowAffected == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create new database");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Server error while creating database. Please try again later.");
		}
		return ResponseEntity.ok(null);
	}

	@PostMapping(value = "/edit")
	public ResponseEntity<?> editBrand(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			JSONObject jsonData = new JSONObject(data);
			String name = jsonData.getString("name");
			Long id = jsonData.getLong("id");
			int rowAffected = 0;

			if (checkDuplicateDbName(name, id).isEmpty()) {
				rowAffected = jdbcTemplate.update(
						"UPDATE brands SET name = ?,brand_db_domain = ?,brand_db_name = ?,brand_db_user = ?,brand_db_password = ?,brand_db_port = ? WHERE id = ?",
						new Object[] { name, jsonData.getString("db_domain"), jsonData.getString("db_name"),
								jsonData.getString("db_username"), jsonData.getString("db_password"),
								jsonData.getInt("db_port"), id });
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN)
						.body("Duplicate brand name found");
			}
			if (rowAffected == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot update brand record");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot update the brand record. Please try again later");
		}
		return ResponseEntity.ok(null);
	}

	private String checkDuplicateDbName(String name) {
		String SELECT_NAME_QUERY = "SELECT DISTINCT name FROM brands WHERE LOWER(name) LIKE LOWER(?)";
		try {
			return jdbcTemplate.queryForObject(SELECT_NAME_QUERY, new Object[] { name }, String.class);
		} catch (EmptyResultDataAccessException ex) {
			return "";
		}
	}

	private String checkDuplicateDbName(String name, Long id) {
		String SELECT_NAME_QUERY = "SELECT DISTINCT name FROM brands WHERE LOWER(name) LIKE LOWER(?) AND != ?";
		String resultString = "";

		if (id != null) {
			try {
				resultString = jdbcTemplate.queryForObject(SELECT_NAME_QUERY, new Object[] { name, id },
						String.class);
			} catch (EmptyResultDataAccessException ex) {
			}
		}
		return resultString;
	}
	
	//Brand(s) Display Brand Users Assignment
	@GetMapping(value="/users-in-brand")
	public ResponseEntity<?> usersInBrand(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("brandId") Long brandId) {
		JSONArray jsonArray = new JSONArray();	
		try {
			//Exluce Super Admin
			List<Map<String, Object>> usersInBrand = jdbcTemplate.queryForList("SELECT u.id, u.username, u.name, u.email, " + 
					"CASE WHEN (SELECT COUNT(*) " + 
					"FROM users_brands ub " + 
					"WHERE ub.user_id = u.id " + 
					"AND " + 
					"ub.brand_id = ?) > 0 " + 
					"THEN CAST (1 AS BIT) " + 
					"ELSE CAST (0 AS BIT) END as exist " + 
					"FROM users u WHERE u.id != 1", new Object[] {brandId});
			
			if(!usersInBrand.isEmpty()) {
				jsonArray = new JSONArray(usersInBrand);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
					.body("Cannot retrieve users. Please try again later");
		}
		return ResponseEntity.ok(jsonArray.toString());
	}
	
	@PostMapping(value="/assign-users-to-brand")
	public ResponseEntity<?> assignUsersToBrand(@RequestBody String data, HttpServletRequest request, HttpServletResponse response){
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		
		try {
			JSONObject jsonObject = new JSONObject(data);
			Long brandId = jsonObject.getLong("brandId");
			JSONArray userArray = jsonObject.optJSONArray("users");
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM users_brands WHERE brand_id = ?");
			stmt.setLong(1,brandId);
			stmt.executeUpdate();

			if(userArray.length()!= 0) {
				connection.setAutoCommit(false);
				
				String insertionSql = "INSERT INTO users_brands(brand_id,user_id,permission) VALUES (?,?,'0')";

				for(int i = 0; i < userArray.length(); i++) {
					JSONObject jsonUserObj = userArray.getJSONObject(i);
						stmt2 = connection.prepareStatement(insertionSql);
						stmt2.setLong(1, brandId);
						stmt2.setLong(2, jsonUserObj.getLong("id"));
						stmt2.executeUpdate();
						
						connection.commit();
				}
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
					.body("Cannot assign user to brand. Please try again later");
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
