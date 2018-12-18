package com.managepay.admin.byod.rest;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.util.ByodUtil;

@RestController
@RequestMapping("/menu/menuItem")
public class MenuItemRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ByodUtil byodUtil;

	@GetMapping(value = "/getMenuItemType", produces = "application/json")
	public ResponseEntity<?> getMenuItemType(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemTypeArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item_type_lookup");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemTypeObj = new JSONObject();
				jsonMenuItemTypeObj.put("menu_item_type_id", rs.getInt("menu_item_type_number"));
				jsonMenuItemTypeObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));

				jsonMenuItemTypeArray.put(jsonMenuItemTypeObj);
			}
			
			return ResponseEntity.ok().body(jsonMenuItemTypeArray.toString());
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

	@GetMapping(value = "/getAllMenuItem", produces = "application/json")
	public String getAllMenuItem(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("modifier_group_id", rs.getLong("modifier_group_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_description", rs.getString("menu_item_description"));
				jsonMenuItemObj.put("menu_item_image_path", rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type", rs.getInt("menu_item_type"));
				jsonMenuItemObj.put("is_taxable", rs.getBoolean("is_taxable"));
				jsonMenuItemObj.put("is_discountable", rs.getBoolean("is_discountable"));
				jsonMenuItemObj.put("is_active", rs.getBoolean("is_active"));
				jsonMenuItemObj.put("created_date", rs.getDate("created_date"));

				jsonMenuItemArray.put(jsonMenuItemObj);
			}
		} catch (Exception ex) {
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
		return jsonMenuItemArray.toString();
	}

	@GetMapping(value = "/getMenuItemById", produces = "application/json")
	public String getMenuItemById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("backend_id", rs.getString("backend_id"));
				jsonResult.put("modifier_group_id", rs.getLong("modifier_group_id"));
				jsonResult.put("menu_item_name", rs.getString("menu_item_name"));
				jsonResult.put("menu_item_description", rs.getString("menu_item_description"));
				jsonResult.put("menu_item_image_path", rs.getString("menu_item_image_path"));
				jsonResult.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonResult.put("menu_item_type", rs.getInt("menu_item_type"));
				jsonResult.put("is_taxable", rs.getBoolean("is_taxable"));
				jsonResult.put("is_discountable", rs.getBoolean("is_discountable"));
				jsonResult.put("created_date", rs.getDate("created_date"));
			} else {
				response.setStatus(404);
				jsonResult.put("response_message", "Menu Item not found!");
			}
		} catch (Exception ex) {
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
		return jsonResult.toString();
	}

	@PostMapping(value = "/createMenuItem", produces = "application/json")
	public ResponseEntity<?> createMenuItem(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonMenuItemData = new JSONObject(data);
			String imagePath = jsonMenuItemData.isNull("menu_item_image_path")?null:jsonMenuItemData.getString("menu_item_image_path");

					connection = dataSource.getConnection();
					stmt = connection.prepareStatement(
							"INSERT INTO menu_item(backend_id, modifier_group_id, menu_item_name, menu_item_description, menu_item_image_path, menu_item_base_price, menu_item_type,is_taxable, is_discountable) VALUES(?,?,?,?,?,?,?,?,?)");
					stmt.setString(1, byodUtil.createBackendId("MI", 8));
					stmt.setLong(2, jsonMenuItemData.getLong("modifier_group_id"));
					stmt.setString(3, jsonMenuItemData.getString("menu_item_name"));
					stmt.setString(4, jsonMenuItemData.getString("menu_item_description"));
					stmt.setString(5, imagePath);
					stmt.setBigDecimal(6, new BigDecimal(jsonMenuItemData.getDouble("menu_item_base_price")));
					stmt.setInt(7, jsonMenuItemData.getInt("menu_item_type"));
					stmt.setBoolean(8, jsonMenuItemData.getBoolean("is_taxable"));
					stmt.setBoolean(9, jsonMenuItemData.getBoolean("is_discountable"));
					int rowAffected = stmt.executeUpdate();
					
					if(rowAffected == 0) {
						throw new Exception("Cannot Create Menu Item!");
					}
		
					return ResponseEntity.ok(null);
		} catch (DuplicateKeyException ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		catch (Exception ex) {
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

	@PostMapping(value = "/editMenuItem", produces = "application/json")
	public ResponseEntity<?> editMenuItem(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			JSONObject jsonMenuItemData = new JSONObject(data);
			if (jsonMenuItemData.has("menu_item_name") && jsonMenuItemData.has("id")) {
				String imagePath = jsonMenuItemData.isNull("menu_item_image_path")?null:jsonMenuItemData.getString("menu_item_image_path");
				
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement(
							"UPDATE menu_item SET modifier_group_id = ?, menu_item_name = ?, menu_item_description =?, menu_item_image_path = ?, menu_item_base_price = ?, menu_item_type = ?, is_taxable = ? , is_discountable = ? WHERE id = ?");
					stmt.setLong(1, jsonMenuItemData.getLong("modifier_group_id"));
					stmt.setString(2, jsonMenuItemData.getString("menu_item_name"));
					stmt.setString(3, jsonMenuItemData.getString("menu_item_description"));
					stmt.setString(4, imagePath);
					stmt.setBigDecimal(5, new BigDecimal(jsonMenuItemData.getDouble("menu_item_base_price")));
					stmt.setInt(6, jsonMenuItemData.getInt("menu_item_type"));
					stmt.setBoolean(7, jsonMenuItemData.getBoolean("is_taxable"));
					stmt.setBoolean(8, jsonMenuItemData.getBoolean("is_discountable"));
					stmt.setLong(9, jsonMenuItemData.getLong("id"));
					int rowAffected = stmt.executeUpdate();
					
					if(rowAffected == 0) {
						throw new Exception("Cannot Edit Menu Item.");
					}		
					return ResponseEntity.ok(null);
			} else {
				return ResponseEntity.notFound().build();
			}
		} 
		 catch (DuplicateKeyException ex) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		catch (Exception ex) {
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

	// TODO delete associated item from menu_item_group and modifier_group
	@DeleteMapping(value = "/deleteMenuItem", produces = "application/json")
	public ResponseEntity<?> deleteMenuItem(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = dataSource.getConnection();
			//stmt = connection.prepareStatement("DELETE FROM menu_item WHERE id = ?");
			stmt = connection.prepareStatement("UPDATE menu_item SET is_active = 0 WHERE id = ?");
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();

			if (categoryRowAffected == 0) {
				return ResponseEntity.badRequest().body(null);
			}		
			return ResponseEntity.ok(null);	
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

	private int checkDuplicateMenuItemName(String menuItemName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(menu_item_name) WHERE menu_item_name = ?",
					new Object[] { menuItemName }, Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private int checkDuplicateMenuItemNameWithId(String menuItemName, Long id) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(menu_item_name) WHERE menu_item_name = ? AND id = ?",
					new Object[] { menuItemName, id }, Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

}
