package my.com.byod.login.rest;

import java.sql.Connection;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
				jsonResult.put("role", role);
				jsonResult.put("brands", brandArray);
			}
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
			JSONObject jsonData = new JSONObject(data);
			conn = dbConnectionUtil.getConnection(jsonData.getLong("id"));

			if (conn != null) {
				dbConnectionUtil.setBrandId(jsonData.getLong("id"), request);
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
						rowAffected = jdbcTemplate.update(
								"INSERT INTO brands(name,brand_db_domain,brand_db_name,brand_db_user,brand_db_password,brand_db_port)VALUES(?,?,?,?,?,?)",
								new Object[] { name, jsonData.getString("db_domain"), jsonData.getString("db_name"),
										jsonData.getString("db_user"), jsonData.getString("db_password"),
										jsonData.getInt("db_port") });
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
/*	@GetMapping(value="/get_all_")
	public ResponseEntity<?> usersInBrand(@RequestBody String data, HttpServletRequest request,
			HttpServletResponse response, @RequestParam("brandId") Long brandId) {
		JSONObject jsonResult = null;
		
		try {
			List<Map<String, Object>> usersInBrand = jdbcTemplate.queryForList("SELECT u.* FROM users u INNER JOIN users_brand ub ")
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return ResponseEntity.ok(null);
	}*/
	
	

	
	
	

}
