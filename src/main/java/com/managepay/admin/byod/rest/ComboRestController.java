package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
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
				stmt = connection.prepareStatement("INSERT INTO combo_detail(menu_item_id, combo_detail_name, combo_detail_quantity, combo_detail_sequence) VALUES (?,?,?,?)");
				stmt.setLong(1, jsonComboDetailObj.getLong("menu_item_id"));
				stmt.setString(2, jsonComboDetailObj.getString("combo_detail_name"));
				stmt.setInt(3, jsonComboDetailObj.getInt("combo_detail_quantity"));
				stmt.setInt(4, jsonComboDetailObj.getInt("combo_detail_sequence"));
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
			stmt = connection.prepareStatement("SELECT * FROM combo_detail WHERE menu_item_id = ? ORDER BY combo_detail_sequence");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonComboDetailObj = new JSONObject();
				jsonComboDetailObj.put("id", rs.getLong("id"));				
				jsonComboDetailObj.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonComboDetailObj.put("name", rs.getString("combo_detail_name"));
				jsonComboDetailObj.put("quantity", rs.getInt("combo_detail_quantity"));
				jsonComboDetailObj.put("order", rs.getInt("combo_detail_sequence"));
								
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
			JSONObject jsonComboDetailObj = new JSONObject(data);
			
			if(!jsonComboDetailObj.isNull("menu_item_id")|| !jsonComboDetailObj.isNull("oldTierItems") || 
					!jsonComboDetailObj.isNull("newTierItems")) {
				
				JSONArray oldJsonComboDetailArray = jsonComboDetailObj.getJSONArray("oldTierItems");
				JSONArray newJsonComboDetailArray = jsonComboDetailObj.getJSONArray("newTierItems");
				Long menuItemId = jsonComboDetailObj.getLong("menu_item_id");
				
				HashSet<Long> deleteComboDetailSet = new HashSet<>();			
				connection = dataSource.getConnection();
				
				for(int i=0;i<newJsonComboDetailArray.length();i++) {
					JSONObject newJsonComboDetailObj = newJsonComboDetailArray.getJSONObject(i);
					Long id = newJsonComboDetailObj.isNull("id")?0:newJsonComboDetailObj.getLong("id");
					
					//new 
					if(newJsonComboDetailObj.isNull("id")) {
						stmt = connection.prepareStatement("INSERT INTO combo_detail(menu_item_id, combo_detail_name, combo_detail_quantity, combo_detail_sequence) VALUES (?,?,?,?)");
						stmt.setLong(1, menuItemId);
						stmt.setString(2, newJsonComboDetailObj.getString("name"));
						stmt.setInt(3, newJsonComboDetailObj.getInt("quantity"));
						stmt.setInt(4, newJsonComboDetailObj.getInt("order"));
						stmt.executeUpdate();
						
						for(int j=0; j<oldJsonComboDetailArray.length(); j++) {
							JSONObject oldJsonComboDetailObj = oldJsonComboDetailArray.getJSONObject(j); 
							if(id == oldJsonComboDetailObj.getLong("id"))
							{					
							
							} else {
								deleteComboDetailSet.add(oldJsonComboDetailObj.getLong("id"));
							}
						}
						
						
						
					} else {
						stmt = connection.prepareStatement("UPDATE combo_detail SET combo_detail_name = ?, combo_detail_quantity = ?, combo_detail_sequence = ? WHERE id = ?");
						stmt.setString(1, newJsonComboDetailObj.getString("name"));
						stmt.setInt(2, newJsonComboDetailObj.getInt("quantity"));
						stmt.setInt(3, newJsonComboDetailObj.getInt("order"));
						stmt.setLong(4, newJsonComboDetailObj.getLong("id"));
						stmt.executeUpdate();
						
						
						for(int j=0; j<oldJsonComboDetailArray.length(); j++) {
							JSONObject oldJsonComboDetailObj = oldJsonComboDetailArray.getJSONObject(j); 
							if(id == oldJsonComboDetailObj.getLong("id"))
							{					
							
							} else {
								deleteComboDetailSet.add(oldJsonComboDetailObj.getLong("id"));
							}
						}
						
					}
					

				}

				System.out.println(deleteComboDetailSet.toString());		
				for(Long id:deleteComboDetailSet) {		
					System.out.println(id);	
					stmt = connection.prepareStatement("DELETE FROM combo_detail WHERE id = ?");
					stmt.setLong(1, id);
					stmt.executeUpdate();
				}
			}
			else {
				return ResponseEntity.notFound().build();
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
	
	@GetMapping(value="/getMenuItemAndItemGroupInCombo", produces = "application/json")
	public ResponseEntity<?> getMenuItemAndItemGroupInCombo(HttpServletRequest request, HttpServletResponse response, @RequestParam("comboDetailId") Long comboDetailId){
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		
		try {
			List<JSONObject> menuItemAndGroupJsonList = new ArrayList<>();
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT mi.*, cid.id AS combo_item_detail_id, cid.combo_item_detail_sequence FROM menu_item mi INNER JOIN combo_item_detail cid ON mi.id = cid.menu_item_id WHERE cid.combo_detail_id = ? AND mi.menu_item_type = 0");
			stmt.setLong(1, comboDetailId);
			rs = (ResultSet)stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("combo_item_detail_id", rs.getLong("combo_item_detail_id"));
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("type", "item");
				jsonMenuItemObj.put("price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("sequence", rs.getInt("combo_item_detail_sequence"));
				
				menuItemAndGroupJsonList.add(jsonMenuItemObj);
			}
			
			stmt = connection.prepareStatement("SELECT mig.*, cid.id AS combo_item_detail_id, cid.combo_item_detail_sequence FROM menu_item_group mig INNER JOIN combo_item_detail cid ON mig.id = cid.menu_item_group_id WHERE cid.combo_detail_id = ?");
			stmt.setLong(1, comboDetailId);
			rs2 = (ResultSet)stmt.executeQuery();
			
			while(rs2.next()) {
				JSONObject jsonMenuItemGroupObj = new JSONObject();
				jsonMenuItemGroupObj.put("combo_item_detail_id", rs2.getLong("combo_item_detail_id"));
				jsonMenuItemGroupObj.put("id", rs2.getLong("id"));
				jsonMenuItemGroupObj.put("name", rs2.getString("menu_item_group_name"));
				jsonMenuItemGroupObj.put("type", "group");
				jsonMenuItemGroupObj.put("price", null);
				jsonMenuItemGroupObj.put("sequence", rs2.getInt("combo_item_detail_sequence"));
				
				
				stmt = connection.prepareStatement("SELECT mi.* FROM menu_item mi INNER JOIN menu_item_group_menu_item migmi ON mi.id = migmi.menu_item_id WHERE migmi.menu_item_group_id = ? ORDER BY migmi.menu_item_group_menu_item_sequence");
				stmt.setLong(1, rs2.getLong("id"));
				rs3 = (ResultSet)stmt.executeQuery();
				
				JSONArray jsonItemGroupMenuItemArray = new JSONArray();
				
				while(rs3.next()) {
					JSONObject jsonMenuItemObj = new JSONObject();
					jsonMenuItemObj.put("id", rs3.getLong("id"));
					jsonMenuItemObj.put("name", rs3.getString("menu_item_name"));
					jsonMenuItemObj.put("price", rs3.getBigDecimal("menu_item_base_price"));
				
					jsonItemGroupMenuItemArray.put(jsonMenuItemObj);
				}
				
				jsonMenuItemGroupObj.put("menu_items", jsonItemGroupMenuItemArray);

				menuItemAndGroupJsonList.add(jsonMenuItemGroupObj);
			}
			
			Collections.sort(menuItemAndGroupJsonList, (jsonObjectA,jsonObjectB)-> {
				 int compare = 0;
			        try
			        {
			            int keyA = jsonObjectA.getInt("sequence");
			            int keyB = jsonObjectB.getInt("sequence");
			            compare = Integer.compare(keyA, keyB);
			        }
			        catch(JSONException ex)
			        {
			            ex.printStackTrace();
			        }
			        return compare;
			});
			JSONArray jsonMenuItemAndGroupArray = new JSONArray(menuItemAndGroupJsonList);
			return ResponseEntity.ok(jsonMenuItemAndGroupArray.toString());
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
