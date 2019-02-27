package my.com.byod.order.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import my.com.byod.admin.util.DbConnectionUtil;
import my.com.byod.logger.Logger;
import my.com.byod.order.configuration.LanguageConfiguration;
import my.com.byod.order.util.AESEncryption;

@RestController
public class Order_RestController {

	@Value("${menu-path}")
	private String filePath;

	@Value("${upload-path}")
	private String imagePath;

	@Autowired
	private LanguageConfiguration languageConfiguration;

	@Autowired
	private Logger logger;

	@Autowired
	private DbConnectionUtil dbConnectionUtil;

	private static final String folName = "byodFE";

	@RequestMapping(value = "/order/getLanguagePack", method = { RequestMethod.POST })
	public String GetStoreName(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

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
			logger.writeError(e, folName);
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
			@RequestParam(value = "token", required = true) String token) {
		String storeLog = "--Get Store Data Begin--" + System.lineSeparator();
		storeLog += "Token: " + token + System.lineSeparator();

		JSONObject result = new JSONObject();
		Connection connection = null;
		String sqlStatement = null;
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		JSONArray categoryList = null;

		try {
			String decryptedTokenString = AESEncryption.decrypt(token);
			String[] tokenSplitArry = decryptedTokenString.split("\\|;");
			
			String brandId = tokenSplitArry[0];
			String storeId = tokenSplitArry[1];
			String tableId = tokenSplitArry[2];
			String checkNo = tokenSplitArry[3];
			
			storeLog += "Decrypted Data: " + System.lineSeparator();
			storeLog += "Brand ID: " + brandId + System.lineSeparator();
			storeLog += "Store ID: " + storeId + System.lineSeparator();
			storeLog += "Table ID: " + tableId + System.lineSeparator();
			storeLog += "Check No: " + checkNo + System.lineSeparator();
			
			connection = dbConnectionUtil.getConnection(Long.parseLong(brandId));
			sqlStatement = "SELECT a.group_category_id, a.store_name, a.store_currency, b.publish_version_id, c.menu_file_path FROM store a "
					+ "INNER JOIN group_category b ON b.id = a.group_category_id "
					+ "LEFT JOIN publish_version c ON c.id = b.publish_version_id "
					+ "WHERE a.is_publish = 1 AND a.id = ?";
			PreparedStatement ps1 = connection.prepareStatement(sqlStatement);
			ps1.setInt(1, Integer.parseInt(storeId));
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {
				if (rs1.getLong("publish_version_id") == 0) {
					// never publish menu before
					resultCode = "E02";
					resultMessage = "Store does not exist. Please re-scan QR.";
				} else {
					File checkFile = new File(filePath + brandId + "/" + rs1.getLong("group_category_id"),
							"/latest/menuFilePath.json");
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
							categoryList = jsonFile.getJSONArray("menuList");
						} finally {
							br.close();
						}

						result.put("tableId", tableId);
						result.put("checkNo", checkNo);
						result.put("storeName", rs1.getString("store_name"));
						result.put("priceTag", rs1.getString("store_currency"));
						result.put("imagePath", imagePath + brandId + "/");
						result.put("menuList", categoryList);

						resultCode = "00";
						resultMessage = "Success";
					} else {
						// unable to find menu file
						resultCode = "E02";
						resultMessage = "Store does not exist. Please re-scan QR.";
					}
				}
			} else {
				resultCode = "E02";
				resultMessage = "Store does not exist. Please re-scan QR.";
			}
			rs1.close();
			ps1.close();
		} catch (Exception e) {
			storeLog += "Error occurred. Refer error log." + System.lineSeparator();
			logger.writeError(e, folName);
		} finally {
			storeLog += "--Get Store Data End--" + System.lineSeparator();
			logger.writeActivity(storeLog, folName);
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

	@RequestMapping(value = "/order/sendOrder", method = { RequestMethod.POST })
	public String SendOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody String bodyData) {
		JSONObject result = new JSONObject();
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";

		try {
			JSONObject parsedData = new JSONObject(bodyData);
			JSONArray cartData = parsedData.getJSONArray("cartData");
			System.out.println(cartData);

			for (int x = 0; x < cartData.length(); x++) {
				System.out.println("--New Order--");
				JSONObject cartObj = cartData.getJSONObject(x);
				/*Sent Data*/
				int mainObjID = Integer.parseInt(cartObj.getString("id"));
				String objType = cartObj.getString("type");
				double totalPrice = Double.parseDouble(cartObj.getString("totalPrice"));
				int quantity = cartObj.getInt("quantity");
				/*Query Data*/
				double qTotalPrice = 0;

				if (objType.equalsIgnoreCase("0")) {
					// Alacarte
					System.out.println("Alacarte Order");
					JSONObject modifierData = cartObj.has("modifierData") ? cartObj.getJSONObject("modifierData")
							: null;
					if (modifierData != null) {
						JSONArray modifierList = modifierData.has("modifierGroupData")
								? modifierData.getJSONArray("modifierGroupData")
								: null;
						if (modifierList != null) {
							System.out.println("Has Modifier");
							for (int y = 0; y < modifierList.length(); y++) {
								JSONArray modifierObjList = modifierList.getJSONArray(y);
								System.out.println("Modifier Item " + y);
								for (int z = 0; z < modifierObjList.length(); z++) {
									JSONObject modifierObj = modifierObjList.getJSONObject(z);
									System.out.println("Modifier Data " + z + ": " + modifierObj.getJSONObject("selectedModifier"));
								}
							}
						}
					}
				} else if (objType.equalsIgnoreCase("1")) {
					// Combo
					System.out.println("Combo Order");
					JSONArray comboList = cartObj.has("comboData") ? cartObj.getJSONArray("comboData") : null;
					for (int y = 0; y < comboList.length(); y++) {
						JSONObject comboObjList = comboList.getJSONObject(y);
						System.out.println("Combo Data " + y + ": " + comboObjList);
					}
				} else {
					
				}
			}

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
}