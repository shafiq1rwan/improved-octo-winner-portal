package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.entity.GroupCategory;
import com.managepay.admin.byod.util.ByodUtil;

@RestController
@RequestMapping("/menu/modifier_group")
public class ModifierGroupRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ByodUtil byodUtil;
	
	@GetMapping(value = "/get_all_modifier_group", produces = "application/json")
	public ResponseEntity<?> getAllModifierGroup(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonModifierGroupArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM modifier_group");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonModifierGroupObj = new JSONObject();
				jsonModifierGroupObj.put("id", rs.getLong("id"));
				jsonModifierGroupObj.put("modifier_group_name", rs.getString("modifier_group_name"));
				jsonModifierGroupObj.put("is_active", rs.getBoolean("is_active"));
				jsonModifierGroupObj.put("created_date", rs.getDate("created_date"));

				jsonModifierGroupArray.put(jsonModifierGroupObj);
			}

			return ResponseEntity.ok().body(jsonModifierGroupArray.toString());
		} catch (Exception ex) {
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

	@GetMapping(value = "/get_modifier_group_by_id", produces = "application/json")
	public ResponseEntity<?> getModifierGroupById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {

		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM modifier_group WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("modifier_group_name", rs.getString("modifier_group_name"));
				jsonResult.put("is_active", rs.getBoolean("is_active"));
				jsonResult.put("created_date", rs.getDate("created_date"));
			} else {
				return ResponseEntity.notFound().build();
			}

			return ResponseEntity.ok().body(jsonResult.toString());
		} catch (Exception ex) {
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

	@PostMapping(value = "/create_modifier_group", produces = "application/json")
	public ResponseEntity<?> createModifierGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonModifierGroupData = new JSONObject(data);
			boolean isActive = jsonModifierGroupData.isNull("is_active") ? false
					: jsonModifierGroupData.getBoolean("is_active");

			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(
					"INSERT INTO modifier_group(modifier_group_name, is_active) VALUES (?,?)");
			stmt.setString(1, jsonModifierGroupData.getString("modifier_group_name"));
			stmt.setBoolean(2, isActive);
			int rowAffected = stmt.executeUpdate();

			if (rowAffected == 0) {
				return ResponseEntity.badRequest().body("Failed To Create Modifier Group");
			}

			return ResponseEntity.ok().body(null);
		} catch (DuplicateKeyException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception ex) {
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

	@PostMapping(value = "/edit_modifier_group", produces = "application/json")
	public ResponseEntity<?> editModifierGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonModifierGroupData = new JSONObject(data);		
			boolean isActive = jsonModifierGroupData.isNull("is_active") ? false
					: jsonModifierGroupData.getBoolean("is_active");

			connection = dataSource.getConnection();
			stmt = connection
					.prepareStatement("UPDATE modifier_group SET modifier_group_name = ?, is_active = ? WHERE id = ?");
			stmt.setString(1, jsonModifierGroupData.getString("modifier_group_name"));
			stmt.setBoolean(2, isActive);
			stmt.setLong(3, jsonModifierGroupData.getLong("id"));
			int rowAffected = stmt.executeUpdate();

			if (rowAffected == 0) {
				return ResponseEntity.badRequest().body("Failed To Edit Modifer Group");
			}

			return ResponseEntity.ok().body(null);
		} catch (DuplicateKeyException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception ex) {
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

	@DeleteMapping(value = "/delete_modifier_group", produces = "application/json")
	public ResponseEntity<?> removeModifierGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {

		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM modifier_group WHERE id = ?");
			stmt.setLong(1, id);
			int rowAffected = stmt.executeUpdate();

			if (rowAffected == 0) {
				return ResponseEntity.badRequest().body("Failed To Remove Modifer Group");
			} else {
				// Update Menu Item (Modifier Item)
				stmt2 = connection
						.prepareStatement("UPDATE menu_item SET modifier_group_id = 0 WHERE modifier_group_id = ?");
				stmt2.setLong(1, id);
				stmt2.executeUpdate();

				// Delete from menu_item_modifier_group
				stmt3 = connection.prepareStatement("DELETE FROM menu_item_modifier_group WHERE modifier_group_id = ?");
				stmt3.setLong(1, id);
				stmt3.executeUpdate();
			}
			return ResponseEntity.ok().body(null);
		} catch (Exception ex) {
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
	
	@GetMapping(value = "/get_assigned_menu_item_list", produces = "application/json")
	public ResponseEntity<?> getAssignedMenuItemList(@RequestParam("modifier_group_id") Long modifierGroupId, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {		
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item a " + 
					"INNER JOIN menu_item_type_lookup b ON a.menu_item_type = b.menu_item_type_number " + 
					"INNER JOIN modifier_item_sequence c ON a.id = c.menu_item_id " + 
					"WHERE c.modifier_group_id = ? ");
			stmt.setLong(1, modifierGroupId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_image_path", rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));			
				jsonMenuItemArray.put(jsonMenuItemObj);
			}
			
			System.out.println("Existing Set " + jsonMenuItemArray.toString());
			
			return ResponseEntity.ok().body(jsonMenuItemArray.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(null);
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
	
	@PostMapping("/assign_menu_items")
	public ResponseEntity<?> assignMenuItems(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonItemsArray = jsonObj.getJSONArray("item_list");
			Long modifierGroupId = jsonObj.getLong("modifier_group_id");
			
			connection = dataSource.getConnection();

			for(int i=0;i<jsonItemsArray.length();i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);		
				stmt = connection.prepareStatement("INSERT INTO modifier_item_sequence (modifier_group_id, menu_item_id, modifier_item_sequence) VALUES (?,?,?)");
				stmt.setLong(1, modifierGroupId);
				stmt.setLong(2, jsonItemObj.getLong("id"));
				stmt.setInt(3, index+1);
				stmt.executeUpdate();
			}
			
			return ResponseEntity.ok().body(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(null);
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
	
	@PostMapping("/reassign_menu_items")
	public ResponseEntity<?> reassignMenuItems(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;

		try {			
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonItemsArray = jsonObj.getJSONArray("item_list");
			Long modifierGroupId = jsonObj.getLong("modifier_group_id");
			
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("DELETE FROM modifier_item_sequence WHERE modifier_group_id = ?");
			stmt.setLong(1, modifierGroupId);
			stmt.executeUpdate();

			for(int i=0;i<jsonItemsArray.length();i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
				stmt2 = connection.prepareStatement("INSERT INTO modifier_item_sequence (modifier_group_id, menu_item_id, modifier_item_sequence) VALUES (?,?,?)");
				stmt2.setLong(1, modifierGroupId);
				stmt2.setLong(2, jsonItemObj.getLong("id"));
				stmt2.setInt(3, index+1);			
				stmt2.executeUpdate();
			}
			
			return ResponseEntity.ok().body(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error at Reassign :" + ex.getMessage());
			return ResponseEntity.badRequest().body(null);
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