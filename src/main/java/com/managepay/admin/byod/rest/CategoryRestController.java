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
@RequestMapping("/menu/category")
public class CategoryRestController {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/get_all_category")
	public String createCategory(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();

			 stmt = connection.prepareStatement("SELECT * FROM category");
			 rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("group_category_id", rs.getLong("group_category_id"));
				jsonObj.put("tax_charge_id", rs.getLong("tax_charge_id"));				
				jsonObj.put("backend_id", rs.getString("backend_id"));
				jsonObj.put("category_name", rs.getString("category_name"));
				jsonObj.put("category_description", rs.getString("category_description"));
				jsonObj.put("category_image_path", rs.getString("category_image_path"));
				jsonObj.put("category_sequence", rs.getInt("category_sequence"));
				jsonObj.put("is_active", rs.getBoolean("is_active"));
				jsonObj.put("created_date", rs.getDate("created_date"));
				jsonArray.put(jsonObj);
			}
			
		}catch(Exception ex) {
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
		return 	jsonArray.toString();
	}
	
	//TODO add child item
	@GetMapping("/get_category_by_id")
	public String getCategoryById(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT c.* FROM category c WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();
			 
			if(rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("group_category_id", rs.getLong("group_category_id"));
				jsonResult.put("tax_charge_id", rs.getLong("tax_charge_id"));				
				jsonResult.put("backend_id", rs.getString("backend_id"));
				jsonResult.put("category_name", rs.getString("category_name"));
				jsonResult.put("category_description", rs.getString("category_description"));
				jsonResult.put("category_image_path", rs.getString("category_image_path"));
				jsonResult.put("category_sequence", rs.getInt("category_sequence"));
				jsonResult.put("is_active", rs.getBoolean("is_active"));
				jsonResult.put("created_date", rs.getDate("created_date"));
				
				
				
				
				
				
				
				
				
			} else {
				response.setStatus(404);
			}
		}catch(Exception ex) {
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
		return 	jsonResult.toString();
	}
	
	//TODO add image path
	@PostMapping("/create_category")
	public String createCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			JSONObject jsonCategoryData = new JSONObject(data);
			if(jsonCategoryData.has("group_category_id") && jsonCategoryData.has("category_name")) {
				
				int existingRecord = checkDuplicateCategoryName(jsonCategoryData.getString("category_name"));
				
				if(existingRecord != 0) {
					response.setStatus(409);
					jsonResult.put("response_message", "Duplication Category Name Found!");
				}
				else {
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement("INSERT into category(group_category_id, tax_charge_id, backend_id, category_name, category_description, category_image_path, category_sequence) VALUES (?,?,?,?,?,?,?)");
					stmt.setLong(1, jsonCategoryData.getLong("group_category_id"));
					stmt.setLong(2, jsonCategoryData.getLong("tax_charge_id"));
					stmt.setString(3, ByodUtil.createBackendId("C", 8));			
					stmt.setString(4, jsonCategoryData.getString("category_name"));
					stmt.setString(5, jsonCategoryData.getString("category_description"));
					stmt.setString(6, jsonCategoryData.getString("category_image_path"));
					stmt.setInt(7, getCategorySequenceNumber(jsonCategoryData.getLong("group_category_id")) + 1);
					stmt.executeUpdate();
				}
			}
			else {
				response.setStatus(400);
			}
		}catch(Exception ex) {
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
		return 	jsonResult.toString();
	}
	
	//TODO edit image path
	@PostMapping("/edit_category")
	public String editCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			JSONObject jsonCategoryData = new JSONObject(data);
			if(jsonCategoryData.has("group_category_id") && jsonCategoryData.has("category_name")) {
				
				int existingRecord = checkDuplicateCategoryNameWithId(jsonCategoryData.getString("category_name"), jsonCategoryData.getLong("id"));
				
				if(existingRecord != 0) {
					response.setStatus(409);
					jsonResult.put("response_message", "Duplication Category Name Found!");
				}
				else {
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement("UPDATE category SET group_category_id =? , tax_charge_id =? , category_name = ?, category_description = ?, category_image_path =? WHERE id = ?");
					stmt.setLong(1, jsonCategoryData.getLong("group_category_id"));
					stmt.setLong(2, jsonCategoryData.getLong("tax_charge_id"));		
					stmt.setString(3, jsonCategoryData.getString("category_name"));
					stmt.setString(4, jsonCategoryData.getString("category_description"));
					stmt.setString(5, jsonCategoryData.getString("category_image_path"));
					stmt.setLong(6, jsonCategoryData.getLong("id"));
					stmt.executeUpdate();
				}
			}
			else {
				response.setStatus(400);
			}
		}catch(Exception ex) {
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
		return 	jsonResult.toString();
	}
	
	@DeleteMapping("/delete_category")
	public String deleteCategory(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM category WHERE id = ?");
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();
			
			if(categoryRowAffected == 0) {
				response.setStatus(400);
				jsonResult.put("response_message", "Category Encountered Error While Perform Deletion!");
			} else {
				stmt = connection.prepareStatement("DELETE FROM category_menu_item WHERE category_id = ?");
				stmt.setLong(1, id);
				stmt.executeUpdate();
			}
		}catch(Exception ex) {
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
		return 	jsonResult.toString();
	}

	private int getCategorySequenceNumber(Long groupCategoryId) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_sequence) WHERE group_category_id = ?", new Object[] {groupCategoryId}, Integer.class);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	private int checkDuplicateCategoryName(String categoryName) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_name) WHERE category_name = ?", new Object[] {categoryName}, Integer.class);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	private int checkDuplicateCategoryNameWithId(String categoryName, Long id) {
		try {
			return jdbcTemplate.queryForObject("SELECT COUNT(category_name) WHERE category_name = ? AND id != ?", new Object[] {categoryName, id}, Integer.class);
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	
	
	@GetMapping("/get_menu_item_type")
	public String getCategoryById(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item_type_lookup");
			rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("menu_item_type_id", rs.getInt("menu_item_type_number"));
				jsonObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));

				jsonArray.put(jsonObj);
			}
		}catch(Exception ex) {
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
		return 	jsonArray.toString();
	}
	
	
	
	
	
	
	
	
}
