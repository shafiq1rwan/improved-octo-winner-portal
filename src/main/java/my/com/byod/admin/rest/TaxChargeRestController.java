package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/menu/charge")
public class TaxChargeRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@Autowired
	private GroupCategoryRestController groupCategoryRestController;
	
	@GetMapping(value = "/getAllCharge", produces = "application/json")
	public ResponseEntity<?> getAllCharge(@RequestParam("group_category_id") Long groupCategoryId, HttpServletRequest request, HttpServletResponse response){
		JSONArray jsonTaxChargeArray = new JSONArray();
		Connection connection = null;	
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonTaxChargeArray = getTaxChargeByGroupCategoryId(connection, groupCategoryId);
			return ResponseEntity.ok(jsonTaxChargeArray.toString());
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
	
	@GetMapping(value = "/getChargeById", produces = "application/json")
	public ResponseEntity<?> getAllChargeById(@RequestParam("id") Long id, HttpServletRequest request, HttpServletResponse response){
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonTaxChargeObj = new JSONObject();
		
		try {
			String query = "SELECT * FROM tax_charge WHERE id = ? ";
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(query);
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				jsonTaxChargeObj.put("id", rs.getLong("id"));				
				jsonTaxChargeObj.put("tax_charge_name", rs.getString("tax_charge_name"));
				jsonTaxChargeObj.put("rate", rs.getInt("rate"));		
				jsonTaxChargeObj.put("charge_type", rs.getInt("charge_type"));
				jsonTaxChargeObj.put("is_active", rs.getBoolean("is_active"));
				jsonTaxChargeObj.put("created_date", rs.getDate("created_date"));
			}
			
			return ResponseEntity.ok(jsonTaxChargeObj.toString());
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
	
	@GetMapping(value = "/getAllChargeType", produces = "application/json")
	public ResponseEntity<?> getAllChargeType(HttpServletRequest request, HttpServletResponse response){
		JSONArray jsonChargeTypeArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM charge_type_lookup");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonChargeTypeObj = new JSONObject();
				jsonChargeTypeObj.put("id", rs.getInt("charge_type_number"));				
				jsonChargeTypeObj.put("tax_name", rs.getString("charge_type_name"));							
				jsonChargeTypeArray.put(jsonChargeTypeObj);
			}
			
			return ResponseEntity.ok(jsonChargeTypeArray.toString());
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
	
	@PostMapping(value = "/createTaxCharge", produces = "application/json")
	public ResponseEntity<?> createTaxCharge(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			JSONObject jsonTaxChargeData = new JSONObject(data);
			connection = dbConnectionUtil.retrieveConnection(request);
			String sqlStatement = "INSERT INTO tax_charge(tax_charge_name, rate, charge_type, is_active, created_date) VALUES (?,?,?,?,NOW());";
			stmt = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, jsonTaxChargeData.getString("tax_charge_name"));
			stmt.setInt(2, jsonTaxChargeData.getInt("rate"));
			stmt.setInt(3, jsonTaxChargeData.getInt("charge_type"));
			stmt.setBoolean(4, jsonTaxChargeData.getBoolean("is_active"));
			rs = stmt.executeQuery();
			if(rs.next()) {
				Long id = rs.getLong(1);
				
				// logging to file	
				String [] parameters = {
						String.valueOf(id),
						jsonTaxChargeData.getString("tax_charge_name")==null?"null":"'"+jsonTaxChargeData.getString("tax_charge_name")+"'",
						String.valueOf(jsonTaxChargeData.getInt("rate")),
						String.valueOf(jsonTaxChargeData.getInt("charge_type")),
						String.valueOf(jsonTaxChargeData.getBoolean("is_active")?1:0)};	
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, jsonTaxChargeData.getLong("group_category_id"), null, 0, "tax_charge");	
				
				sqlStatement = "INSERT INTO group_category_tax_charge (group_category_id, tax_charge_id) VALUES (?,?);";
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, jsonTaxChargeData.getLong("group_category_id"));
				stmt.setLong(2, id);
				stmt.executeUpdate();
				
				/*// logging to file	
				String [] parameters2 = {
						String.valueOf(jsonTaxChargeData.getLong("group_category_id")),
						String.valueOf(id)};	
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters2, jsonTaxChargeData.getLong("group_category_id"), null, 0, null);	*/
			}
				
			return ResponseEntity.ok(null);
		}catch (DuplicateKeyException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		catch(Exception ex) {
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
	
	@PostMapping(value = "/editTaxCharge", produces = "application/json")
	public ResponseEntity<?> editTaxCharge(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			JSONObject jsonTaxChargeData = new JSONObject(data);
			connection = dbConnectionUtil.retrieveConnection(request);
			String sqlStatement = "UPDATE tax_charge SET tax_charge_name = ?, rate = ?, charge_type = ?, is_active = ? WHERE id = ?;";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(1, jsonTaxChargeData.getString("tax_charge_name"));
			stmt.setInt(2, jsonTaxChargeData.getInt("rate"));
			stmt.setInt(3, jsonTaxChargeData.getInt("charge_type"));
			stmt.setBoolean(4, jsonTaxChargeData.getBoolean("is_active"));
			stmt.setLong(5, jsonTaxChargeData.getLong("id"));
			stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					jsonTaxChargeData.getString("tax_charge_name")==null?"null":"'"+jsonTaxChargeData.getString("tax_charge_name")+"'",
					String.valueOf(jsonTaxChargeData.getInt("rate")),
					String.valueOf(jsonTaxChargeData.getInt("charge_type")),
					String.valueOf(jsonTaxChargeData.getBoolean("is_active")?1:0),
					String.valueOf(jsonTaxChargeData.getLong("id"))};	
			groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, jsonTaxChargeData.getLong("group_category_id"), null, 0, null);		
			
			return ResponseEntity.ok(null);
		}catch (DuplicateKeyException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		catch(Exception ex) {
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
	
	public JSONArray getTaxChargeByGroupCategoryId(Connection connection, Long groupCategoryId) throws Exception {
		JSONArray jsonTaxChargeArray = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String query = "SELECT a.* FROM tax_charge a "
					+ "INNER JOIN group_category_tax_charge b ON a.id = b.tax_charge_id "
					+ "WHERE b.group_category_id = ? ";
			
			stmt = connection.prepareStatement(query);
			stmt.setLong(1, groupCategoryId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonTaxChargeObj = new JSONObject();
				jsonTaxChargeObj.put("id", rs.getLong("id"));				
				jsonTaxChargeObj.put("tax_charge_name", rs.getString("tax_charge_name"));
				jsonTaxChargeObj.put("rate", rs.getInt("rate"));		
				jsonTaxChargeObj.put("charge_type", rs.getInt("charge_type"));
				jsonTaxChargeObj.put("is_active", rs.getBoolean("is_active"));
				jsonTaxChargeObj.put("created_date", rs.getDate("created_date"));
								
				jsonTaxChargeArray.put(jsonTaxChargeObj);
			}
			
		} catch(Exception ex) {
			throw ex;
		} finally {
			if(stmt!=null)
				stmt.close();
			if (rs!=null)
				rs.close();
		}
		return jsonTaxChargeArray;
	}
	
}
