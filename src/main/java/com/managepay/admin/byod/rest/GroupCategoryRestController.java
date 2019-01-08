package com.managepay.admin.byod.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@RestController
@RequestMapping("/menu/group_category")
public class GroupCategoryRestController {
	
	@Value("${menu-path}")
	private String filePath;
	
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
	
	@GetMapping(value ="/publish_menu",produces = "application/json")
	public ResponseEntity<?> publishMenu( @RequestParam("group_category_id") Long groupCategoryId, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonGroupCategoryArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sqlStatement = "";
		JSONObject result = null;
		
		try {
			JSONArray menuList = new JSONArray();
			connection = dataSource.getConnection();
			sqlStatement = "SELECT id, category_name, category_image_path FROM category WHERE group_category_id = ? ORDER BY category_sequence ASC";
			PreparedStatement ps2 = connection.prepareStatement(sqlStatement);
			ps2.setLong(1, groupCategoryId);
			ResultSet rs2 = ps2.executeQuery();

			while (rs2.next()) {
				String categoryID = rs2.getString("id");
				String categoryName = rs2.getString("category_name");
				String categoryImagePath = rs2.getString("category_image_path");

				JSONArray itemList = new JSONArray();
				sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_description, mi.menu_item_type, mi.menu_item_image_path, mi.menu_item_base_price FROM category_menu_item cmi, menu_item mi WHERE cmi.category_id = ? AND cmi.menu_item_id = mi.id and mi.is_active = 'True' ORDER BY category_menu_item_sequence ASC";
				PreparedStatement ps3 = connection.prepareStatement(sqlStatement);
				ps3.setInt(1, Integer.parseInt(categoryID));
				ResultSet rs3 = ps3.executeQuery();
				while (rs3.next()) {
					String itemID = rs3.getString("id");
					String itemName = rs3.getString("menu_item_name");
					String itemDesc = rs3.getString("menu_item_description");
					String itemType = rs3.getString("menu_item_type");
					String itemPath = rs3.getString("menu_item_image_path");
					String itemPrice = String.format("%.2f", rs3.getDouble("menu_item_base_price"));

					JSONArray comboList = new JSONArray();
					if (itemType.equals("0")) {
						sqlStatement = "SELECT cd.combo_detail_name, cd.combo_detail_quantity, cid.combo_detail_id, cid.menu_item_id, cid.menu_item_group_id FROM combo_detail cd, combo_item_detail cid WHERE cd.menu_item_id = ? AND cd.id = cid.combo_detail_id ORDER BY cd.combo_detail_sequence ASC, cid.combo_item_detail_sequence ASC";
						PreparedStatement ps4 = connection.prepareStatement(sqlStatement);
						ps4.setInt(1, Integer.parseInt(itemID));
						ResultSet rs4 = ps4.executeQuery();

						JSONObject tierData = null;
						JSONArray tierItemList = null;
						String lastComboDetailID = null;
						while (rs4.next()) {
							String tierName = rs4.getString("combo_detail_name");
							String tierQuantity = rs4.getString("combo_detail_quantity");
							String comboDetailID = rs4.getString("combo_detail_id");
							String tierMenuItemID = rs4.getString("menu_item_id");
							String tierItemGroupID = rs4.getString("menu_item_group_id");

							if (tierData == null) {
								tierData = new JSONObject();
								tierData.put("name", tierName);
								tierData.put("quantity", tierQuantity);
								tierItemList = new JSONArray();
							}

							if (lastComboDetailID == null) {
								lastComboDetailID = comboDetailID;
							} else {
								if (!lastComboDetailID.equalsIgnoreCase(comboDetailID)) {
									lastComboDetailID = comboDetailID;
									tierData.put("itemList", tierItemList);
									comboList.put(tierData);
									tierData = null;
								}
							}

							if (tierMenuItemID != null) {
								sqlStatement = "SELECT id, menu_item_name, menu_item_image_path, menu_item_base_price FROM menu_item WHERE id = ?";
								PreparedStatement ps5 = connection.prepareStatement(sqlStatement);
								ps5.setInt(1, Integer.parseInt(tierMenuItemID));
								ResultSet rs5 = ps5.executeQuery();
								rs5.next();

								JSONObject menuItem = new JSONObject();
								menuItem.put("id", rs5.getString("id"));
								menuItem.put("name", rs5.getString("menu_item_name"));
								menuItem.put("path", rs5.getString("menu_item_image_path"));
								menuItem.put("price", rs5.getString("menu_item_base_price"));

								JSONArray modifierGroupList = new JSONArray();
								sqlStatement = "SELECT mimg.modifier_group_id, mg.modifier_group_name FROM menu_item_modifier_group mimg, modifier_group mg WHERE mimg.menu_item_id = ? ORDER BY mimg.menu_item_modifier_group_sequence ASC";
								PreparedStatement ps6 = connection.prepareStatement(sqlStatement);
								ps6.setInt(1, Integer.parseInt(rs5.getString("id")));
								ResultSet rs6 = ps6.executeQuery();
								while (rs6.next()) {
									JSONObject modifierGroupData = new JSONObject();
									modifierGroupData.put("name", rs6.getString("modifier_group_name"));

									JSONArray modifierList = new JSONArray();
									sqlStatement = "SELECT id, menu_item_name, menu_item_base_price FROM menu_item WHERE modifier_group_id = ?";
									PreparedStatement ps7 = connection.prepareStatement(sqlStatement);
									ps7.setInt(1, Integer.parseInt(rs6.getString("modifier_group_id")));
									ResultSet rs7 = ps7.executeQuery();
									while (rs7.next()) {
										JSONObject modifierData = new JSONObject();
										modifierData.put("id", rs7.getString("id"));
										modifierData.put("name", rs7.getString("menu_item_name"));
										modifierData.put("price", rs7.getString("menu_item_base_price"));

										modifierList.put(modifierData);
									}
									rs7.close();
									ps7.close();

									modifierGroupData.put("modifierList", modifierList);

									modifierGroupList.put(modifierGroupData);
								}
								rs6.close();
								ps6.close();

								menuItem.put("modifierGroupList", modifierGroupList);

								tierItemList.put(menuItem);

								rs5.close();
								ps5.close();
							} else {
								sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_image_path, mi.menu_item_base_price FROM menu_item mi, menu_item_group_menu_item migmi WHERE migmi.menu_item_group_id = ? AND mi.id = migmi.menu_item_id ORDER BY migmi.menu_item_group_menu_item_sequence ASC";
								PreparedStatement ps5 = connection.prepareStatement(sqlStatement);
								ps5.setInt(1, Integer.parseInt(tierItemGroupID));
								ResultSet rs5 = ps5.executeQuery();

								while (rs5.next()) {
									JSONObject menuItem = new JSONObject();
									menuItem.put("id", rs5.getString("id"));
									menuItem.put("name", rs5.getString("menu_item_name"));
									menuItem.put("path", rs5.getString("menu_item_image_path"));
									menuItem.put("price", rs5.getString("menu_item_base_price"));

									JSONArray modifierGroupList = new JSONArray();
									sqlStatement = "SELECT mimg.modifier_group_id, mg.modifier_group_name FROM menu_item_modifier_group mimg, modifier_group mg WHERE mimg.menu_item_id = ? ORDER BY mimg.menu_item_modifier_group_sequence ASC";
									PreparedStatement ps6 = connection.prepareStatement(sqlStatement);
									ps6.setInt(1, Integer.parseInt(rs5.getString("id")));
									ResultSet rs6 = ps6.executeQuery();
									while (rs6.next()) {
										JSONObject modifierGroupData = new JSONObject();
										modifierGroupData.put("name", rs6.getString("modifier_group_name"));

										JSONArray modifierList = new JSONArray();
										sqlStatement = "SELECT id, menu_item_name, menu_item_base_price FROM menu_item WHERE modifier_group_id = ?";
										PreparedStatement ps7 = connection.prepareStatement(sqlStatement);
										ps7.setInt(1, Integer.parseInt(rs6.getString("modifier_group_id")));
										ResultSet rs7 = ps7.executeQuery();
										while (rs7.next()) {
											JSONObject modifierData = new JSONObject();
											modifierData.put("id", rs7.getString("id"));
											modifierData.put("name", rs7.getString("menu_item_name"));
											modifierData.put("price", rs7.getString("menu_item_base_price"));

											modifierList.put(modifierData);
										}
										rs7.close();
										ps7.close();

										modifierGroupData.put("modifierList", modifierList);

										modifierGroupList.put(modifierGroupData);
									}
									rs6.close();
									ps6.close();

									menuItem.put("modifierGroupList", modifierGroupList);

									tierItemList.put(menuItem);
								}
								rs5.close();
								ps5.close();
							}
						}

						if (tierData != null) {
							tierData.put("itemList", tierItemList);
							comboList.put(tierData);
							tierData = null;
						}
						rs4.close();
						ps4.close();
					}

					JSONObject menuItem = new JSONObject();
					menuItem.put("id", itemID);
					menuItem.put("name", itemName);
					menuItem.put("description", itemDesc);
					menuItem.put("type", itemType);
					menuItem.put("path", itemPath);
					menuItem.put("comboList", comboList);
					menuItem.put("price", itemPrice);

					itemList.put(menuItem);
				}
				rs3.close();
				ps3.close();

				JSONObject categoryItem = new JSONObject();
				categoryItem.put("id", categoryID);
				categoryItem.put("name", categoryName);
				categoryItem.put("path", categoryImagePath);
				categoryItem.put("itemList", itemList);

				menuList.put(categoryItem);
			}
			
			result = new JSONObject();
			result.put("menuList", menuList);
			
			// write to json file
			String fileName = "";
			// pending save file name method
			try {
				boolean checker = false;
				fileName = "test.json";
				File checkdir = new File(filePath);
				checkdir.mkdirs();

				do {
					File checkFile = new File(filePath, fileName);
					if (checkFile.exists()) {
						checker = true;
						fileName = "testtest.json";
					} else {
						checker = false;
					}
				} while (checker);
				
				File file = new File(filePath, fileName);
				Writer output = new BufferedWriter(new FileWriter(file));
                output.write(result.toString());
                output.close();
                
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			int count = 1;
			sqlStatement = "UPDATE group_category SET file_path = ?, last_publish_date = GETDATE() WHERE id = ? ";
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
	
	

}
