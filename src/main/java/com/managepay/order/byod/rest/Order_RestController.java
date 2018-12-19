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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.managepay.order.byod.configuration.LanguageConfiguration;
import com.managepay.order.byod.configuration.OrderConfiguration;

@RestController
public class Order_RestController {
	
	@Autowired
	private OrderConfiguration orderConfiguration;

	@Autowired
	private LanguageConfiguration languageConfiguration;

	@Autowired
	DataSource dataSource;

	@RequestMapping(value = "/order/getLanguagePack", method = { RequestMethod.POST })
	public String GetStoreName(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	@RequestMapping(value = "/order/getMenuData", method = { RequestMethod.POST })
	public String getMenuData(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		Connection connection = null;
		String sqlStatement = null;

		try {
			JSONArray menuList = new JSONArray();

			connection = dataSource.getConnection();
			sqlStatement = "SELECT id, category_name, category_image_path FROM category WHERE group_category_id = ? ORDER BY category_sequence ASC";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, 1);
			ResultSet rs1 = ps1.executeQuery();

			while (rs1.next()) {
				String categoryID = rs1.getString("id");
				String categoryName = rs1.getString("category_name");
				String categoryImagePath = rs1.getString("category_image_path");

				JSONArray itemList = new JSONArray();
				sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_description, mi.menu_item_type, mi.menu_item_image_path, mi.menu_item_base_price FROM category_menu_item cmi, menu_item mi WHERE cmi.is_active = 'True' AND cmi.category_id = ? AND cmi.menu_item_id = mi.id and mi.is_active = 'True' ORDER BY category_menu_item_sequence ASC";
				PreparedStatement ps2 = connection.prepareStatement(sqlStatement);
				ps2.setInt(1, Integer.parseInt(categoryID));
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					String itemID = rs2.getString("id");
					String itemName = rs2.getString("menu_item_name");
					String itemDesc = rs2.getString("menu_item_description");
					String itemType = rs2.getString("menu_item_type");
					String itemPath = rs2.getString("menu_item_image_path");
					String itemPrice = String.format("%.2f", rs2.getDouble("menu_item_base_price"));
					
					JSONArray comboList = new JSONArray();
					if (itemType.equals("0")) {
						sqlStatement = "SELECT cd.combo_detail_name, cid.combo_detail_id, cid.menu_item_id, cid.menu_item_group_id FROM combo_detail cd, combo_item_detail cid WHERE cd.menu_item_id = ? AND cd.id = cid.combo_detail_id ORDER BY cd.combo_detail_sequence ASC, cid.combo_item_detail_sequence ASC";
						PreparedStatement ps3 = connection.prepareStatement(sqlStatement);
						ps3.setInt(1, Integer.parseInt(itemID));
						ResultSet rs3 = ps3.executeQuery();
						
						JSONObject tierData = null;
						JSONArray tierItemList = null;
						String lastComboDetailID = null;
						while (rs3.next()) {
							String tierName = rs3.getString("combo_detail_name");
							String comboDetailID = rs3.getString("combo_detail_id");
							String tierMenuItemID = rs3.getString("menu_item_id");
							String tierItemGroupID = rs3.getString("menu_item_group_id");
							
							if (tierData == null) {
								tierData =  new JSONObject();
								tierData.put("name", tierName);
								tierItemList = new JSONArray();
							}
							
							if (lastComboDetailID == null) {
								lastComboDetailID = comboDetailID;
							} else {
								if (lastComboDetailID != comboDetailID) {
									tierData.put("itemList", tierItemList);
									comboList.put(tierData);
									tierData = null;
								}
							}
							
							if (tierMenuItemID != null) {
								sqlStatement = "SELECT id, menu_item_name, menu_item_image_path, menu_item_base_price FROM menu_item WHERE id = ?";
								PreparedStatement ps4 = connection.prepareStatement(sqlStatement);
								ps4.setInt(1, Integer.parseInt(tierMenuItemID));
								ResultSet rs4 = ps4.executeQuery();
								rs4.next();
								
								JSONObject menuItem = new JSONObject();
								menuItem.put("id", rs4.getString("id"));
								menuItem.put("name", rs4.getString("menu_item_name"));
								menuItem.put("path", rs4.getString("menu_item_image_path"));
								menuItem.put("price", rs4.getString("menu_item_base_price"));
								
								tierItemList.put(menuItem);
								
								rs4.close();
								ps4.close();
							} else {
								sqlStatement = "SELECT mi.id, mi.menu_item_name, mi.menu_item_image_path, mi.menu_item_base_price FROM menu_item mi, menu_item_group_menu_item migmi WHERE migmi.menu_item_group_id = ? AND mi.id = migmi.menu_item_id ORDER BY migmi.menu_item_group_menu_item_sequence ASC";
								PreparedStatement ps4 = connection.prepareStatement(sqlStatement);
								ps4.setInt(1, Integer.parseInt(tierItemGroupID));
								ResultSet rs4 = ps4.executeQuery();
								
								while (rs4.next()) {
									JSONObject menuItem = new JSONObject();
									menuItem.put("id", rs4.getString("id"));
									menuItem.put("name", rs4.getString("menu_item_name"));
									menuItem.put("path", rs4.getString("menu_item_image_path"));
									menuItem.put("price", rs4.getString("menu_item_base_price"));
									
									tierItemList.put(menuItem);
								}
								rs4.close();
								ps4.close();
							}
						}
						
						if (tierData != null) {
							tierData.put("itemList", tierItemList);
							comboList.put(tierData);
							tierData = null;
						}
						rs3.close();
						ps3.close();
					}
					
					JSONObject menuItem = new JSONObject();
					menuItem.put("id", itemID);
					menuItem.put("name", itemName);
					menuItem.put("description", itemDesc);
					menuItem.put("type", itemType);
					menuItem.put("path", itemPath);
					menuItem.put("comboList", comboList);
					menuItem.put("price", orderConfiguration.applicationData().getPriceTag() + itemPrice);

					itemList.put(menuItem);
				}
				rs2.close();
				ps2.close();

				JSONObject categoryItem = new JSONObject();
				categoryItem.put("id", categoryID);
				categoryItem.put("name", categoryName);
				categoryItem.put("path", categoryImagePath);
				categoryItem.put("itemList", itemList);

				menuList.put(categoryItem);
			}
			rs1.close();
			ps1.close();

			result.put("menuList", menuList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e1) {
				}
			}
		}

		return result.toString();
	}
}