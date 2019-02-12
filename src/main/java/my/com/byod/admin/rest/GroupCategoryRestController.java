package my.com.byod.admin.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.microsoft.sqlserver.jdbc.SQLServerException;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/menu/group_category")
public class GroupCategoryRestController {
	
	@Value("${menu-path}")
	private String filePath;
	
	@Value("${upload-path}")
	private String imagePath;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@GetMapping(value ="/get_all_group_category",produces = "application/json")
	public String getAllGroupCategory(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonGroupCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {	
			connection = dbConnectionUtil.retrieveConnection(request);		
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
			connection = dbConnectionUtil.retrieveConnection(request);
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
				int existingRecord = checkDuplicateGroupCategoryName(jsonGroupCategoryData.getString("group_category_name"), request);
				if(existingRecord!=0) {
					response.setStatus(409);
					return jsonResult.put("response_message", "Duplication Group Category Name Found!").toString();
				}
				else {
					connection = dbConnectionUtil.retrieveConnection(request);
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
			
				connection = dbConnectionUtil.retrieveConnection(request);
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
			connection = dbConnectionUtil.retrieveConnection(request);
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
			connection = dbConnectionUtil.retrieveConnection(request);
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
			connection = dbConnectionUtil.retrieveConnection(request);
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
			connection = dbConnectionUtil.retrieveConnection(request);
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
	
	@GetMapping(value ="/publish_menu",produces = "application/json")
	public ResponseEntity<?> publishMenu( @RequestParam("group_category_id") Long groupCategoryId, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlStatement = "";
		JSONObject result = null;
		boolean firstPublish = false;
		String tmpImgFilePath = null;
		String tmpQueryFilePath = null;
		String menuImgFilePath = null;
		String menuQueryFilePath = null;
		String menuFilePath = null;
		Long versionCount = null;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			
			JSONObject groupCategoryInfo = getGroupCategoryInfoByID(connection, groupCategoryId);
			if(groupCategoryInfo.getLong("publish_version_id")==0) {
				// first time publish menu
				firstPublish = true;
				versionCount = (long) 1;
		
				if(groupCategoryInfo.has("tmp_img_file_path"))
					tmpImgFilePath = groupCategoryInfo.getString("tmp_img_file_path");
				else 
					tmpImgFilePath = byodUtil.createUniqueBackendId("TIF");
			}
			else {
				versionCount = groupCategoryInfo.getLong("version_count")+1;
				if(groupCategoryInfo.has("tmp_img_file_path"))
					tmpImgFilePath = groupCategoryInfo.getString("tmp_img_file_path");
			}
			
			// not first time publish menu and no query action performed
			if(!firstPublish && !checkTmpQueryFileExist(connection, groupCategoryInfo)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Previously published menu is already the latest version menu."); 
			}
			
			JSONArray categoryList = getCategoryListByGroupCategoryID(connection, groupCategoryId, tmpImgFilePath, firstPublish);
			for (int categoryIndex = 0; categoryIndex < categoryList.length(); categoryIndex++) {
				JSONArray itemList = getMenuItemListByCategoryID(connection, Integer.parseInt(categoryList.getJSONObject(categoryIndex).getString("id")), groupCategoryId, tmpImgFilePath, firstPublish);
				for (int itemIndex = 0; itemIndex < itemList.length(); itemIndex++) {
					JSONArray comboList = new JSONArray();
					JSONArray alacarteModifierList = new JSONArray();
					if (itemList.getJSONObject(itemIndex).getString("type").equals("1")) {
						comboList = getComboDetailListByComboOverheadID(connection, Integer.parseInt(itemList.getJSONObject(itemIndex).getString("id")));
						for (int comboIndex = 0; comboIndex < comboList.length(); comboIndex++) {
							JSONArray comboDetailList = getComboDetailItemListByComboDetailID(connection, Integer.parseInt(comboList.getJSONObject(comboIndex).getString("id")));
							
							JSONArray tierItemList = new JSONArray();
							for (int comboDetailIndex = 0; comboDetailIndex < comboDetailList.length(); comboDetailIndex++) {
								if (!comboDetailList.getJSONObject(comboDetailIndex).isNull("menuItemID") && comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemID") != null) {
									JSONObject menuItem = getMenuItemDataByMenuItemID(connection, Integer.parseInt(comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemID")), groupCategoryId, tmpImgFilePath, firstPublish);
									menuItem.put("modifierGroupList", getModifierListByMenuItemID(connection, Integer.parseInt(menuItem.getString("id"))));
									tierItemList.put(menuItem);
								} else {
									tierItemList = getMenuItemListByItemGroupID(connection, Integer.parseInt(comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemGroupID")), groupCategoryId, tmpImgFilePath, firstPublish);
									for (int tierIndex = 0; tierIndex < tierItemList.length(); tierIndex++) {
										tierItemList.getJSONObject(tierIndex).put("modifierGroupList", getModifierListByMenuItemID(connection, Integer.parseInt(tierItemList.getJSONObject(tierIndex).getString("id"))));
									}
								}
							}
							comboList.getJSONObject(comboIndex).put("itemList", tierItemList);
						}
					} else if (itemList.getJSONObject(itemIndex).getString("type").equals("0")) {
						alacarteModifierList = getModifierListByMenuItemID(connection, Integer.parseInt(itemList.getJSONObject(itemIndex).getString("id")));
					}
					itemList.getJSONObject(itemIndex).put("comboList", comboList);
					itemList.getJSONObject(itemIndex).put("modifierList", alacarteModifierList);
				}
				categoryList.getJSONObject(categoryIndex).put("itemList", itemList);
			}
			result = new JSONObject();
			result.put("menuList", categoryList);
			
			menuFilePath = createMenuFile(result);
			// get new info after logging tmp image file
			groupCategoryInfo = getGroupCategoryInfoByID(connection, groupCategoryId);		
			menuImgFilePath = extractImageList(connection, tmpImgFilePath, menuFilePath);
			
			if(groupCategoryInfo.has("tmp_query_file_path"))
				tmpQueryFilePath = groupCategoryInfo.getString("tmp_query_file_path");
			
			menuQueryFilePath = extractQueryFile(connection, tmpQueryFilePath, menuFilePath);		
			Long publishVersionId = updatePublishVersion(connection, groupCategoryId, versionCount, menuFilePath, menuQueryFilePath, menuImgFilePath);
			
			sqlStatement = "UPDATE group_category SET publish_version_id = ? WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, publishVersionId);
			stmt.setLong(2, groupCategoryId);
			int rowAffected = stmt.executeUpdate();
			if(rowAffected==0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Failed to publish menu.");
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
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);	
		
	}
	

	private int checkDuplicateGroupCategoryName(String groupCategoryName, HttpServletRequest request) {
		Connection connection = null;
		PreparedStatement stmt = null;
	    ResultSet rs = null;
	    int count =0;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
	        String query= "SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ?";
	        stmt = connection.prepareStatement(query);
	        stmt.setString(1,groupCategoryName);
            rs= stmt.executeQuery();

            while (rs.next()) {
                count=rs.getInt(1);
            }
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	private int checkDuplicateGroupCategoryNameById(String groupCategoryName, Long id, HttpServletRequest request) {
		Connection connection = null;
		PreparedStatement stmt = null;
	    ResultSet rs = null;
	    int count =0;
		
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
	        String query= "SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ? AND id = ?";
	        stmt = connection.prepareStatement(query);
	        stmt.setString(1,groupCategoryName);
	        stmt.setLong(2,id);
            rs= stmt.executeQuery();

            while (rs.next()) {
                count=rs.getInt("COUNT(group_category_name)");
            }
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	private JSONObject getGroupCategoryInfoByID(Connection connection, Long groupCategoryID) throws Exception {
		JSONObject groupCategoryInfo = null;
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT a.group_category_name, a.created_date, a.publish_version_id, a.tmp_query_file_path, a.tmp_img_file_path, b.version_count, b.menu_file_path, b.menu_query_file_path, b.menu_img_file_path, b.publish_date FROM group_category a "
					+ "LEFT JOIN publish_version b ON a.publish_version_id = b.id AND b.group_category_id = a.id "
					+ "WHERE a.id = ?;";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, groupCategoryID);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				groupCategoryInfo = new JSONObject();
				groupCategoryInfo.put("group_category_name", rs1.getString("group_category_name"));
				groupCategoryInfo.put("created_date",  rs1.getString("created_date"));
				groupCategoryInfo.put("publish_version_id", rs1.getLong("publish_version_id"));
				groupCategoryInfo.put("tmp_query_file_path",  rs1.getString("tmp_query_file_path"));
				groupCategoryInfo.put("version_count", rs1.getString("version_count"));
				groupCategoryInfo.put("tmp_img_file_path",  rs1.getString("tmp_img_file_path"));
				groupCategoryInfo.put("menu_file_path",  rs1.getString("menu_file_path"));
				groupCategoryInfo.put("menu_query_file_path",  rs1.getString("menu_query_file_path"));
				groupCategoryInfo.put("menu_img_file_path",  rs1.getString("menu_img_file_path"));
				groupCategoryInfo.put("publish_date",  rs1.getString("publish_date"));
				System.out.println(groupCategoryInfo.toString());
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return groupCategoryInfo;
	}
	
	private JSONArray getCategoryListByGroupCategoryID(Connection connection, Long groupCategoryID, String tmpImgFilePath, boolean firstPublish) throws Exception {
		JSONArray categoryList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT id, category_name, category_image_path FROM category WHERE group_category_id = ? AND is_active = 1 ORDER BY category_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, groupCategoryID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject categoryItem = new JSONObject();
				categoryItem.put("id",  rs1.getString("id"));
				categoryItem.put("name", rs1.getString("category_name"));
				categoryItem.put("path",  rs1.getString("category_image_path"));

				categoryList.put(categoryItem);
				
				// first time publish menu
				/*if(firstPublish && rs1.getString("category_image_path")!=null && !rs1.getString("category_image_path").equals(""))
					logImageFile(connection, tmpImgFilePath, rs1.getString("category_image_path"), 1, groupCategoryID);*/
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return categoryList;
	}
	
	private JSONArray getMenuItemListByCategoryID(Connection connection, int categoryID, Long groupCategoryID, String tmpImgFilePath, boolean firstPublish) throws Exception {
		JSONArray menuList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_description, mi.menu_item_type, mi.menu_item_image_path, mi.menu_item_base_price FROM category_menu_item cmi, menu_item mi WHERE cmi.category_id = ? AND cmi.menu_item_id = mi.id and mi.is_active = 1 ORDER BY category_menu_item_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, categoryID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject menuItem = new JSONObject();
				menuItem.put("id", rs1.getString("id"));
				menuItem.put("name", rs1.getString("menu_item_name"));
				menuItem.put("description", rs1.getString("menu_item_description"));
				menuItem.put("type", rs1.getString("menu_item_type"));
				menuItem.put("path", rs1.getString("menu_item_image_path"));
				menuItem.put("price", String.format("%.2f", rs1.getDouble("menu_item_base_price")));

				menuList.put(menuItem);
				
				// first time publish menu
				/*if(firstPublish && rs1.getString("menu_item_image_path")!=null && !rs1.getString("menu_item_image_path").equals(""))
					logImageFile(connection, tmpImgFilePath, rs1.getString("menu_item_image_path"), 1, groupCategoryID);*/
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return menuList;
	}
	
	private JSONArray getComboDetailListByComboOverheadID(Connection connection, int comboOverheadID) throws Exception {
		JSONArray menuList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT id, combo_detail_name, combo_detail_quantity FROM combo_detail WHERE menu_item_id = ? ORDER BY combo_detail_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, comboOverheadID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject comboDetail = new JSONObject();
				comboDetail.put("id", rs1.getString("id"));
				comboDetail.put("name", rs1.getString("combo_detail_name"));
				comboDetail.put("quantity", rs1.getString("combo_detail_quantity"));

				menuList.put(comboDetail);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return menuList;
	}
	
	private JSONArray getComboDetailItemListByComboDetailID(Connection connection, int comboDetailID) throws Exception {
		JSONArray menuList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT menu_item_id, menu_item_group_id FROM combo_item_detail WHERE combo_detail_id = ? ORDER BY combo_item_detail_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, comboDetailID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject comboDetail = new JSONObject();
				comboDetail.put("menuItemID", rs1.getString("menu_item_id"));
				comboDetail.put("menuItemGroupID", rs1.getString("menu_item_group_id"));

				menuList.put(comboDetail);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return menuList;
	}
	
	private JSONObject getMenuItemDataByMenuItemID(Connection connection, int menuItemID, Long groupCategoryID, String tmpImgFilePath, boolean firstPublish) throws Exception {
		JSONObject menuItem = new JSONObject();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT id, menu_item_name, menu_item_image_path, menu_item_base_price FROM menu_item WHERE id = ? AND is_active = 1";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, menuItemID);
			rs1 = ps1.executeQuery();
			rs1.next();

			menuItem.put("id", rs1.getString("id"));
			menuItem.put("name", rs1.getString("menu_item_name"));
			menuItem.put("path", rs1.getString("menu_item_image_path"));
			menuItem.put("price", rs1.getString("menu_item_base_price"));
			
			// first time publish menu
			/*if(firstPublish && rs1.getString("menu_item_image_path")!=null && !rs1.getString("menu_item_image_path").equals(""))
				logImageFile(connection, tmpImgFilePath, rs1.getString("menu_item_image_path"), 1, groupCategoryID);*/
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return menuItem;
	}
	
	private JSONArray getMenuItemListByItemGroupID(Connection connection, int itemGroupID, Long groupCategoryID, String tmpImgFilePath, boolean firstPublish) throws Exception {
		JSONArray menuItemList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_image_path, mi.menu_item_base_price FROM menu_item mi, menu_item_group_sequence migs WHERE migs.menu_item_group_id = ? AND mi.id = migs.menu_item_id ORDER BY migs.menu_item_group_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, itemGroupID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject menuItem = new JSONObject();
				menuItem.put("id", rs1.getString("id"));
				menuItem.put("name", rs1.getString("menu_item_name"));
				menuItem.put("path", rs1.getString("menu_item_image_path"));
				menuItem.put("price", rs1.getString("menu_item_base_price"));

				menuItemList.put(menuItem);
				
				// first time publish menu
				/*if(firstPublish && rs1.getString("menu_item_image_path")!=null && !rs1.getString("menu_item_image_path").equals(""))
					logImageFile(connection, tmpImgFilePath, rs1.getString("menu_item_image_path"), 1, groupCategoryID);*/
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
		
		return menuItemList;
	}
	
	private JSONArray getModifierListByMenuItemID(Connection connection, int menuItemID) throws Exception {
		JSONArray modifierGroupList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			sqlStatement = "SELECT mimg.modifier_group_id, mg.modifier_group_name FROM menu_item_modifier_group mimg, modifier_group mg WHERE mimg.menu_item_id = ? AND mimg.modifier_group_id = mg.id AND mg.is_active = 1 ORDER BY mimg.menu_item_modifier_group_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, menuItemID);
			rs1 = ps1.executeQuery();
			while (rs1.next()) {
				JSONObject modifierGroupData = new JSONObject();
				modifierGroupData.put("name", rs1.getString("modifier_group_name"));

				JSONArray modifierList = new JSONArray();
				sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_base_price FROM menu_item mi, modifier_item_sequence mis WHERE mis.modifier_group_id = ? AND mi.id = mis.menu_item_id AND mi.is_active = 1 ORDER BY mis.modifier_item_sequence ASC";
				ps2 = connection.prepareStatement(sqlStatement);
				ps2.setInt(1, Integer.parseInt(rs1.getString("modifier_group_id")));
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
					JSONObject modifierData = new JSONObject();
					modifierData.put("id", rs2.getString("id"));
					modifierData.put("name", rs2.getString("menu_item_name"));
					modifierData.put("price", rs2.getString("menu_item_base_price"));

					modifierList.put(modifierData);
				}
				rs2.close();
				ps2.close();

				modifierGroupData.put("modifierList", modifierList);

				modifierGroupList.put(modifierGroupData);
			}
			rs1.close();
			ps1.close();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
			if (ps2 != null) {
				ps2.close();
			}
		}
		
		return modifierGroupList;
	}
	
	private String createMenuFile(JSONObject result){
		// write to json file
		String menuFilePath = byodUtil.createUniqueBackendId("MF");
		try {
			boolean checker = false;
			File checkdir = new File(filePath);
			checkdir.mkdirs();

			do {
				File checkFile = new File(filePath+menuFilePath);
				if (checkFile.exists()) {
					System.out.println("duplicate");
					checker = true;
					menuFilePath = byodUtil.createUniqueBackendId("MF");;
				} else {
					checker = false;
					checkFile.mkdirs();
				}
			} while (checker);
			
			File file = new File(filePath+menuFilePath, menuFilePath + ".json");
			Writer output = new BufferedWriter(new FileWriter(file));
            output.write(result.toString());
            output.close();
            
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return menuFilePath;
	}
	
	
	public void logActionToFile(Connection connection, String query, String[] parameters, Long groupCategoryId, String imageName, int saveType) throws Exception {
		// saveType for imageName
		// 0 - Nothing
		// 1 - Save
		// 2 - Delete
		
		String sqlStatement = "";
		String tmpQueryFilePath = "";
		String tmpImgFilePath = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Long publishVersionId = null;
		
		try {
			System.out.println(query);
			System.out.println(parameters.length);
			
			String[] splitData = query.split("\\?");
			query = splitData[0];
			for(int a=0; a<parameters.length; a++) {
				query += parameters[a] + splitData[a + 1];
			}
			query+="\r\n";
			
			System.out.println(query);
			
			sqlStatement = "SELECT publish_version_id, tmp_query_file_path, tmp_img_file_path FROM group_category "
					+ "WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, groupCategoryId);
			rs = stmt.executeQuery();
			
			if(rs.next()) {	
				publishVersionId = rs.getLong("publish_version_id");
				// never publish menu before, no need to log
				if(publishVersionId==0)
					return;
				
				tmpQueryFilePath = rs.getString("tmp_query_file_path");
				tmpImgFilePath = rs.getString("tmp_img_file_path");
				if(tmpQueryFilePath == null) {
					tmpQueryFilePath = byodUtil.createUniqueBackendId("TQF");
				}
				
				// write to txt file
				File checkdir = new File(filePath);
				checkdir.mkdirs();

				File checkFile = new File(filePath, tmpQueryFilePath + ".txt");
				if (checkFile.exists()) {	
					// append to existing file
					Writer output = new BufferedWriter(new FileWriter(checkFile, true));
		            output.write(query);
		            output.close();
				} else {
					// new file
					Writer output = new BufferedWriter(new FileWriter(checkFile));
		            output.write(query);
		            output.close();
				} 
				sqlStatement = "UPDATE group_category SET tmp_query_file_path = ? WHERE id = ? ";
				stmt.close();
				stmt = connection.prepareStatement(sqlStatement);
				stmt.setString(1, tmpQueryFilePath);
				stmt.setLong(2, groupCategoryId);
				stmt.executeUpdate();
				
				if(imageName!=null && !imageName.equals(""))
					logImageFile(connection, tmpImgFilePath, imageName, saveType, groupCategoryId);
			}       
		}catch (Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}
	
	public void logActionToAllFiles(Connection connection, String query, String[] parameters, String imageName, int saveType) throws Exception {
		// saveType for imageName
		// 0 - Nothing
		// 1 - Save
		// 2 - Delete
		
		String sqlStatement = "";
		String tmpQueryFilePath = "";
		String tmpImgFilePath = "";
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		Long groupCategoryId = null;
		Long publishVersionId = null;
		
		try {	
			System.out.println(query);
			System.out.println(parameters.length);
			
			String[] splitData = query.split("\\?");
			query = splitData[0];
			for(int a=0; a<parameters.length; a++) {
				query += parameters[a] + splitData[a + 1];
			}
			query+="\r\n";
			
			System.out.println(query);
			
			sqlStatement = "SELECT id, publish_version_id, tmp_query_file_path, tmp_img_file_path FROM group_category ";
			stmt = connection.prepareStatement(sqlStatement);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				groupCategoryId = rs.getLong("id");
				publishVersionId = rs.getLong("publish_version_id");
				
				// never publish menu before, no need to log
				if(publishVersionId==0)
					continue;
				
				tmpQueryFilePath = rs.getString("tmp_query_file_path");
				tmpImgFilePath = rs.getString("tmp_img_file_path");
				
				if(tmpQueryFilePath == null) {
					tmpQueryFilePath = byodUtil.createUniqueBackendId("TQF");
				}
				// write to txt file
				File checkdir = new File(filePath);
				checkdir.mkdirs();

				File checkFile = new File(filePath, tmpQueryFilePath + ".txt");
				if (checkFile.exists()) {	
					// append to existing file
					Writer output = new BufferedWriter(new FileWriter(checkFile, true));
		            output.write(query);
		            output.close();
				} else {
					// new file
					Writer output = new BufferedWriter(new FileWriter(checkFile));
		            output.write(query);
		            output.close();
				}
				sqlStatement = "UPDATE group_category SET tmp_query_file_path = ? WHERE id = ? ";
				stmt2 = connection.prepareStatement(sqlStatement);
				stmt2.setString(1, tmpQueryFilePath);
				stmt2.setLong(2, groupCategoryId);
				stmt2.executeUpdate();
				
				if(imageName!=null && !imageName.equals(""))
					logImageFile(connection, tmpImgFilePath, imageName, saveType, groupCategoryId);
			}       
		}catch (Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (stmt2 != null) {
				stmt2.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}
	
	public void logImageFile(Connection connection, String tmpImgFilePath, String imageName, int saveType, Long groupCategoryId) throws Exception  {
		// saveType for imageName
		// 0 - Nothing
		// 1 - Save
		// 2 - Delete
		String sqlStatement = "";
		PreparedStatement stmt = null;
		
		try {
			// write to image file
			if(tmpImgFilePath==null) {
				tmpImgFilePath = byodUtil.createUniqueBackendId("TIF");
			}
			File checkdir = new File(filePath);
			checkdir.mkdirs();
			JSONObject writeResult = new JSONObject();
			ArrayList<String> imageList = new ArrayList<String>();
			File checkFile = new File(filePath, tmpImgFilePath + ".json");
			if (checkFile.exists()) {
				// read file
				BufferedReader br = new BufferedReader(new FileReader(checkFile));
				try {
				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();

				    while (line != null) {
				        sb.append(line);
				        sb.append(System.lineSeparator());
				        line = br.readLine();
				    }
				    String everything = sb.toString();
				    JSONObject jsonFile = new JSONObject(everything);						
				    JSONArray imageArray = jsonFile.getJSONArray("imageList");
				    for(int i = 0; i < imageArray.length(); i++)
				    	imageList.add(imageArray.getString(i));
				} finally {
				    br.close();
				}
				
				Writer output = new BufferedWriter(new FileWriter(checkFile));
				if(saveType==1) {
					// append image file
					if(!imageList.contains(imageName))
						imageList.add(imageName);	
				}else if(saveType==2) {
					// delete image file
					if(imageList.contains(imageName))
						imageList.remove(imageName);
				}
				writeResult.put("imageList", imageList);					
	            output.write(writeResult.toString());
	            output.close();
			} else {
				// new file
				Writer output = new BufferedWriter(new FileWriter(checkFile));
				imageList.add(imageName);
				writeResult.put("imageList", imageList);	
	            output.write(writeResult.toString());
	            output.close();
			}
			
			sqlStatement = "UPDATE group_category SET tmp_img_file_path = ? WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(1, tmpImgFilePath);
			stmt.setLong(2, groupCategoryId);
			stmt.executeUpdate();
		
		}catch(Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	private String extractImageList(Connection connection, String tmpImgFilePath, String menuFilePath) throws Exception {
		String menuImgFilePath = null;
		try {		
			if(tmpImgFilePath!=null && !tmpImgFilePath.equals("")) {

				ArrayList<String> imageList = new ArrayList<String>();
				File checkFile = new File(filePath, tmpImgFilePath + ".json");
				if (checkFile.exists()) {
					// read file
					BufferedReader br = new BufferedReader(new FileReader(checkFile));
					try {
					    StringBuilder sb = new StringBuilder();
					    String line = br.readLine();
	
					    while (line != null) {
					        sb.append(line);
					        sb.append(System.lineSeparator());
					        line = br.readLine();
					    }
					    String everything = sb.toString();
					    JSONObject jsonFile = new JSONObject(everything);						
					    JSONArray imageArray = jsonFile.getJSONArray("imageList");
					    for(int i = 0; i < imageArray.length(); i++)
					    	imageList.add(imageArray.getString(i));
					} finally {
					    br.close();
					}
					
					/*for(int a = 0; a < imageList.size(); a++) {
						File source = new File(imagePath, imageList.get(a));
						if(source.exists()) {
							// copy image files
							File dest = new File(filePath + menuFilePath ,imageList.get(a));
							try {
								FileUtils.copyFile(source, dest);
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
					}*/
					
					// zipping images
					//List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
					menuImgFilePath = byodUtil.createUniqueBackendId("MIF");
					FileOutputStream fos = new FileOutputStream(filePath + menuFilePath + "/" + menuImgFilePath+".zip");
					ZipOutputStream zipOut = new ZipOutputStream(fos);
					for (String srcFile : imageList) {
						File fileToZip = new File(imagePath, srcFile);
						if(fileToZip.exists()) {
							FileInputStream fis = new FileInputStream(fileToZip);
							ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
							zipOut.putNextEntry(zipEntry);		
							byte[] bytes = new byte[1024];
							int length;
							while((length = fis.read(bytes)) >= 0) {
								zipOut.write(bytes, 0, length);
							}
							fis.close();
						}
					}
					zipOut.close();
					fos.close();
					
					// copy image json file
					File dest = new File(filePath + menuFilePath, menuImgFilePath + ".json");
					try {
						FileUtils.copyFile(checkFile, dest);
					}catch(Exception e) {
						e.printStackTrace();
					}
					// delete image json file
					checkFile.delete();
					
				} else {
					// no image file
					System.out.println("No image file to be published");
				}
			}
			
		}catch(Exception ex) {
			throw ex;
		}
		return menuImgFilePath;
	}
	
	private String extractQueryFile(Connection connection, String tmpQueryFilePath, String menuFilePath) throws Exception {
		String menuQueryFilePath = null;
		try {
			if(tmpQueryFilePath!=null && !tmpQueryFilePath.equals("")) {
				
				File checkFile = new File(filePath, tmpQueryFilePath + ".txt");
				if (checkFile.exists()) {
					menuQueryFilePath = byodUtil.createUniqueBackendId("MQF");
					// copy query file
					File dest = new File(filePath + menuFilePath, menuQueryFilePath + ".txt");
					try {
						FileUtils.copyFile(checkFile, dest);
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					// delete query file
					checkFile.delete();
			
				} else {
					// no query file
					System.out.println("No query file to be published");
				}
			}
			
		}catch(Exception ex) {
			throw ex;
		}
		return menuQueryFilePath;
	}
	
	private boolean checkTmpQueryFileExist(Connection connection, JSONObject groupCategoryInfo) throws Exception {
		String tmpQueryFilePath = "";
		boolean flag = false;
		try {
			if(groupCategoryInfo.has("tmp_query_file_path")) {
				tmpQueryFilePath = groupCategoryInfo.getString("tmp_query_file_path");
				File checkFile = new File(filePath, tmpQueryFilePath + ".txt");
				if (checkFile.exists()) {
					flag = true;
				} 
			}
			
		}catch(Exception ex) {
			throw ex;
		}
		return flag;
	}
	
	private Long updatePublishVersion(Connection connection, Long groupCategoryId, Long versionCount, String menuFilePath, String menuQueryFilePath, String menuImgFilePath ) throws Exception {
		String sqlStatement = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Long publishVersionId = null;

		try {		
			sqlStatement = "INSERT INTO publish_version (group_category_id, version_count, menu_file_path, menu_query_file_path, menu_img_file_path, publish_date) VALUES (?, ?, ?, ?, ?, GETDATE()); SELECT SCOPE_IDENTITY();";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, groupCategoryId);
			stmt.setLong(2, versionCount);
			stmt.setString(3, menuFilePath);
			stmt.setString(4, menuQueryFilePath);
			stmt.setString(5, menuImgFilePath);
			rs = stmt.executeQuery();
			if(rs.next()) {
				publishVersionId = rs.getLong(1);
			}
			
		}catch (Exception ex) {
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return publishVersionId;
	}
	
}
