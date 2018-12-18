package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu/combo")
public class ComboRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PostMapping(value = "/createComboDetail", produces = "application/json")
	public ResponseEntity<?> createComboDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			//JSONObject jsonComboData = new JSONObject(data);
			JSONArray jsonComboDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();
			
			for(int i=0;i<jsonComboDetailArray.length();i++) {
				JSONObject jsonComboDetailObj = jsonComboDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement("INSERT INTO combo_detail(menu_item_id, combo_detail_name, combo_detail_sequence) VALUES (?,?,?)");
				stmt.setLong(1, jsonComboDetailObj.getLong("menu_item_id"));
				stmt.setString(2, jsonComboDetailObj.getString("combo_detail_name"));
				stmt.setInt(3, jsonComboDetailObj.getInt("combo_detail_sequence"));
				stmt.executeUpdate();			
			}
			return ResponseEntity.ok(null);
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
	
	@GetMapping(value = "/getComboDetailByMenuItemId", produces = "application/json")
	public ResponseEntity<?> getComboDetailById(HttpServletRequest request, HttpServletResponse response, @RequestParam("menuItemId") Long menuItemId){
		JSONArray jsonComboDetailArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM combo_detail WHERE menu_item_id = ?");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonComboDetailObj = new JSONObject();
				jsonComboDetailObj.put("id", rs.getLong("id"));				
				jsonComboDetailObj.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonComboDetailObj.put("combo_detail_name", rs.getString("combo_detail_name"));
				jsonComboDetailObj.put("combo_detail_sequence", rs.getInt("combo_detail_sequence"));
								
				jsonComboDetailArray.put(jsonComboDetailObj);
			}
			
			return ResponseEntity.ok(jsonComboDetailArray.toString());
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
	
	@PostMapping(value = "/editComboDetail", produces = "application/json")
	public ResponseEntity<?> editComboDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
			
		try {
			JSONArray jsonComboDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();
			
			for(int i=0;i<jsonComboDetailArray.length();i++) {
				JSONObject jsonComboDetailObj = jsonComboDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement("UPDATE combo_detail SET combo_detail_name = ?, combo_detail_sequence = ? WHERE id = ?");
				stmt.setString(1, jsonComboDetailObj.getString("combo_detail_name"));
				stmt.setInt(2, jsonComboDetailObj.getInt("combo_detail_sequence"));
				stmt.setLong(3, jsonComboDetailObj.getLong("id"));
				stmt.executeUpdate();
			}
			return ResponseEntity.ok(null);
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
	
	//TODO delete combo detail item
	@DeleteMapping(value = "/deleteComboDetail", produces = "application/json")
	public ResponseEntity<?> deleteComboDetail(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id){
		
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM combo_detail WHERE id = ?");
			stmt.setLong(1, id);
			int deletedRow = stmt.executeUpdate();
			
			if(deletedRow == 0) {
				return ResponseEntity.badRequest().body(null);
			}
			else {
				stmt = connection.prepareStatement("DELETE FROM combo_item_detail WHERE combo_detail_id = ?");
				stmt.setLong(1, id);
				stmt.executeUpdate();
				//may change
			}
			
			return ResponseEntity.ok(null);
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
	
	//=============== Combo Item Details =========================
	
	@GetMapping(value = "/getComboItemDetailByComboDetailId", produces = "application/json")
	public ResponseEntity<?> getComboItemDetailByComboDetailId(HttpServletRequest request, HttpServletResponse response, @RequestParam("comboDetailId") Long comboDetailId){
		JSONArray jsonComboItemDetailArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM combo_item_detail WHERE combo_detail_id = ? ORDER BY combo_item_detail_sequence");
			stmt.setLong(1, comboDetailId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonComboItemDetailObj = new JSONObject();
				jsonComboItemDetailObj.put("id", rs.getLong("id"));
				jsonComboItemDetailObj.put("combo_detail_id", rs.getLong("combo_detail_id"));
				jsonComboItemDetailObj.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonComboItemDetailObj.put("menu_item_group_id", rs.getLong("menu_item_group_id"));
				jsonComboItemDetailObj.put("combo_item_detail_sequence", rs.getInt("combo_item_detail_sequence"));
				
				jsonComboItemDetailArray.put(jsonComboItemDetailObj);
			}	
			return ResponseEntity.ok(jsonComboItemDetailArray.toString());
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
	
	@PostMapping(value = "/createComboItemDetail", produces = "application/json")
	public ResponseEntity<?> createComboItemDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			//JSONObject jsonComboItemDetail = new JSONObject(data);
			JSONArray jsonComboItemDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();
			
			for(int i=0;i<jsonComboItemDetailArray.length();i++) {
				JSONObject jsonComboItemDetailObj = jsonComboItemDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement("INSERT INTO combo_item_detail(combo_detail_id, menu_item_id, menu_item_group_id, combo_item_detail_sequence) VALUES (?,?,?,?)");
				stmt.setLong(1, jsonComboItemDetailObj.getLong("combo_detail_id"));
				stmt.setLong(2, jsonComboItemDetailObj.getLong("menu_item_id"));
				stmt.setLong(3, jsonComboItemDetailObj.getLong("menu_item_group_id"));
				stmt.setInt(4, jsonComboItemDetailObj.getInt("combo_item_detail_sequence"));
				stmt.executeUpdate();
			}

			return ResponseEntity.ok(null);
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
	

	
	@PostMapping(value = "/editComboItemDetail", produces = "application/json")
	public ResponseEntity<?> editComboItemDetail(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			//JSONObject jsonComboItemDetail = new JSONObject(data);
			JSONArray jsonComboItemDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();
			
			for(int i=0;i<jsonComboItemDetailArray.length();i++) {
				JSONObject jsonComboItemDetailObj = jsonComboItemDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement("UPDATE combo_item_detail SET menu_item_id = ?, menu_item_group_id = ?, combo_item_detail_sequence = ? WHERE id = ?");
				stmt.setLong(1, jsonComboItemDetailObj.getLong("menu_item_id"));
				stmt.setLong(2, jsonComboItemDetailObj.getLong("menu_item_group_id"));
				stmt.setInt(3, jsonComboItemDetailObj.getInt("combo_item_detail_sequence"));
				stmt.setLong(4, jsonComboItemDetailObj.getLong("id"));
				stmt.executeUpdate();
			}

			return ResponseEntity.ok(null);
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
	
	@DeleteMapping(value = "/deleteComboItemDetail", produces = "application/json")
	public ResponseEntity<?> deleteComboItemDetail(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id){
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM combo_item_detail WHERE id = ?");
			stmt.setLong(1, id);
			int deletedRow = stmt.executeUpdate();		
			
			if(deletedRow == 0) 
				return ResponseEntity.badRequest().body(null);
			
			return ResponseEntity.ok(null);
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

}
