package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

@RestController
@RequestMapping("/menu/charge")
public class TaxChargeRestController {


	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping(value = "/getAllCharge", produces = "application/json")
	public ResponseEntity<?> getAllCharge(HttpServletRequest request, HttpServletResponse response, @RequestParam("chargeType") int chargeType){
		JSONArray jsonTaxChargeArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String query = "SELECT * FROM tax_charge";
			
			switch(chargeType) {
			case 0:
				break;
			case 1:
				query += " WHERE charge_type = 1";
				break;
			case 2:
				query += " WHERE charge_type = 2";
				break;
			}
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(query);
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
	
	@GetMapping(value = "/getAllChargeType", produces = "application/json")
	public ResponseEntity<?> getAllChargeType(HttpServletRequest request, HttpServletResponse response){
		JSONArray jsonChargeTypeArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM charge_type_lookup");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonChargeTypeObj = new JSONObject();
				jsonChargeTypeObj.put("charge_type_number", rs.getInt("charge_type_number"));				
				jsonChargeTypeObj.put("charge_type_name", rs.getString("charge_type_name"));							
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
		
		try {
			JSONObject jsonTaxChargeData = new JSONObject(data);
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("INSERT INTO tax_charge(tax_charge_name, rate, charge_type, is_active) VALUES (?,?,?,?)");
				stmt.setString(1, jsonTaxChargeData.getString("tax_charge_name"));
				stmt.setInt(2, jsonTaxChargeData.getInt("rate"));
				stmt.setInt(3, jsonTaxChargeData.getInt("charge_type"));
				stmt.setBoolean(4, jsonTaxChargeData.getBoolean("is_active"));
				stmt.executeUpdate();			
			
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
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("UPDATE tax_charge SET tax_charge_name =?, rate =?, charge_type =?, is_active = ? WHERE id = ?");
				stmt.setString(1, jsonTaxChargeData.getString("tax_charge_name"));
				stmt.setInt(2, jsonTaxChargeData.getInt("rate"));
				stmt.setInt(3, jsonTaxChargeData.getInt("charge_type"));
				stmt.setBoolean(4, jsonTaxChargeData.getBoolean("is_active"));
				stmt.setLong(5, jsonTaxChargeData.getLong("id"));
				stmt.executeUpdate();			
			
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
	
/*	@PostMapping(value = "/applyChargeToMenuItem", produces = "application/json")
	public ResponseEntity<?> applyChargeToMenuItem(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){	
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
	
			
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

	}*/
	
	
	
	
	
}
