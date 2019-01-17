package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import my.com.byod.admin.entity.GroupCategory;
import my.com.byod.admin.util.ByodUtil;

@RestController
@RequestMapping("/menu/modifier_group")
public class ModifierGroupRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private GroupCategoryRestController groupCategoryRestController;

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

/*	@GetMapping(value = "/get_assigne_modifier_groups_by_item_id", produces = "application/json")
	public ResponseEntity<?> getAssignedModifierGroupsByItemId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("menuItemId") Long menuItemId) {

		JSONArray jsonAssignedModifierGroupArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM ("
					+ "SELECT b.menu_item_modifier_group_sequence AS sequence_id, a.* FROM modifier_group a "
					+ "INNER JOIN menu_item_modifier_group b ON a.id = b.modifier_group_id WHERE b.menu_item_id = ? "
					+ "UNION " + "SELECT 9999 AS sequence_id, a.* FROM modifier_group a "
					+ "LEFT JOIN menu_item_modifier_group b ON a.id = b.modifier_group_id "
					+ "WHERE b.modifier_group_id IS NULL " + ") AS A " + "ORDER BY sequence_id");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("sequence_id", rs.getInt("sequence_id"));
				jsonObj.put("modifier_group_name", rs.getString("modifier_group_name"));
				jsonObj.put("is_active", rs.getBoolean("is_active"));

				jsonAssignedModifierGroupArray.put(jsonObj);
			}

			System.out.println(jsonAssignedModifierGroupArray.toString());

			return ResponseEntity.ok().body(jsonAssignedModifierGroupArray.toString());
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

	}*/

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
			String sqlStatement = "INSERT INTO modifier_group(modifier_group_name, is_active) VALUES (?, ?);";
			stmt = connection
					.prepareStatement(sqlStatement);
			stmt.setString(1, jsonModifierGroupData.getString("modifier_group_name"));
			stmt.setBoolean(2, isActive);
			int rowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					jsonModifierGroupData.getString("modifier_group_name"),
					String.valueOf(isActive)};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);
			
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
			String sqlStatement = "UPDATE modifier_group SET modifier_group_name = ?, is_active = ? WHERE id = ?;";
			stmt = connection
					.prepareStatement(sqlStatement);
			stmt.setString(1, jsonModifierGroupData.getString("modifier_group_name"));
			stmt.setBoolean(2, isActive);
			stmt.setLong(3, jsonModifierGroupData.getLong("id"));
			int rowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					jsonModifierGroupData.getString("modifier_group_name"),
					String.valueOf(isActive),
					String.valueOf(jsonModifierGroupData.getLong("id"))};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);
			
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
			String sqlStatement = "DELETE FROM modifier_group WHERE id = ?;";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, id);
			int rowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(id)};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);

			if (rowAffected == 0) {
				return ResponseEntity.badRequest().body("Failed To Remove Modifer Group");
			} else {
				// Delete from menu_item_modifier_group
				sqlStatement = "DELETE FROM menu_item_modifier_group WHERE modifier_group_id = ?;";
				stmt3 = connection.prepareStatement(sqlStatement);
				stmt3.setLong(1, id);
				stmt3.executeUpdate();
				
				// logging to file	
				String [] parameters2 = {
						String.valueOf(id)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters2, null, 0);
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
	public ResponseEntity<?> getAssignedMenuItemList(@RequestParam("modifier_group_id") Long modifierGroupId,
			HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item a "
					+ "INNER JOIN menu_item_type_lookup b ON a.menu_item_type = b.menu_item_type_number "
					+ "INNER JOIN modifier_item_sequence c ON a.id = c.menu_item_id "
					+ "WHERE c.modifier_group_id = ? ");
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
			String sqlStatement = "INSERT INTO modifier_item_sequence (modifier_group_id, menu_item_id, modifier_item_sequence) VALUES (?, ?, ?);";

			for (int i = 0; i < jsonItemsArray.length(); i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, modifierGroupId);
				stmt.setLong(2, jsonItemObj.getLong("id"));
				stmt.setInt(3, index + 1);
				stmt.executeUpdate();
				
				// logging to file	
				String [] parameters = {
						String.valueOf(modifierGroupId),
						String.valueOf(jsonItemObj.getLong("id")),
						String.valueOf(index + 1)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);
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
			String sqlStatement = "DELETE FROM modifier_item_sequence WHERE modifier_group_id = ?;";
			
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, modifierGroupId);
			stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(modifierGroupId)};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);

			for (int i = 0; i < jsonItemsArray.length(); i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
				sqlStatement = "INSERT INTO modifier_item_sequence (modifier_group_id, menu_item_id, modifier_item_sequence) VALUES (?, ?, ?);";
				stmt2 = connection.prepareStatement(sqlStatement);
				stmt2.setLong(1, modifierGroupId);
				stmt2.setLong(2, jsonItemObj.getLong("id"));
				stmt2.setInt(3, index + 1);
				stmt2.executeUpdate();
				
				// logging to file	
				String [] parameters2 = {
						String.valueOf(modifierGroupId),
						String.valueOf(jsonItemObj.getLong("id")),
						String.valueOf(index + 1)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters2, null, 0);
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

	@GetMapping("/get_assigned_modifier_group_by_menu_item_id")
	public ResponseEntity<?> getAssignedModifierGroupByMenuItemId(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("menuItemId") Long menuItemId) {
		JSONArray jsonAssignedModifierArray = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			jsonAssignedModifierArray = new JSONArray();
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement(
					"SELECT mg.*, mimg.menu_item_modifier_group_sequence FROM modifier_group mg INNER JOIN menu_item_modifier_group mimg On mg.id = mimg.modifier_group_id WHERE mimg.menu_item_id = ? ORDER BY mimg.menu_item_modifier_group_sequence");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonModifierGroupObj = new JSONObject();
				jsonModifierGroupObj.put("sequence", rs.getInt("menu_item_modifier_group_sequence"));
				jsonModifierGroupObj.put("name", rs.getString("modifier_group_name"));
				jsonModifierGroupObj.put("id", rs.getLong("id"));

				jsonAssignedModifierArray.put(jsonModifierGroupObj);
			}
			return ResponseEntity.ok().body(jsonAssignedModifierArray.toString());
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

	// Required Testing
	@GetMapping("/get_unassigned_modifier_groups")
	public ResponseEntity<?> getUnassignedModifierGroups(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("menuItemId") Long menuItemId) {
		JSONArray jsonUnassignedModifierArray = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			System.out.println("Hello From Unassigned " + menuItemId);

			jsonUnassignedModifierArray = new JSONArray();
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement(
					"SELECT id, modifier_group_name FROM modifier_group "
					+ "WHERE id NOT IN(SELECT mimg.modifier_group_id FROM menu_item_modifier_group mimg "
					+ "INNER JOIN modifier_group mg ON mg.id = mimg.modifier_group_id "
					+ "WHERE  mimg.menu_item_id = ?)");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonModifierGroupObj = new JSONObject();
				jsonModifierGroupObj.put("name", rs.getString("modifier_group_name"));
				jsonModifierGroupObj.put("id", rs.getLong("id"));
				jsonUnassignedModifierArray.put(jsonModifierGroupObj);
			}
			return ResponseEntity.ok().body(jsonUnassignedModifierArray.toString());
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

	@PostMapping("/assign_modifier_group")
	public ResponseEntity<?> assignModifierGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonModifierGroupsArray = jsonObj.getJSONArray("modifier_group_list");
			Long menuItemId = jsonObj.getLong("menu_item_id");

			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String sqlStatement = "INSERT INTO menu_item_modifier_group (menu_item_id, modifier_group_id, menu_item_modifier_group_sequence) VALUES (?, ?, ?);";

			for (int i = 0; i < jsonModifierGroupsArray.length(); i++) {
				int index = i + 1;
				JSONObject jsonModifierGroupsObj = jsonModifierGroupsArray.getJSONObject(i);
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, menuItemId);
				stmt.setLong(2, jsonModifierGroupsObj.getLong("id"));
				stmt.setInt(3, index);
				stmt.executeUpdate();
				
				// logging to file	
				String [] parameters = {
						String.valueOf(menuItemId),
						String.valueOf(jsonModifierGroupsObj.getLong("id")),
						String.valueOf(index)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);
				
				connection.commit();
			}

			return ResponseEntity.ok().body(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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

	@DeleteMapping(value = "/delete_assigned_modifier", produces = "application/json")
	public ResponseEntity<?> deleteAssignedModifier(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("menuItemId") Long menuItemId, @RequestParam("modifierGroupId") Long modifierGroupId) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {

			connection = dataSource.getConnection();
			String sqlStatement = "DELETE FROM menu_item_modifier_group WHERE menu_item_id = ? AND modifier_group_id = ?;";
			stmt = connection.prepareStatement(
					"DELETE FROM menu_item_modifier_group WHERE menu_item_id = ? AND modifier_group_id = ?");
			stmt.setLong(1, menuItemId);
			stmt.setLong(2, modifierGroupId);
			int rowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(menuItemId),
					String.valueOf(modifierGroupId)};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);
			
			if (rowAffected == 0) {
				return ResponseEntity.badRequest().body("Failed To Remove Assigned Modifer");
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

	@PostMapping("/reorder_assigned_modifier_group")
	public ResponseEntity<?> reorderAssignedModifierGroup(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;

		try {
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonModifierGroupsArray = jsonObj.getJSONArray("modifier_group_list");
			Long menuItemId = jsonObj.getLong("menu_item_id");

			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			String sqlStatement = "DELETE FROM menu_item_modifier_group WHERE menu_item_id = ?;";
			
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, menuItemId);
			stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(menuItemId)};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0);

			for (int i = 0; i < jsonModifierGroupsArray.length(); i++) {
				int index = i;
				JSONObject jsonModifierGroupsObj = jsonModifierGroupsArray.getJSONObject(i);
				sqlStatement = "INSERT INTO menu_item_modifier_group (menu_item_id, modifier_group_id, menu_item_modifier_group_sequence) VALUES (? ,? ,?);";
				stmt2 = connection.prepareStatement(sqlStatement);
				stmt2.setLong(1, menuItemId);
				stmt2.setLong(2, jsonModifierGroupsObj.getLong("id"));
				stmt2.setInt(3, index + 1);
				stmt2.executeUpdate();
				
				// logging to file	
				String [] parameters2 = {
						String.valueOf(menuItemId),
						String.valueOf(jsonModifierGroupsObj.getLong("id")),
						String.valueOf(index + 1)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters2, null, 0);			
				
				connection.commit();
			}

			return ResponseEntity.ok().body(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
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
