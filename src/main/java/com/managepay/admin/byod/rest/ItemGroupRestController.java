package com.managepay.admin.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.entity.Store;
import com.managepay.admin.byod.service.StoreService;

@RestController
@RequestMapping("/menu/item_group")
public class ItemGroupRestController {

	@Autowired
	private DataSource dataSource;	
	
	@RequestMapping(value = "/get_all_item_group", method = RequestMethod.GET)
	public String getAllItemGroup() {
		JSONArray JARY = new JSONArray();
		JSONObject jObject = new JSONObject();
		JSONObject jObjectResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM menu_item_group");
			rs = (ResultSet) stmt.executeQuery();
			 
			while(rs.next()) {
				jObject = new JSONObject();
				jObject.put("id", rs.getLong("id"));
				jObject.put("name", rs.getString("staff_name"));
				jObject.put("backend_id", rs.getString("backend_id"));	
				jObject.put("created_date", rs.getString("created_date"));	
				JARY.put(jObject);
			}
			
			jObjectResult = new JSONObject();
			jObjectResult.put("data", JARY);
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return jObjectResult.toString();
	}
	
	@RequestMapping(value = "/save_menu_item_group", method = RequestMethod.POST)
	public String saveItemGroup(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObject = null;
		JSONObject jObjectResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jObject = new JSONObject(formfield);
			if(!jObject.has("backend_id")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Backend ID not found.").toString();
			}
			if(!jObject.has("group_name")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Group name not found.").toString();
			}
			if(!jObject.has("menu_items")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Menu Item not found.").toString();
			}
			//check for duplicate?
			int existing = 0;
			if(existing != 1) {
				
			}else {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("INSERT INTO menu_item_group(backend_id, menu_item_group_name, created_date) "
						+ "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, jObject.getString("backend_id"));
				stmt.setString(2, jObject.getString("group_name"));
				stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
				stmt.executeUpdate();
				
				rs = stmt.getGeneratedKeys();
				if(rs.next()) {
					System.out.print("Key: " + rs.getLong(1));
					JSONArray array = jObject.optJSONArray("menu_items");
					
					int[] items = new int[array.length()];
					
					for(int i = 0; i < array.length(); i++) {
						items[i] = array.optInt(i);
					}
					
					stmt = null;
					int i = 1;
					for(int item_id : items) {
						stmt = connection.prepareStatement("INSERT INTO menu_item_group_menu_item(menu_item_group_id, menu_item_id, menu_item_group_menu_item_sequence) "
								+ "VALUES(?, ?, ?)");
						stmt.setLong(1, rs.getLong(1));
						stmt.setLong(2, item_id);
						stmt.setLong(3, i);
						stmt.executeUpdate();
						i++;
					}
				}
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return jObjectResult.toString();
	}
	
	@RequestMapping(value = "/get_category", method = RequestMethod.GET)
	public String getAllCategory() {
		JSONObject jObject = null;
		JSONArray JARY = new JSONArray();
		JSONObject jObjectResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM category WHERE is_active = ?");
			stmt.setBoolean(1, true);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				jObject = new JSONObject();
				jObject.put("id", rs.getLong("id"));
				jObject.put("name", rs.getString("category_name"));
				jObject.put("backend_id", rs.getString("backend_id"));
				JARY.put(jObject);
			}
			jObjectResult.put("data", JARY);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return jObjectResult.toString();
	}
	
	@RequestMapping(value = "/items_by_category/{id}", method = RequestMethod.GET)
	public String getItemGroupByCategory(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObject = null;
		JSONObject jObjectResult = new JSONObject();
		JSONArray JARY = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		if(id <= 0) {
			response.setStatus(409);
			return jObjectResult.put("response_message", "Category ID not found").toString();
		}
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT mi.* FROM menu_item mi INNER JOIN category_menu_item cmi ON mi.menu_item_id = cmi.id "
					+ "WHERE category_id = ? AND mi.is_active = ?");
			stmt.setLong(1, id);
			stmt.setBoolean(2, true);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				jObject = new JSONObject();
				jObject.put("id", rs.getLong("id"));
				jObject.put("name", rs.getString("menu_item_name"));
				jObject.put("image", rs.getString("menu_item_image_path"));
				jObject.put("backend_id", rs.getString("backend_id"));
				JARY.put(jObject);
			}
			jObjectResult.put("data", JARY);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return jObjectResult.toString();
	}
	
	@RequestMapping(value = "/update_item_group", method = RequestMethod.POST)
	public String updateItemGroup(@RequestBody String formfield, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObject = new JSONObject();
		JSONObject jObjectResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			jObject = new JSONObject(formfield);
			if(!jObject.has("id")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Menu Item Group ID not found.").toString();
			}
			if(!jObject.has("backend_id")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Backend ID not found.").toString();
			}
			if(!jObject.has("group_name")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Group name not found.").toString();
			}
			if(!jObject.has("item_sequence")) {
				response.setStatus(408);
				return jObjectResult.put("response_message", "Item Sequence not found.").toString();
			}
			if(!jObject.has("menu_items")) {
				response.setStatus(409);
				return jObjectResult.put("response_message", "Menu Item not found.").toString();
			}
			
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("UPDATE menu_item_group SET backend_id = ?, menu_item_group_name = ?, menu_item_group_menu_item_sequnce = ? "
					+ "WHERE id = ?; SELECT SCOPE_IDENTITY();");
			stmt.setLong(1, jObject.getLong("backend_id"));
			stmt.setString(2, jObject.getString("group_name"));
			stmt.setInt(3, jObject.getInt("item_sequence"));
			stmt.setLong(4, jObject.getLong("id"));
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				jObject.put("item_group_id", rs.getInt(1));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return jObject.toString();
	}
	
	@RequestMapping(value = "/remove_item_group/{id}", method = RequestMethod.POST)
	public String removeItemGroup(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jObjectResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM menu_item_group_menu_item WHERE menu_item_group_id = ?");
			stmt.setLong(1, id);
			int affectedRow = stmt.executeUpdate();
			
			if(affectedRow != 0) {
				stmt = null;
				stmt = connection.prepareStatement("DELETE FROM menu_item_group WHERE id = ?");
				stmt.setLong(1, id);
				affectedRow = stmt.executeUpdate();
				
				if(affectedRow == 0) {
					response.setStatus(409);
					jObjectResult.put("response_message", "Fail to remove item group.").toString();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return jObjectResult.toString();
	}
	
}
