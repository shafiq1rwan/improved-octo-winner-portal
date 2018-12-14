package com.managepay.admin.byod.rest;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.managepay.admin.byod.entity.GroupCategory;
import com.managepay.admin.byod.service.GroupCategoryService;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@RestController
@RequestMapping("/menu/group_category")
public class GroupCategoryRestController {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping(value ="/get_all_group_category",produces = "application/json")
	public String getAllGroupCategory(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonGroupCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM group_category");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonGroupCategoryObj = new JSONObject();
				jsonGroupCategoryObj.put("id", rs.getLong("id"));
				jsonGroupCategoryObj.put("group_category_name", rs.getString("group_category_name"));		
				jsonGroupCategoryObj.put("created_date", rs.getDate("created_date"));	
				
				jsonGroupCategoryArray.put(jsonGroupCategoryObj);
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
		return 	jsonGroupCategoryArray.toString();
		
	}

	@GetMapping(value = "/get_group_category_by_id", produces = "application/json")
	public String getGroupCategoryById(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM group_category WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("group_category_name", rs.getString("group_category_name"));		
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

	@PostMapping(value = "/create_group_category",produces = "application/json")
	public String createGroupCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet keyRs = null;
		
		try {
			JSONObject jsonGroupCategoryData = new JSONObject(data);
			
			if(!jsonGroupCategoryData.has("group_category_name")) {
				response.setStatus(404);
				return jsonResult.put("response_message", "Group Category Name not found. Please try again.").toString(); 
			} else {
				int existingRecord = checkDuplicateGroupCategoryName(jsonGroupCategoryData.getString("group_category_name"));
				if(existingRecord!=0) {
					response.setStatus(409);
					return jsonResult.put("response_message", "Duplication Group Category Name Found!").toString();
				}
				else {
					connection = dataSource.getConnection();
					stmt = connection.prepareStatement("INSERT INTO group_category(group_category_name) VALUES(?)", new String[] {"id"});
					stmt.setString(1, jsonGroupCategoryData.getString("group_category_name"));
					stmt.executeUpdate();
					
					keyRs = stmt.getGeneratedKeys();
					if(keyRs.next()) {
								
						System.out.println("Key: " + keyRs.getLong(1));
						
						if(jsonGroupCategoryData.has("stores")) {
							JSONArray array = jsonGroupCategoryData.optJSONArray("stores");
							
							int[] stores = new int[array.length()];
							
							for (int i = 0; i < array.length(); ++i) {
								stores[i] = array.optInt(i);
							}
							
							for(int storeId: stores) {
								stmt2 = connection.prepareStatement("UPDATE store SET group_category_id = ? WHERE id = ?");
								stmt2.setLong(1, keyRs.getLong(1));
								stmt2.setLong(2, storeId);
								stmt2.executeUpdate();
							}
							
						}
					}
				}
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
	
	@PostMapping(value = "/edit_group_category",produces = "application/json")
	public String editGroupCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			JSONObject jsonGroupCategoryData = new JSONObject(data);
			
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement("UPDATE group_category SET group_category_name = ? WHERE id = ?");
				stmt.setString(1, jsonGroupCategoryData.getString("group_category_name"));
				stmt.setLong(2, jsonGroupCategoryData.getLong("id"));
				int rowAffected = stmt.executeUpdate();
				
				System.out.println("Edit Row " + rowAffected);	
				if(rowAffected == 1) {
					
					if(jsonGroupCategoryData.has("stores")) {		
						JSONArray array = jsonGroupCategoryData.optJSONArray("stores");
						System.out.println(array.length());
	
							stmt = connection.prepareStatement("UPDATE store SET group_category_id = 0 WHERE group_category_id = ?");
							stmt.setLong(1, jsonGroupCategoryData.getLong("id"));
							stmt.executeUpdate();

							int[] stores = new int[array.length()];
							
							for (int i = 0; i < array.length(); ++i) {
								stores[i] = array.optInt(i);
							}
							
							for(int storeId: stores) {
								stmt = connection.prepareStatement("UPDATE store SET group_category_id = ? WHERE id = ?");
								stmt.setLong(1, jsonGroupCategoryData.getLong("id"));
								stmt.setLong(2, storeId);
								stmt.executeUpdate();
							}
						
					}
				}			
		}catch(SQLServerException ex) {
			ex.printStackTrace();
			response.setStatus(409);
			try {
				jsonResult.put("response_message", "Duplicate Group Category Name Found!");
				return jsonResult.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		catch(Exception ex) {
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
	
	@DeleteMapping(value = "/delete_group_category",produces = "application/json")
	public String deleteGroupCategory(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM group_category WHERE id = ?");
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();
			
			if(categoryRowAffected == 0) {
				response.setStatus(400);
				jsonResult.put("response_message", "Group Category Encountered Error While Perform Deletion!");
			} else {
				stmt = connection.prepareStatement("DELETE FROM category WHERE group_category_id = ?");
				stmt.setLong(1, id);
				stmt.executeUpdate();
				
				stmt = connection.prepareStatement("UPDATE store SET group_category_id = 0 WHERE group_category_id = ?");
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
	
	@GetMapping(value ="/get_unassigned_store",produces = "application/json")
	public String getAllUnassignedStore(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonStoreArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM store WHERE group_category_id IS NULL OR group_category_id = 0");
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonStoreObj = new JSONObject();
				jsonStoreObj.put("id", rs.getLong("id"));
				jsonStoreObj.put("store_name", rs.getString("store_name"));		
				jsonStoreArray.put(jsonStoreObj);
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
		return 	jsonStoreArray.toString();
		
	}
	
	@GetMapping(value ="/get_all_store_by_group_category_id",produces = "application/json")
	public String getAllStoreByGroupCategoryId(HttpServletRequest request, HttpServletResponse response, @RequestParam("group_category_id") Long groupCategoryId) {
		JSONArray jsonStoreArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM store WHERE group_category_id = ?");
			stmt.setLong(1, groupCategoryId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonStoreObj = new JSONObject();
				jsonStoreObj.put("id", rs.getLong("id"));
				jsonStoreObj.put("store_name", rs.getString("store_name"));	
				jsonStoreObj.put("is_publish", rs.getBoolean("is_publish"));
				jsonStoreArray.put(jsonStoreObj);
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
		return 	jsonStoreArray.toString();
	}
	
	@GetMapping(value ="/get_all_store",produces = "application/json")
	public String getAllStore(HttpServletRequest request, HttpServletResponse response, @RequestParam("group_category_id") Long groupCategoryId) {
		JSONArray jsonStoreArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM store WHERE group_category_id IN (0,?)");
			stmt.setLong(1, groupCategoryId);
			rs = (ResultSet) stmt.executeQuery();
			
			while(rs.next()) {
				JSONObject jsonStoreObj = new JSONObject();
				jsonStoreObj.put("id", rs.getLong("id"));
				jsonStoreObj.put("group_category_id", rs.getLong("group_category_id"));
				jsonStoreObj.put("store_name", rs.getString("store_name"));	
				jsonStoreObj.put("is_publish", rs.getString("is_publish"));
				
				jsonStoreArray.put(jsonStoreObj);
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
		return 	jsonStoreArray.toString();
		
	}
	
	

	private int checkDuplicateGroupCategoryName(String groupCategoryName) {
		return jdbcTemplate.queryForObject("SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ?", new Object[] {groupCategoryName}, Integer.class);
	}
	
	private int checkDuplicateGroupCategoryNameById(String groupCategoryName, Long id) {
		System.out.println(groupCategoryName);
		return jdbcTemplate.queryForObject("SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ? AND id = ?", new Object[] {groupCategoryName, id}, Integer.class);

	}
	
	

}
