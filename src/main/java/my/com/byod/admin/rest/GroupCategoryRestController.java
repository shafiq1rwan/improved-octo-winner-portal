package my.com.byod.admin.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

@RestController
@RequestMapping("/menu/group_category")
public class GroupCategoryRestController {
	
	@Value("${menu-path}")
	private String filePath;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ByodUtil byodUtil;
	
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
	
	@GetMapping(value ="/publish_menu",produces = "application/json")
	public ResponseEntity<?> publishMenu( @RequestParam("group_category_id") int groupCategoryId, HttpServletRequest request, HttpServletResponse response) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlStatement = "";
		JSONObject result = null;
		
		try {
			connection = dataSource.getConnection();
			
			JSONArray categoryList = getCategoryListByGroupCategoryID(connection, groupCategoryId);
			for (int categoryIndex = 0; categoryIndex < categoryList.length(); categoryIndex++) {
				JSONArray itemList = getMenuItemListByCategoryID(connection, Integer.parseInt(categoryList.getJSONObject(categoryIndex).getString("id")));
				for (int itemIndex = 0; itemIndex < itemList.length(); itemIndex++) {
					JSONArray comboList = new JSONArray();
					JSONArray alacarteModifierList = new JSONArray();
					if (itemList.getJSONObject(itemIndex).getString("type").equals("0")) {
						comboList = getComboDetailListByComboOverheadID(connection, Integer.parseInt(itemList.getJSONObject(itemIndex).getString("id")));
						for (int comboIndex = 0; comboIndex < comboList.length(); comboIndex++) {
							JSONArray comboDetailList = getComboDetailItemListByComboDetailID(connection, Integer.parseInt(comboList.getJSONObject(comboIndex).getString("id")));
							
							JSONArray tierItemList = new JSONArray();
							for (int comboDetailIndex = 0; comboDetailIndex < comboDetailList.length(); comboDetailIndex++) {
								if (!comboDetailList.getJSONObject(comboDetailIndex).isNull("menuItemID") && comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemID") != null) {
									JSONObject menuItem = getMenuItemDataByMenuItemID(connection, Integer.parseInt(comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemID")));
									menuItem.put("modifierGroupList", getModifierListByMenuItemID(connection, Integer.parseInt(menuItem.getString("id"))));
									tierItemList.put(menuItem);
								} else {
									tierItemList = getMenuItemListByItemGroupID(connection, Integer.parseInt(comboDetailList.getJSONObject(comboDetailIndex).getString("menuItemGroupID")));
									for (int tierIndex = 0; tierIndex < tierItemList.length(); tierIndex++) {
										tierItemList.getJSONObject(tierIndex).put("modifierGroupList", getModifierListByMenuItemID(connection, Integer.parseInt(tierItemList.getJSONObject(tierIndex).getString("id"))));
									}
								}
							}
							comboList.getJSONObject(comboIndex).put("itemList", tierItemList);
						}
					} else if (itemList.getJSONObject(itemIndex).getString("type").equals("1")) {
						alacarteModifierList = getModifierListByMenuItemID(connection, Integer.parseInt(itemList.getJSONObject(itemIndex).getString("id")));
					}
					itemList.getJSONObject(itemIndex).put("comboList", comboList);
					itemList.getJSONObject(itemIndex).put("modifierList", alacarteModifierList);
				}
				categoryList.getJSONObject(categoryIndex).put("itemList", itemList);
			}
			result = new JSONObject();
			result.put("menuList", categoryList);
			
			// write to json file
			String fileName = byodUtil.createUniqueBackendId("MF");
			try {
				boolean checker = false;
				File checkdir = new File(filePath);
				checkdir.mkdirs();

				do {
					File checkFile = new File(filePath, fileName + ".json");
					if (checkFile.exists()) {
						checker = true;
						fileName = byodUtil.createUniqueBackendId("MF");;
					} else {
						checker = false;
					}
				} while (checker);
				
				File file = new File(filePath, fileName + ".json");
				Writer output = new BufferedWriter(new FileWriter(file));
                output.write(result.toString());
                output.close();
                
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			int count = 1;
			sqlStatement = "UPDATE group_category SET menu_file_path = ?, last_publish_date = GETDATE() WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setString(count++, fileName);
			stmt.setLong(count++, groupCategoryId);
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
	

	private int checkDuplicateGroupCategoryName(String groupCategoryName) {
		return jdbcTemplate.queryForObject("SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ?", new Object[] {groupCategoryName}, Integer.class);
	}
	
	private int checkDuplicateGroupCategoryNameById(String groupCategoryName, Long id) {
		System.out.println(groupCategoryName);
		return jdbcTemplate.queryForObject("SELECT COUNT(group_category_name) FROM group_category WHERE group_category_name = ? AND id = ?", new Object[] {groupCategoryName, id}, Integer.class);

	}
	
	private JSONArray getCategoryListByGroupCategoryID(Connection connection, int groupCategoryID) throws Exception {
		JSONArray categoryList = new JSONArray();
		
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			sqlStatement = "SELECT id, category_name, category_image_path FROM category WHERE group_category_id = ? AND is_active = 1 ORDER BY category_sequence ASC";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, groupCategoryID);
			rs1 = ps1.executeQuery();

			while (rs1.next()) {
				JSONObject categoryItem = new JSONObject();
				categoryItem.put("id",  rs1.getString("id"));
				categoryItem.put("name", rs1.getString("category_name"));
				categoryItem.put("path",  rs1.getString("category_image_path"));

				categoryList.put(categoryItem);
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
	
	private JSONArray getMenuItemListByCategoryID(Connection connection, int categoryID) throws Exception {
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
	
	private JSONObject getMenuItemDataByMenuItemID(Connection connection, int menuItemID) throws Exception {
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
	
	private JSONArray getMenuItemListByItemGroupID(Connection connection, int itemGroupID) throws Exception {
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
	
	public boolean logActionToFile(Connection connection, String query, String[] parameters, int groupCategoryId) throws Exception {
		
		String sqlStatement = "";
		String tmpQueryFilePath = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
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
			
			sqlStatement = "SELECT tmp_query_file_path FROM group_category WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setInt(1, groupCategoryId);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				tmpQueryFilePath = rs.getString("tmp_query_file_path");
				if(tmpQueryFilePath == null) {
					stmt.close();
					tmpQueryFilePath = byodUtil.createUniqueBackendId("TQF");
					sqlStatement = "UPDATE group_category SET tmp_query_file_path = ? WHERE id = ? ";
					stmt = connection.prepareStatement(sqlStatement);
					stmt.setString(1, tmpQueryFilePath);
					stmt.setInt(2, groupCategoryId);
					stmt.executeUpdate();
				}
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
		return true;
	}
	
	/*public boolean logActionToAllFiles(Connection connection, String query, String[] parameters) throws Exception {
		
		String sqlStatement = "";
		String tmpQueryFilePath = "";
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		ResultSet rs = null;
		
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
			
			sqlStatement = "SELECT tmp_query_file_path FROM group_category ";
			stmt = connection.prepareStatement(sqlStatement);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				tmpQueryFilePath = rs.getString("tmp_query_file_path");
				if(tmpQueryFilePath == null) {
					stmt.close();
					tmpQueryFilePath = byodUtil.createUniqueBackendId("TQF");
					sqlStatement = "UPDATE group_category SET tmp_query_file_path = ? WHERE id = ? ";
					stmt = connection.prepareStatement(sqlStatement);
					stmt.setString(1, tmpQueryFilePath);
					stmt.setInt(2, groupCategoryId);
					stmt.executeUpdate();
				}
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
		return true;
	}*/
}
