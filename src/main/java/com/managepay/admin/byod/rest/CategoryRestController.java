package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.util.ByodUtil;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@RestController
@RequestMapping("/menu/category")
public class CategoryRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ByodUtil byodUtil;

	@GetMapping(value = { "/get_all_category" }, produces = "application/json")
	public String getAllCategory(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("SELECT * FROM category");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonCategoryObj = new JSONObject();
				jsonCategoryObj.put("id", rs.getLong("id"));
				jsonCategoryObj.put("group_category_id", rs.getLong("group_category_id"));
				jsonCategoryObj.put("category_name", rs.getString("category_name"));
				jsonCategoryObj.put("category_description", rs.getString("category_description"));
				jsonCategoryObj.put("category_image_path", rs.getString("category_image_path"));
				jsonCategoryObj.put("category_sequence", rs.getInt("category_sequence"));
				jsonCategoryObj.put("is_active", rs.getBoolean("is_active"));
				jsonCategoryObj.put("created_date", rs.getDate("created_date"));

				jsonCategoryArray.put(jsonCategoryObj);
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
		return jsonCategoryArray.toString();
	}

	@GetMapping(value = { "/get_all_category_by_group_category_id" }, produces = "application/json")
	public String getAllCategoryByGroupCategoryId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("group_category_id") Long groupCategoryId) {
		JSONArray jsonCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("SELECT * FROM category WHERE group_category_id = ?");
			stmt.setLong(1, groupCategoryId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonCategoryObj = new JSONObject();
				jsonCategoryObj.put("id", rs.getLong("id"));
				jsonCategoryObj.put("group_category_id", rs.getLong("group_category_id"));
				jsonCategoryObj.put("category_name", rs.getString("category_name"));
				jsonCategoryObj.put("category_description", rs.getString("category_description"));
				jsonCategoryObj.put("category_image_path", rs.getString("category_image_path"));
				jsonCategoryObj.put("category_sequence", rs.getInt("category_sequence"));
				jsonCategoryObj.put("is_active", rs.getBoolean("is_active"));
				jsonCategoryObj.put("created_date", rs.getDate("created_date"));

				jsonCategoryArray.put(jsonCategoryObj);
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
		return jsonCategoryArray.toString();
	}

	@GetMapping(value = { "/get_category_by_id" }, produces = "application/json")
	public String getCategoryById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM category WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("group_category_id", rs.getLong("group_category_id"));
				jsonResult.put("category_name", rs.getString("category_name"));
				jsonResult.put("category_description", rs.getString("category_description"));
				jsonResult.put("category_image_path", rs.getString("category_image_path"));
				jsonResult.put("category_sequence", rs.getInt("category_sequence"));
				jsonResult.put("is_active", rs.getBoolean("is_active"));
				jsonResult.put("created_date", rs.getDate("created_date"));

				stmt = connection.prepareStatement(
						"SELECT * FROM menu_item mi INNER JOIN category_menu_item cmi ON mi.id = cmi.category_id WHERE cmi.category_id = ? ORDER BY cmi.category_menu_item_sequence");
				stmt.setLong(1, rs.getLong("id"));
				rs2 = (ResultSet) stmt.executeQuery();

				while (rs2.next()) {
					JSONObject jsonMenuItemObj = new JSONObject();
					jsonMenuItemObj.put("id", rs2.getLong("id"));
					jsonMenuItemObj.put("backend_id", rs2.getString("backend_id"));
					jsonMenuItemObj.put("modifier_group_id", rs2.getLong("modifier_group_id"));
					jsonMenuItemObj.put("menu_item_name", rs2.getString("menu_item_name"));
					jsonMenuItemObj.put("menu_item_description", rs2.getString("menu_item_description"));
					jsonMenuItemObj.put("menu_item_image_path", rs2.getString("menu_item_image_path"));
					jsonMenuItemObj.put("menu_item_base_price", rs2.getBigDecimal("menu_item_base_price"));
					jsonMenuItemObj.put("menu_item_type", rs2.getInt("menu_item_type"));
					jsonMenuItemObj.put("is_taxable", rs2.getBoolean("is_taxable"));
					jsonMenuItemObj.put("is_discountable", rs2.getBoolean("is_discountable"));
					jsonMenuItemObj.put("is_active", rs2.getBoolean("is_discountable"));
					jsonMenuItemObj.put("created_date", rs.getDate("created_date"));

					jsonArray.put(jsonMenuItemObj);
				}

				jsonResult.put("menu_items", jsonArray);
			} else {
				response.setStatus(404);
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

	// TODO add image path
	@PostMapping("/create_category")
	public String createCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonCategoryData = new JSONObject(data);
			if (jsonCategoryData.has("group_category_id") && jsonCategoryData.has("category_name")) {

				String description = jsonCategoryData.isNull("category_description") ? null
						: jsonCategoryData.getString("category_description");
				String imagePath = jsonCategoryData.isNull("category_image_path") ? null
						: jsonCategoryData.getString("category_image_path");

				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(
						"INSERT into category(group_category_id, category_name, category_description, category_image_path, category_sequence, is_active) VALUES (?,?,?,?,?,?)");
				stmt.setLong(1, jsonCategoryData.getLong("group_category_id"));
				stmt.setString(2, jsonCategoryData.getString("category_name"));
				stmt.setString(3, description);
				stmt.setString(4, imagePath);
				stmt.setInt(5, getCategorySequenceNumber(jsonCategoryData.getLong("group_category_id")) + 1);
				stmt.setBoolean(6, jsonCategoryData.getBoolean("is_active"));
				stmt.executeUpdate();
			} else {
				response.setStatus(404);
				return null;
			}
		} catch (SQLServerException ex) {
			ex.printStackTrace();
			response.setStatus(409);
			try {
				jsonResult.put("response_message", "Duplicate Category Name Found!");
				return jsonResult.toString();
			} catch (JSONException e) {
				e.printStackTrace();
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

	// TODO edit image path
	@PostMapping(value = "/edit_category",produces = "application/json")
	public String editCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonCategoryData = new JSONObject(data);
			if (jsonCategoryData.has("id") && jsonCategoryData.has("category_name")) {
			
				String description = jsonCategoryData.isNull("category_description") ? null
						: jsonCategoryData.getString("category_description");
				String imagePath = jsonCategoryData.isNull("category_image_path") ? null
						: jsonCategoryData.getString("category_image_path");
				
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(
						"UPDATE category SET category_name = ?, category_description = ?, category_image_path =?, is_active = ? WHERE id = ?");
				stmt.setString(1, jsonCategoryData.getString("category_name"));
				stmt.setString(2, description);
				stmt.setString(3, imagePath);
				stmt.setBoolean(4, jsonCategoryData.getBoolean("is_active"));
				stmt.setLong(5, jsonCategoryData.getLong("id"));
				stmt.executeUpdate();
			} else {
				response.setStatus(404);
				return null;
			}
		}
		catch (SQLServerException ex) {
			ex.printStackTrace();
			response.setStatus(409);
			try {
				jsonResult.put("response_message", "Duplicate Category Name Found!");
				return jsonResult.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		catch (Exception ex) {
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

	@DeleteMapping("/delete_category")
	public String deleteCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM category WHERE id = ?");
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();

			if (categoryRowAffected == 0) {
				response.setStatus(400);
				jsonResult.put("response_message", "Category Encountered Error While Perform Deletion!");
				return jsonResult.toString();
			} else {
				stmt = connection.prepareStatement("DELETE FROM category_menu_item WHERE category_id = ?");
				stmt.setLong(1, id);
				stmt.executeUpdate();
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

	private int getCategorySequenceNumber(Long groupCategoryId) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_sequence) WHERE group_category_id = ?",
					new Object[] { groupCategoryId }, Integer.class);
		} catch (Exception ex) {
			return 0;
		}
	}

	private int checkDuplicateCategoryName(String categoryName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_name) WHERE category_name = ?",
					new Object[] { categoryName }, Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private int checkDuplicateCategoryNameWithId(String categoryName, Long id) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_name) WHERE category_name = ? AND id != ?",
					new Object[] { categoryName, id }, Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	private int getCategoryMenuItemSequence(Long categoryId) {
		try {
			return jdbcTemplate.queryForObject("SELECT TOP 1 category_menu_item_sequence FROM category_menu_item WHERE category_id = ? ORDER BY category_menu_item_sequence DESC",
					new Object[] {categoryId}, Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	@PostMapping("/assign_menu_item_to_category")
	public ResponseEntity<?> assignMenuItemToCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonItemsArray = jsonObj.getJSONArray("item_list");
			Long categoryId = jsonObj.getLong("category_id");
			
			connection = dataSource.getConnection();

			for(int i=0;i<jsonItemsArray.length();i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);		
				stmt = connection.prepareStatement("INSERT INTO category_menu_item (category_id, menu_item_id, category_menu_item_sequence) VALUES (?,?,?)");
				stmt.setLong(1, categoryId);
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
	
	@PostMapping("/reassign_menu_item_to_category")
	public ResponseEntity<?> reassignMenuItemToCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;

		try {			
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonItemsArray = jsonObj.getJSONArray("item_list");
			Long categoryId = jsonObj.getLong("category_id");
			
			connection = dataSource.getConnection();

			stmt = connection.prepareStatement("DELETE FROM category_menu_item WHERE category_id = ?");
			stmt.setLong(1, categoryId);
			stmt.executeUpdate();

			for(int i=0;i<jsonItemsArray.length();i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
				stmt2 = connection.prepareStatement("INSERT INTO category_menu_item (category_id, menu_item_id, category_menu_item_sequence) VALUES (?,?,?)");
				stmt2.setLong(1, categoryId);
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
