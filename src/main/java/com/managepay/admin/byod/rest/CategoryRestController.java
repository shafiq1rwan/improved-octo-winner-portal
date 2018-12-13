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
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@Autowired
	private ByodUtil byodUtil;
	
	@GetMapping(value = {"/get_all_category"}, produces = "application/json")
	public String getAllCategory(HttpServletRequest request, HttpServletResponse response) {
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
	
	@GetMapping(value= {"/get_category_by_id"}, produces = "application/json")
	public String getCategoryById(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
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
			
			if(rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("group_category_id", rs.getLong("group_category_id"));				
				jsonResult.put("backend_id", rs.getString("backend_id"));
				jsonResult.put("category_name", rs.getString("category_name"));
				jsonResult.put("category_description", rs.getString("category_description"));
				jsonResult.put("category_image_path", rs.getString("category_image_path"));
				jsonResult.put("category_sequence", rs.getInt("category_sequence"));
				jsonResult.put("is_active", rs.getBoolean("is_active"));
				jsonResult.put("created_date", rs.getDate("created_date"));
				
				stmt = connection.prepareStatement("SELECT * FROM menu_item mi INNER JOIN category_menu_item cmi ON mi.id = cmi.category_id WHERE cmi.category_id = ? ORDER BY cmi.category_menu_item_sequence");
				stmt.setLong(1, rs.getLong("id"));
				rs2 = (ResultSet) stmt.executeQuery();
				
				while(rs2.next()) {
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
		
		System.out.println(data);
		
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
					stmt = connection.prepareStatement("INSERT into category(group_category_id, backend_id, category_name, category_description, category_image_path, category_sequence, is_active) VALUES (?,?,?,?,?,?,?, ?)");
					stmt.setLong(1, jsonCategoryData.getLong("group_category_id"));
					stmt.setString(2, byodUtil.createBackendId("C", 8));			
					stmt.setString(3, jsonCategoryData.getString("category_name"));
					stmt.setString(4, jsonCategoryData.getString("category_description"));
					stmt.setString(5, jsonCategoryData.getString("category_image_path"));
					stmt.setInt(6, getCategorySequenceNumber(jsonCategoryData.getLong("group_category_id")) + 1);
					stmt.setBoolean(7, jsonCategoryData.getBoolean("is_active"));
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
					stmt = connection.prepareStatement("UPDATE category SET category_name = ?, category_description = ?, category_image_path =? WHERE id = ?");
					stmt.setString(1, jsonCategoryData.getString("category_name"));
					stmt.setString(2, jsonCategoryData.getString("category_description"));
					stmt.setString(3, jsonCategoryData.getString("category_image_path"));
					stmt.setLong(4, jsonCategoryData.getLong("id"));
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
	
	
	

	
	
	
	
	
	
	
	
	
}
