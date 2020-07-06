package my.com.byod.admin.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
@RequestMapping("/menu/menuItem")
public class MenuItemRestController {

	@Value("${get-upload-path}")
	private String displayFilePath;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private GroupCategoryRestController groupCategoryRestController;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@GetMapping(value = "/getMenuItemType", produces = "application/json")
	public ResponseEntity<?> getMenuItemType(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemTypeArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {			
			connection = dbConnectionUtil.retrieveConnection(request);		
			stmt = connection.prepareStatement("SELECT * FROM menu_item_type_lookup");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemTypeObj = new JSONObject();
				jsonMenuItemTypeObj.put("menu_item_type_id", rs.getInt("menu_item_type_number"));
				jsonMenuItemTypeObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));

				jsonMenuItemTypeArray.put(jsonMenuItemTypeObj);
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
		return ResponseEntity.ok().body(jsonMenuItemTypeArray.toString());
	}

	@GetMapping(value = "/getAllMenuItem", produces = "application/json")
	public ResponseEntity<?> getAllMenuItem(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM menu_item");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("modifier_group_id", rs.getLong("modifier_group_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_barcode", rs.getString("menu_item_barcode"));
				jsonMenuItemObj.put("menu_item_description", rs.getString("menu_item_description"));
				jsonMenuItemObj.put("menu_item_image_path", rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type", rs.getInt("menu_item_type"));
				jsonMenuItemObj.put("menu_quantity_stock", rs.getInt("menu_quantity_stock"));
				jsonMenuItemObj.put("is_taxable", rs.getBoolean("is_taxable"));
				jsonMenuItemObj.put("is_discountable", rs.getBoolean("is_discountable"));
				jsonMenuItemObj.put("is_active", rs.getBoolean("is_active"));
				jsonMenuItemObj.put("created_date", rs.getDate("created_date"));

				jsonMenuItemArray.put(jsonMenuItemObj);
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
		return ResponseEntity.ok().body(jsonMenuItemArray.toString());
	}

	@GetMapping(value = "/getAllMenuItemByType", produces = "application/json")
	public ResponseEntity<?> getAllMenuItemByType(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("menuItemType") int menuItemType) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			if (menuItemType == -1) {
				// ala carte + combo
				stmt = connection.prepareStatement(
						"SELECT mi.*, mitl.menu_item_type_name FROM menu_item mi INNER JOIN menu_item_type_lookup mitl ON mi.menu_item_type = mitl.menu_item_type_number WHERE menu_item_type IN (0, 1) AND is_active = 1");
			} else {
				stmt = connection.prepareStatement(
						"SELECT mi.*, mitl.menu_item_type_name FROM menu_item mi INNER JOIN menu_item_type_lookup mitl ON mi.menu_item_type = mitl.menu_item_type_number WHERE menu_item_type = ? AND is_active = 1");
				stmt.setInt(1, menuItemType);
			}
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("modifier_group_id", rs.getLong("modifier_group_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_barcode", rs.getString("menu_item_barcode"));
				jsonMenuItemObj.put("menu_item_description", rs.getString("menu_item_description"));
				jsonMenuItemObj.put("menu_item_image_path", displayFilePath + brandId + "/" + rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type", rs.getInt("menu_item_type"));
				jsonMenuItemObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));
				jsonMenuItemObj.put("menu_quantity_stock", rs.getInt("menu_quantity_stock"));
				jsonMenuItemObj.put("is_taxable", rs.getBoolean("is_taxable"));
				jsonMenuItemObj.put("is_discountable", rs.getBoolean("is_discountable"));
				jsonMenuItemObj.put("is_active", rs.getBoolean("is_active"));
				jsonMenuItemObj.put("created_date", rs.getDate("created_date"));

				jsonMenuItemArray.put(jsonMenuItemObj);
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
		return ResponseEntity.ok().body(jsonMenuItemArray.toString());
	}

	@GetMapping(value = "/getAllAlaCartMenuItem", produces = "application/json")
	public ResponseEntity<?> getAllAlaCartMenuItem(HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM menu_item WHERE menu_item_type = 0 AND is_active = 1");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("type", "Item");

				jsonMenuItemArray.put(jsonMenuItemObj);
			}
			jsonResult.put("data", jsonMenuItemArray);
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
		return ResponseEntity.ok().body(jsonResult.toString());
	}

	@GetMapping(value = "/getMenuItemById", produces = "application/json")
	public ResponseEntity<?> getMenuItemById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			stmt = connection.prepareStatement("SELECT * FROM menu_item WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("backend_id", rs.getString("backend_id"));
				jsonResult.put("menu_item_name", rs.getString("menu_item_name"));
				jsonResult.put("menu_item_alt_name", rs.getString("menu_item_alt_name"));
				jsonResult.put("menu_item_barcode", rs.getString("menu_item_barcode"));
				jsonResult.put("menu_item_description", rs.getString("menu_item_description"));
				jsonResult.put("menu_item_image_path", displayFilePath + brandId + "/" + rs.getString("menu_item_image_path"));
				jsonResult.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonResult.put("menu_item_type", rs.getInt("menu_item_type"));
				jsonResult.put("menu_quantity_stock", rs.getInt("menu_quantity_stock"));
				jsonResult.put("is_taxable", rs.getBoolean("is_taxable"));
				jsonResult.put("is_discountable", rs.getBoolean("is_discountable"));
				jsonResult.put("created_date", rs.getDate("created_date"));
			} else {
				return ResponseEntity.notFound().build();
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
		return ResponseEntity.ok().body(jsonResult.toString());
	}
	
	@PostMapping(value = "/createMenuItem", produces = "application/json")
	public ResponseEntity<?> createMenuItem(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		//ResponseEntity<String> responseEntity = ResponseEntity.badRequest().body(null);

		try {
			JSONObject jsonMenuItemData = new JSONObject(data);

			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String imagePath = jsonMenuItemData.isNull("menu_item_image_path") ? null
					: byodUtil.saveImageFile(brandId,"imgMI", jsonMenuItemData.getString("menu_item_image_path"), null);      
			String description = jsonMenuItemData.isNull("menu_item_description") ? null
					: jsonMenuItemData.getString("menu_item_description");
			String altName = jsonMenuItemData.isNull("menu_item_alt_name") ? null 
					: jsonMenuItemData.getString("menu_item_alt_name");
			String barcode = jsonMenuItemData.isNull("menu_item_barcode") ? null
					: jsonMenuItemData.getString("menu_item_barcode");
			int quantity = jsonMenuItemData.isNull("menu_quantity_stock") ? null : jsonMenuItemData.getInt("menu_quantity_stock");
			System.out.println("Quantity Stock: "+quantity);
			
			if(barcode!=null) {
				int existingBarcode = checkingExistingBarcode(barcode, request);
				if(existingBarcode != 0) {
					return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplication Barcode Found");
				}
			}
			
			String sqlStatement = "INSERT INTO menu_item (backend_id, menu_item_name, menu_item_alt_name, menu_item_barcode, menu_item_description, menu_item_image_path, menu_item_base_price, menu_item_type, menu_quantity_stock, is_taxable, is_discountable, created_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW());";
			stmt = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, jsonMenuItemData.getString("menu_item_backend_id"));
			stmt.setString(2, jsonMenuItemData.getString("menu_item_name"));
			stmt.setString(3, altName);
			stmt.setString(4, barcode);
			stmt.setString(5, description);
			stmt.setString(6, imagePath);
			stmt.setBigDecimal(7, BigDecimal.valueOf(jsonMenuItemData.getDouble("menu_item_base_price")));
			stmt.setInt(8, jsonMenuItemData.getInt("menu_item_type"));
			stmt.setInt(9, quantity);
			stmt.setBoolean(10, jsonMenuItemData.getBoolean("is_taxable"));
			stmt.setBoolean(11, jsonMenuItemData.getBoolean("is_discountable"));
			
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			if(rs.next()) {
			// logging to file	
			String [] parameters = {
					String.valueOf(rs.getLong(1)),
					jsonMenuItemData.getString("menu_item_backend_id")==null?"null":"'"+jsonMenuItemData.getString("menu_item_backend_id")+"'",
					jsonMenuItemData.getString("menu_item_name")==null?"null":"'"+jsonMenuItemData.getString("menu_item_name")+"'",
					altName==null?"null":"'"+altName+"'",
					barcode==null?"null":"'"+barcode+"'",
					description==null?"null":"'"+description+"'",
					imagePath==null?"null":"'"+imagePath+"'",
					String.valueOf(jsonMenuItemData.getDouble("menu_item_base_price")),
					String.valueOf(jsonMenuItemData.getInt("menu_item_type")),
					String.valueOf(quantity),
					String.valueOf(jsonMenuItemData.getBoolean("is_taxable")?1:0),
					String.valueOf(jsonMenuItemData.getBoolean("is_discountable")?1:0)};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, imagePath, 1, "menu_item");	
			}
			else {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create menu item");
			}
		} catch (SQLException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplication Backend Id Found");
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
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping(value = "/editMenuItem", produces = "application/json")
	public ResponseEntity<?> editMenuItem(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;
		String [] parameters = null;
		
		try {
			System.out.println(data);
			JSONObject jsonMenuItemData = new JSONObject(data);
			if (jsonMenuItemData.has("menu_item_name") && jsonMenuItemData.has("id")) {
				
				int existingMenuItemType = checkingExistingMenuItemType(jsonMenuItemData.getLong("id"), request);
				
				if(existingMenuItemType != -1 && existingMenuItemType != jsonMenuItemData.getInt("menu_item_type")) {			
					if(checkingAlreadyAssigned(jsonMenuItemData.getLong("id"), request)>0){
						return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Please unassigned first before performing type modification");
					}
				}
				connection = dbConnectionUtil.retrieveConnection(request);
				String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
				
				// getting existing category image
				String existingImage = null;
				String sqlStatement = "SELECT menu_item_image_path FROM menu_item WHERE id = ?;";
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setLong(1, jsonMenuItemData.getLong("id"));
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					existingImage = rs.getString("menu_item_image_path");
				}
				
				String imagePath = jsonMenuItemData.isNull("menu_item_image_path") ? null
						: byodUtil.saveImageFile(brandId,"imgMI", jsonMenuItemData.getString("menu_item_image_path"), existingImage); 
				String description = jsonMenuItemData.isNull("menu_item_description") ? null
						: jsonMenuItemData.getString("menu_item_description");
				String altName = jsonMenuItemData.isNull("menu_item_alt_name") ? null 
						: jsonMenuItemData.getString("menu_item_alt_name");
				String barcode = jsonMenuItemData.isNull("menu_item_barcode") ? null
						: jsonMenuItemData.getString("menu_item_barcode");
				int quantity = jsonMenuItemData.isNull("menu_quantity_stock") ? null : jsonMenuItemData.getInt("menu_quantity_stock");

				if(barcode!=null) {
					int existingBarcode = checkingExistingBarcode(jsonMenuItemData.getLong("id"), barcode, request);
					if(existingBarcode > 0) {
						return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplication Barcode Found");
					}
				}

				if(imagePath == null) {
					sqlStatement = "UPDATE menu_item SET backend_id = ?, menu_item_name = ?, menu_item_alt_name = ?, menu_item_barcode = ?, menu_item_description =?, menu_item_base_price = ?, menu_item_type = ?, menu_quantity_stock = ?,  is_taxable = ? , is_discountable = ? WHERE id = ?;";
				} else {
					sqlStatement = "UPDATE menu_item SET backend_id = ?, menu_item_name = ?, menu_item_alt_name = ?, menu_item_barcode = ?, menu_item_description =?, menu_item_base_price = ?, menu_item_type = ?, menu_quantity_stock = ?, is_taxable = ? , is_discountable = ?, menu_item_image_path = ? WHERE id = ?;";
				}
				
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setString(1, jsonMenuItemData.getString("menu_item_backend_id"));
				stmt.setString(2, jsonMenuItemData.getString("menu_item_name"));
				stmt.setString(3, altName);
				stmt.setString(4, barcode);
				stmt.setString(5, description);
				stmt.setBigDecimal(6, BigDecimal.valueOf(jsonMenuItemData.getDouble("menu_item_base_price")));
				stmt.setInt(7, jsonMenuItemData.getInt("menu_item_type"));
				stmt.setInt(8, quantity);
				stmt.setBoolean(9, jsonMenuItemData.getBoolean("is_taxable"));
				stmt.setBoolean(10, jsonMenuItemData.getBoolean("is_discountable"));
				
				if(imagePath == null) {
					stmt.setLong(11, jsonMenuItemData.getLong("id"));
					
					// logging to file	
					parameters = new String[] {
							jsonMenuItemData.getString("menu_item_backend_id")==null?"null":"'"+jsonMenuItemData.getString("menu_item_backend_id")+"'",
							jsonMenuItemData.getString("menu_item_name")==null?"null":"'"+jsonMenuItemData.getString("menu_item_name")+"'",
							altName == null?"null":"'"+altName+"'",	
							barcode == null?"null":"'"+barcode+"'",
							description==null?"null":"'"+description+"'",
							String.valueOf(jsonMenuItemData.getDouble("menu_item_base_price")),
							String.valueOf(jsonMenuItemData.getInt("menu_item_type")),
							String.valueOf(jsonMenuItemData.getBoolean("is_taxable")?1:0),
							String.valueOf(jsonMenuItemData.getBoolean("is_discountable")?1:0),
							String.valueOf(jsonMenuItemData.getLong("id"))};	
				} else {
					stmt.setString(11, imagePath);
					stmt.setLong(12, jsonMenuItemData.getLong("id"));
					
					// logging to file
					parameters = new String[] {
							jsonMenuItemData.getString("menu_item_backend_id")==null?"null":"'"+jsonMenuItemData.getString("menu_item_backend_id")+"'",
							jsonMenuItemData.getString("menu_item_name")==null?"null":"'"+jsonMenuItemData.getString("menu_item_name")+"'",
							altName == null?"null":"'" + altName + "'",
							barcode == null?"null":"'" + barcode + "'",		
							description==null?"null":"'"+description+"'",
							String.valueOf(jsonMenuItemData.getDouble("menu_item_base_price")),
							String.valueOf(jsonMenuItemData.getInt("menu_item_type")),
							String.valueOf(jsonMenuItemData.getBoolean("is_taxable")?1:0),
							String.valueOf(jsonMenuItemData.getBoolean("is_discountable")?1:0),
							"'" + imagePath + "'", 
							String.valueOf(jsonMenuItemData.getLong("id"))};	
				}
				int rowAffected = stmt.executeUpdate();
				
				// logging to file	
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, imagePath, 1, null);
				
				if (rowAffected == 0) {
					return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create menu item");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Menu Item Not Found");
			}
		} 
		catch (SQLException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body("Duplication Backend Id Found");
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

	// TODO delete associated item from menu_item_group and modifier_group
	@DeleteMapping(value = "/deleteMenuItem", produces = "application/json")
	public ResponseEntity<?> deleteMenuItem(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String sqlStatement = "UPDATE menu_item SET is_active = 0 WHERE id = ?;";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, id);
			int categoryRowAffected = stmt.executeUpdate();
			
			// logging to file	
			String [] parameters = {
					String.valueOf(id)
					};		
			groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0, null);
			
			if (categoryRowAffected == 0) {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot delete menu item");
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

	@PostMapping(value = "/updateMenuItemActiveStatus", produces = "application/json")
	public ResponseEntity<?> updateMenuItemActiveStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonMenuItemData = new JSONObject(data);
			if (jsonMenuItemData.has("id") && jsonMenuItemData.has("active_status")) {
				connection = dbConnectionUtil.retrieveConnection(request);
				String sqlStatement = "UPDATE menu_item SET is_active = ? WHERE id = ?;";
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setBoolean(1, !jsonMenuItemData.getBoolean("active_status"));
				stmt.setLong(2, jsonMenuItemData.getLong("id"));
				int rowAffected = stmt.executeUpdate();
				
				// logging to file	
				String [] parameters = {
						String.valueOf(!jsonMenuItemData.getBoolean("active_status")?1:0),
						String.valueOf(jsonMenuItemData.getLong("id"))};		
				groupCategoryRestController.logActionToAllFiles(connection, sqlStatement, parameters, null, 0, null);
				
				if (rowAffected == 0)
					return ResponseEntity.badRequest().body("Cannot update menu item active status");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Menu item not found");
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

	@GetMapping(value = "/getMenuItemByCategory", produces = "application/json")
	public ResponseEntity<?> getMenuItemByCategory(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("categoryId") Long categoryId) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			stmt = connection.prepareStatement(
					"SELECT mi.*, mitl.menu_item_type_name FROM menu_item mi INNER JOIN menu_item_type_lookup mitl ON mi.menu_item_type = mitl.menu_item_type_number INNER JOIN category_menu_item cmi ON mi.id = cmi.menu_item_id WHERE cmi.category_id = ?");
			stmt.setLong(1, categoryId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_barcode", rs.getString("menu_item_barcode"));
				jsonMenuItemObj.put("menu_item_image_path", displayFilePath + brandId + "/" + rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));
				jsonMenuItemArray.put(jsonMenuItemObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"); 
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok().body(jsonMenuItemArray.toString());
	}

	@GetMapping(value = "/getMenuItemWithoutDuplication", produces = "application/json")
	public ResponseEntity<?> getMenuItemWithoutDuplication(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("categoryId") Long categoryId) {
		JSONArray jsonMenuItemArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(
					"SELECT mi.*, mitl.menu_item_type_name FROM menu_item mi INNER JOIN menu_item_type_lookup mitl ON mi.menu_item_type = mitl.menu_item_type_number INNER JOIN category_menu_item cmi ON mi.id = cmi.menu_item_id WHERE cmi.category_id != ? AND mi.menu_item_type != 2");
			stmt.setLong(1, categoryId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("backend_id", rs.getString("backend_id"));
				jsonMenuItemObj.put("menu_item_name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("menu_item_barcode", rs.getString("menu_item_barcode"));
				jsonMenuItemObj.put("menu_item_image_path", rs.getString("menu_item_image_path"));
				jsonMenuItemObj.put("menu_item_base_price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("menu_item_type_name", rs.getString("menu_item_type_name"));
				jsonMenuItemArray.put(jsonMenuItemObj);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error"); 
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok().body(jsonMenuItemArray.toString());
	}
	
	//Used in edit mode
	private int checkingAlreadyAssigned(Long menuItemId, HttpServletRequest request) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlQuery = "SELECT COUNT(*) FROM "
				+ "(SELECT mi.* FROM menu_item mi INNER JOIN combo_item_detail cid ON mi.id = cid.menu_item_id UNION "
				+ "SELECT mi.* FROM menu_item mi INNER JOIN menu_item_group_sequence migs ON mi.id =migs.menu_item_id UNION "
				+ "SELECT mi.* FROM menu_item mi INNER JOIN modifier_item_sequence mis ON mi.id = mis.menu_item_id) AS a "
				+ "WHERE a.id = ?";
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) 
				return rs.getInt("COUNT(*)");
			else 
				return 0;	
		} catch(Exception ex) {
			return 0;
		}  finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					return 0;
				}
			}
		}
		
	}
	
	private int checkingExistingMenuItemType(Long menuItemId, HttpServletRequest request) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int existingItemType = 0;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT menu_item_type FROM menu_item WHERE id = ?");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			existingItemType = rs.getInt("menu_item_type");
		} catch(Exception ex) {
			existingItemType = -1;
		}  finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					existingItemType = -1;
				}
			}
		}
		
		return existingItemType;
	}
	
	private int checkingExistingBackendId(Long menuItemId, String backendId, HttpServletRequest request) throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int existingBackendId = 0;

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT COUNT(backend_id) FROM menu_item WHERE backend_id = ? AND id !=?");
			stmt.setString(1, backendId);
			stmt.setLong(2, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				existingBackendId = rs.getInt("COUNT(backend_id)");
			} else {
				existingBackendId = -1;
			}
		} catch(Exception ex) {
			throw ex;
		}  finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					existingBackendId = -1;
				}
			}
		}
		return existingBackendId;
	}
	
	private int checkingExistingBarcode(String barcode, HttpServletRequest request) throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int existingBarcode = 0;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT COUNT(menu_item_barcode) as 'existingBarcode' FROM menu_item WHERE menu_item_barcode = ?");
			stmt.setString(1, barcode);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				existingBarcode = rs.getInt("existingBarcode");
			} else {
				existingBarcode = -1;
			}
			
			System.out.println(existingBarcode);
			
		} catch(Exception ex) {
			throw ex;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
				}
			}
		}
		return existingBarcode;
	}

	private int checkingExistingBarcode(Long menuItemId, String barcode, HttpServletRequest request) throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int existingBarcode = 0;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT COUNT(menu_item_barcode) as 'existingBarcode' FROM menu_item WHERE menu_item_barcode = ? AND id !=?");
			stmt.setString(1, barcode);
			stmt.setLong(2, menuItemId);
			rs = (ResultSet) stmt.executeQuery();
			
			if(rs.next()) {
				existingBarcode = rs.getInt("existingBarcode");
			} else {
				existingBarcode = -1;
			}
			
			System.out.println(existingBarcode);
			
		} catch(Exception ex) {
			throw ex;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
				}
			}
		}
		return existingBarcode;
	}

}
