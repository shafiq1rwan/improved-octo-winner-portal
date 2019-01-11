package my.com.byod.order.rest;

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

import my.com.byod.order.configuration.ErrorConfiguration;
import my.com.byod.order.configuration.LanguageConfiguration;

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
			connection = dataSource.getConnection();
			sqlStatement = "SELECT group_category_id, store_name, store_currency FROM store WHERE is_publish = 1 AND id = ?";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, Integer.parseInt(storeId));
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result.put("storeName", rs1.getString("store_name"));
				result.put("priceTag", rs1.getString("store_currency"));

				JSONArray categoryList = getCategoryListByGroupCategoryID(connection, rs1.getInt("group_category_id"));
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
				result.put("menuList", categoryList);

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
}