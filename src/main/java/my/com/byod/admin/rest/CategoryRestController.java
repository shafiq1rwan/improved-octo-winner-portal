package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/menu/category")
public class CategoryRestController {

	@Value("${upload-path}")
	private String filePath;
	
	@Value("${get-upload-path}")
	private String displayFilePath;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private GroupCategoryRestController groupCategoryRestController;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;

	@GetMapping(value = { "/get_all_category" }, produces = "application/json")
	public ResponseEntity<?> getAllCategory(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);

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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(jsonCategoryArray.toString());
	}

	@GetMapping(value = { "/get_all_category_by_group_category_id" }, produces = "application/json")
	public ResponseEntity<?> getAllCategoryByGroupCategoryId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("group_category_id") Long groupCategoryId) {
		JSONArray jsonCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);

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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(jsonCategoryArray.toString());
	}

	@GetMapping(value = { "/get_category_by_id" }, produces = "application/json")
	public ResponseEntity<?> getCategoryById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			stmt = connection.prepareStatement("SELECT * FROM category WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("group_category_id", rs.getLong("group_category_id"));
				jsonResult.put("category_name", rs.getString("category_name"));
				jsonResult.put("category_description", rs.getString("category_description"));
				jsonResult.put("category_image_path", displayFilePath + brandId + "/" + rs.getString("category_image_path"));
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
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Unable to find category detail");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(jsonResult.toString());
	}

	@PostMapping("/create_category")
	public ResponseEntity<?> createCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			
			JSONObject jsonCategoryData = new JSONObject(data);
			if (jsonCategoryData.has("group_category_id") && jsonCategoryData.has("category_name")) {
				connection = dbConnectionUtil.retrieveConnection(request);
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String description = jsonCategoryData.isNull("category_description") ? null
						: jsonCategoryData.getString("category_description");
				String imagePath = jsonCategoryData.isNull("category_image_path") ? null
						: byodUtil.saveImageFile(brandId,"imgC",jsonCategoryData.getString("category_image_path"), null);
				String sqlStatement = "INSERT into category(group_category_id, category_name, category_description, category_image_path, category_sequence, is_active, created_date) VALUES (?, ?, ?, ?, ?, ?, NOW());";
				stmt = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
				stmt.setLong(1, jsonCategoryData.getLong("group_category_id"));
				stmt.setString(2, jsonCategoryData.getString("category_name"));
				stmt.setString(3, description);
				stmt.setString(4, imagePath);
				stmt.setInt(5, getCategorySequenceNumber(jsonCategoryData.getLong("group_category_id"), connection) + 1);
				stmt.setBoolean(6, jsonCategoryData.getBoolean("is_active"));
				stmt.executeUpdate();
				rs = stmt.getGeneratedKeys();
				if(rs.next()) {
					// logging to file	
					String [] parameters = {
							String.valueOf(rs.getLong(1)),
							String.valueOf(jsonCategoryData.getLong("group_category_id")),
							jsonCategoryData.getString("category_name")==null?"null":"'"+jsonCategoryData.getString("category_name")+"'",
							description==null?"null":"'"+description+"'",
							imagePath==null?"null":"'"+imagePath+"'",
							String.valueOf(getCategorySequenceNumber(jsonCategoryData.getLong("group_category_id"), connection) + 1),
							String.valueOf(jsonCategoryData.getBoolean("is_active")?1:0)};	
					groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, jsonCategoryData.getLong("group_category_id"), imagePath, 1, "category");
					
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Category name not available");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplicate category name");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	@PostMapping(value = "/edit_category",produces = "application/json")
	public ResponseEntity<?> editCategory(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		String [] parameters = null;

		try {
			JSONObject jsonCategoryData = new JSONObject(data);
			if (jsonCategoryData.has("id") && jsonCategoryData.has("category_name")) {
				connection = dbConnectionUtil.retrieveConnection(request);
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				String description = jsonCategoryData.isNull("category_description") ? null
						: jsonCategoryData.getString("category_description");	
				
				// getting existing category image
				String existingImage = null;
				String sqlStatement = "SELECT category_image_path FROM category WHERE id = ?;";
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, jsonCategoryData.getLong("id"));
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					existingImage = rs.getString("category_image_path");
				}
				
				String imagePath = jsonCategoryData.isNull("category_image_path") ? null
						: byodUtil.saveImageFile(brandId,"imgC",jsonCategoryData.getString("category_image_path"), existingImage);
				
				if(imagePath == null)
					sqlStatement = "UPDATE category SET category_name = ?, category_description = ?, is_active = ? WHERE id = ?;";
				else
					sqlStatement = "UPDATE category SET category_name = ?, category_description = ?, is_active = ?, category_image_path = ? WHERE id = ?;";
					
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setString(1, jsonCategoryData.getString("category_name"));
				stmt.setString(2, description);
				stmt.setBoolean(3, jsonCategoryData.getBoolean("is_active"));
				
				if(imagePath == null) {
					stmt.setLong(4, jsonCategoryData.getLong("id"));
					
					parameters = new String []{
							jsonCategoryData.getString("category_name")==null?"null":"'"+jsonCategoryData.getString("category_name")+"'",
							description==null?"null":"'"+description+"'",
							String.valueOf(jsonCategoryData.getBoolean("is_active")?1:0),
							String.valueOf(jsonCategoryData.getLong("id"))};
					
				} else {
					stmt.setLong(4, jsonCategoryData.getLong("id"));
					stmt.setString(5, imagePath);
					
					// logging to file	
					parameters = new String []{
							jsonCategoryData.getString("category_name")==null?"null":"'"+jsonCategoryData.getString("category_name")+"'",
							description==null?"null":"'"+description+"'",
							imagePath==null?"null":"'"+imagePath+"'",
							String.valueOf(jsonCategoryData.getBoolean("is_active")?1:0),
							String.valueOf(jsonCategoryData.getLong("id"))};
				}
				stmt.executeUpdate();
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, jsonCategoryData.getLong("group_category_id"), imagePath, 1, null);			
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Category name not available");
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplicate category name");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}
	
	@PostMapping(value = "/edit_category_sequence",produces = "application/json")
	public ResponseEntity<?> editCategorySequence(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonObjForm = new JSONObject(data);
			Long group_category_id =  jsonObjForm.getLong("group_category_id");		
			JSONArray jsonCategorySequenceArray = jsonObjForm.getJSONArray("array");
			
			String sqlStatement = "UPDATE category SET category_sequence = ? WHERE id = ?;";
			connection = dbConnectionUtil.retrieveConnection(request);
			connection.setAutoCommit(false);
			stmt = connection.prepareStatement(sqlStatement);
			
			for(int i=0;i<jsonCategorySequenceArray.length();i++) {
				int index = i + 1;
				JSONObject jsonObj = jsonCategorySequenceArray.getJSONObject(i);			
				stmt.setInt(1, index);
				stmt.setLong(2, jsonObj.getLong("id"));
				stmt.executeUpdate();
				
				// logging to file	
				String [] parameters = {
						String.valueOf(index),
						String.valueOf(jsonObj.getLong("id"))};			
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, group_category_id, null, 0, null);					
			}		
			connection.commit();	
		}
		catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	@DeleteMapping("/delete_category")
	public ResponseEntity<?> deleteCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Long group_category_id = null;
			connection = dbConnectionUtil.retrieveConnection(request);
			// get group_category_id
			stmt = connection.prepareStatement("SELECT a.id FROM group_category a "
					+ "INNER JOIN category b ON a.id = b.group_category_id "
					+ "WHERE b.id = ? ");
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				group_category_id = rs.getLong("id");
			}	
			
			String sqlStatement = "DELETE FROM category WHERE id = ?;";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(id)};		
			groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, group_category_id, null, 0, null);	

			if (categoryRowAffected == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Cannot delete category");
			} else {
				sqlStatement = "DELETE FROM category_menu_item WHERE category_id = ?;";
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, id);
				stmt.executeUpdate();
				
				// logging to file	
				String [] parameters2 = {
						String.valueOf(id)};		
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters2, group_category_id, null, 0, null);	
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	private int getCategorySequenceNumber(Long groupCategoryId, Connection connection) {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			/*stmt = connection.prepareStatement("SELECT TOP 1 category_sequence FROM category WHERE group_category_id = ? ORDER BY category_sequence DESC");*/
			stmt = connection.prepareStatement("SELECT category_sequence FROM category WHERE group_category_id = ? ORDER BY category_sequence DESC LIMIT 1");
			stmt.setLong(1, groupCategoryId);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("category_sequence");
			} else {
				return 0;
			}
		} catch (Exception ex) {
			return 0;
		}
	}

/*	private int checkDuplicateCategoryName(String categoryName) {
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
	}*/
	
	//TODO need change
	@PostMapping("/assign_menu_item_to_category")
	public ResponseEntity<?> assignMenuItemToCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			JSONObject jsonObj = new JSONObject(data);
			JSONArray jsonItemsArray = jsonObj.getJSONArray("item_list");
			Long categoryId = jsonObj.getLong("category_id");
			Long group_category_id = jsonObj.getLong("group_category_id");
			
			connection = dbConnectionUtil.retrieveConnection(request);
			connection.setAutoCommit(false);
			
			String sqlStatement = "INSERT INTO category_menu_item (category_id, menu_item_id, category_menu_item_sequence) VALUES (?, ?, ?)";
			
			for(int count=0; count<jsonItemsArray.length() -1;count++) {
				sqlStatement += ", (?, ?, ?)";
			}
			sqlStatement+=";";
			
			stmt = connection.prepareStatement(sqlStatement);
			int insertionIndex = 0;
			int parameterCount = jsonItemsArray.length() * 3;
			String [] parameters = new String[parameterCount];
			
			for(int i=0;i<jsonItemsArray.length();i++) {
				int index = i;
				JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
				
				// 3 parameter for stmt
				parameters[insertionIndex] = String.valueOf(categoryId);
				stmt.setLong(++insertionIndex, categoryId);
				
				parameters[insertionIndex] = String.valueOf(jsonItemObj.getLong("id"));
				stmt.setLong(++insertionIndex, jsonItemObj.getLong("id"));
				
				parameters[insertionIndex] = String.valueOf(index+1);
				stmt.setInt(++insertionIndex, index+1);	
			}
			// logging to file		
			groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, group_category_id, null, 0, null);
			
			stmt.executeUpdate();
			connection.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok().body(null);
	}
	
	//TODO need change
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
			Long group_category_id = jsonObj.getLong("group_category_id");
			
			connection = dbConnectionUtil.retrieveConnection(request);
			
			String sqlStatement = "DELETE FROM category_menu_item WHERE category_id = ?;";
			
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, categoryId);
			int rowAffected = stmt.executeUpdate();

			if(rowAffected == 0) {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot reassign menu item");
			} else {
				// logging to file	
				String [] parameters = {
						String.valueOf(categoryId)
						};		
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters, group_category_id, null, 0, null);
			}
			
			if(jsonItemsArray.length() > 0) {
				connection.setAutoCommit(false);
				
				sqlStatement = "INSERT INTO category_menu_item (category_id, menu_item_id, category_menu_item_sequence) VALUES (?, ?, ?)";
				for(int count=0; count<jsonItemsArray.length() -1;count++) {
					sqlStatement += ", (?, ?, ?)";
				}
				sqlStatement+=";";
				
				stmt2 = connection.prepareStatement(sqlStatement);
				int insertionIndex = 0;
				int parameterCount = jsonItemsArray.length() * 3;
				String [] parameters2 = new String[parameterCount];		
				
				for(int i=0;i<jsonItemsArray.length();i++) {
					int index = i;
					JSONObject jsonItemObj = jsonItemsArray.getJSONObject(i);
					
					// 3 parameter for stmt
					parameters2[insertionIndex] = String.valueOf(categoryId);
					stmt2.setLong(++insertionIndex, categoryId);
					
					parameters2[insertionIndex] = String.valueOf(jsonItemObj.getLong("id"));
					stmt2.setLong(++insertionIndex, jsonItemObj.getLong("id"));
					
					parameters2[insertionIndex] = String.valueOf(index+1);
					stmt2.setInt(++insertionIndex, index+1);
				}
				// logging to file	
				groupCategoryRestController.logActionToFile(connection, sqlStatement, parameters2, group_category_id, null, 0, null);
				
				stmt2.executeUpdate();
				connection.commit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

}
