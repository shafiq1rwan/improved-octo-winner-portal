package com.managepay.order.byod.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.managepay.order.byod.configuration.ErrorConfiguration;
import com.managepay.order.byod.configuration.LanguageConfiguration;

@RestController
public class Order_RestController {

	@Autowired
	private LanguageConfiguration languageConfiguration;
	
	@Autowired
	private ErrorConfiguration errorConfiguration;

	@Autowired
	DataSource dataSource;

	@RequestMapping(value = "/order/getLanguagePack", method = { RequestMethod.POST })
	public String GetStoreName(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String resultCode = "E1";
		String resultMessage = errorConfiguration.errorData().getE1();
		
		try {
			JSONArray localeDataList = new JSONArray();
			JSONObject localeData = new JSONObject();
			localeData.put("name", languageConfiguration.languagePackEN().getPackName());
			localeData.put("shortName", languageConfiguration.languagePackEN().getPackShortName());
			localeDataList.put(localeData);
			localeData = new JSONObject();
			localeData.put("name", languageConfiguration.languagePackCN().getPackName());
			localeData.put("shortName", languageConfiguration.languagePackCN().getPackShortName());
			localeDataList.put(localeData);

			JSONObject languageData = new JSONObject();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			languageData.put(languageConfiguration.languagePackEN().getPackShortName(),
					new JSONObject(ow.writeValueAsString(languageConfiguration.languagePackEN())));
			languageData.put(languageConfiguration.languagePackCN().getPackShortName(),
					new JSONObject(ow.writeValueAsString(languageConfiguration.languagePackCN())));

			result.put("localeData", localeDataList);
			result.put("languageData", languageData);
			
			resultCode = "00";
			resultMessage = "Success";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}

		return result.toString();
	}

	@RequestMapping(value = "/order/getStoreData", method = { RequestMethod.POST })
	public String getMenuData(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "storeId", required = true) String storeId,
			@RequestParam(value = "tableId", required = true) String tableId) {
		JSONObject result = new JSONObject();
		Connection connection = null;
		String sqlStatement = null;
		String resultCode = "E1";
		String resultMessage = errorConfiguration.errorData().getE1();

		try {
			JSONArray menuList = new JSONArray();

			connection = dataSource.getConnection();
			sqlStatement = "SELECT group_category_id, store_name, store_currency FROM store WHERE is_publish = 1 AND id = ?";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, Integer.parseInt(storeId));
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result.put("storeName", rs1.getString("store_name"));
				result.put("priceTag", rs1.getString("store_currency"));
				
				sqlStatement = "SELECT id, category_name, category_image_path FROM category WHERE group_category_id = ? ORDER BY category_sequence ASC";
				PreparedStatement ps2 = connection.prepareStatement(sqlStatement);
				ps2.setInt(1, rs1.getInt("group_category_id"));
				ResultSet rs2 = ps2.executeQuery();

				while (rs2.next()) {
					String categoryID = rs2.getString("id");
					String categoryName = rs2.getString("category_name");
					String categoryImagePath = rs2.getString("category_image_path");

					JSONArray itemList = new JSONArray();
					sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_description, mi.menu_item_type, mi.menu_item_image_path, mi.menu_item_base_price FROM category_menu_item cmi, menu_item mi WHERE cmi.is_active = 'True' AND cmi.category_id = ? AND cmi.menu_item_id = mi.id and mi.is_active = 'True' ORDER BY category_menu_item_sequence ASC";
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
						JSONArray alacarteModifierList = new JSONArray();
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
										sqlStatement = "SELECT mimg.modifier_group_id, mg.modifier_group_name FROM menu_item_modifier_group mimg, modifier_group mg WHERE mimg.menu_item_id = ? AND mg.id = mimg.modifier_group_id ORDER BY mimg.menu_item_modifier_group_sequence ASC";
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
						} else {
							sqlStatement = "SELECT mimg.modifier_group_id, mg.modifier_group_name FROM menu_item_modifier_group mimg, modifier_group mg WHERE mimg.menu_item_id = ? AND mg.id = mimg.modifier_group_id ORDER BY mimg.menu_item_modifier_group_sequence ASC";
							PreparedStatement ps4 = connection.prepareStatement(sqlStatement);
							ps4.setInt(1, Integer.parseInt(itemID));
							ResultSet rs4 = ps4.executeQuery();
							while (rs4.next()) {
								JSONObject modifierGroupData = new JSONObject();
								modifierGroupData.put("name", rs4.getString("modifier_group_name"));

								JSONArray modifierList = new JSONArray();
								sqlStatement = "SELECT id, menu_item_name, menu_item_base_price FROM menu_item WHERE modifier_group_id = ?";
								PreparedStatement ps5 = connection.prepareStatement(sqlStatement);
								ps5.setInt(1, Integer.parseInt(rs4.getString("modifier_group_id")));
								ResultSet rs5 = ps5.executeQuery();
								while (rs5.next()) {
									JSONObject modifierData = new JSONObject();
									modifierData.put("id", rs5.getString("id"));
									modifierData.put("name", rs5.getString("menu_item_name"));
									modifierData.put("price", rs5.getString("menu_item_base_price"));

									modifierList.put(modifierData);
								}
								rs5.close();
								ps5.close();

								modifierGroupData.put("modifierList", modifierList);

								alacarteModifierList.put(modifierGroupData);
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
						menuItem.put("modifierList", alacarteModifierList);
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
				
				result.put("menuList", menuList);
				
				resultCode = "00";
				resultMessage = "Success";
			} else {
				resultCode = "E2";
				resultMessage = errorConfiguration.errorData().getE2();
			}
			rs1.close();
			ps1.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
				}
			}
			
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}

		return result.toString();
	}
}