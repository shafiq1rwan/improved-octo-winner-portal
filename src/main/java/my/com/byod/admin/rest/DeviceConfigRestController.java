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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/api/device")
public class DeviceConfigRestController {
	
	@Value("${menu-path}")
	private String filePath;
	
	@Value("${get-menu-path}")
	private String displayFilePath;
	
	@Value("${upload-path}")
	private String imagePath;
	
	@Value("${get-upload-path}")
	private String displayImagePath;
	
	@Value("${byod-cloud-url}")
	private String byodUrl;
	
	@Autowired
	private SettingRestController settingRestController;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@Autowired
	DataSource dataSource;
	
	@RequestMapping(value = "/activation", method = { RequestMethod.POST })
	public String activation(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "activationId", required = true) String activationId, 
			@RequestParam(value = "activationKey", required = true) String activationKey,
			@RequestParam(value = "macAddress", required = true) String macAddress,
			@RequestParam(value = "brandId", required = true) Long brandId,
			@RequestParam(value = "type", required = true) Long type) {

		// device activation api
		
		// parameter "type"
		// 1 - ECPOS
		// 2 - BYOD
		// 3 - KIOSK
		
		JSONObject result = new JSONObject();
		JSONObject deviceInfo = null;
		Connection connection = null;
		JSONObject menuInfo = null;
		String menuFile = null;
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		System.out.println("activationId:"+ activationId);
		System.out.println("activationKey:"+ activationKey);
		System.out.println("type:"+ type);
		try {
			if(checkBrandExist(brandId)) {
				connection = dbConnectionUtil.getConnection(brandId);
				connection.setAutoCommit(false);
				deviceInfo = verifyActivation(connection, activationId, activationKey, type);
				if(deviceInfo==null) {
					resultCode = "E02";
					resultMessage = "Invalid activation info";
				}		
				else if(deviceInfo.getLong("statusLookupId")==2) {
					// already activated
					resultCode = "E03";
					resultMessage = "Activation info is already being used.";
				}
				else if(deviceInfo.getLong("statusLookupId")==3) {
					// already terminated
					resultCode = "E04";
					resultMessage = "Activation info is already terminated.";
				}
				else if(deviceInfo.getLong("storeStatus")==0) {
					// store status is not active
					resultCode = "E06";
					resultMessage = "Store info is not published.";
				}
				else if(activateDevice(connection, deviceInfo.getLong("id"), macAddress, deviceInfo.getLong("groupCategoryId"))) {
					// successful activation
					
					// ecpos retrieve extra info
					if(type==1) {
						JSONArray ecposStaffInfo = getEcposStaffInfo(connection, deviceInfo.getLong("refId"));
						JSONArray ecposTableSetting = getEcposTableSetting(connection, deviceInfo.getLong("refId"));
						JSONArray ecposStaffRole = getEcposStaffRole(connection);					
						
						JSONObject ecposSetting = new JSONObject();
						ecposSetting.put("deviceName", deviceInfo.getString("deviceName"));
						ecposSetting.put("deviceId", deviceInfo.getLong("id"));
											
						if(ecposStaffInfo.length()==0) {
							resultCode = "E07";
							resultMessage = "Please create at least one staff login before activation.";
							throw new IllegalArgumentException("There is no staff login info.");
						}		
						result.put("staffInfo", ecposStaffInfo);
						result.put("tableSetting", ecposTableSetting);
						result.put("staffRole", ecposStaffRole);
						result.put("ecposSetting", ecposSetting);
					}
					
					connection.commit();
					connection.setAutoCommit(true);
					
					JSONObject storeInfo = getStoreInfo(connection, deviceInfo.getLong("refId"), brandId);
					result.put("storeInfo", storeInfo);
					
					menuInfo = getLatestMenuFile(connection, deviceInfo.getLong("refId"));
					if(menuInfo!=null) {
						menuFile = menuInfo.getString("menuFilePath");
						Long groupCategoryId = menuInfo.getLong("groupCategoryId");
						String url = byodUrl + displayFilePath + brandId + "/" + groupCategoryId;
						String menuFilePath = url + "/latest/menuFilePath.json";
						String imageFilePath = url + "/latest/image.zip";	
						String queryFilePath = url + "/latest/query.txt";
						String queryFilePath2 = url + "/latest/queryMySql.txt";
						result.put("menuFilePath", menuFilePath);
						result.put("imageFilePath", imageFilePath);
						result.put("queryFilePath", queryFilePath);
						result.put("queryMySqlFilePath", queryFilePath2);
						result.put("versionCount", menuInfo.getLong("versionCount"));
						resultCode = "00";
						resultMessage = "Successful device activation and menu synchronization.";
					}
					else {
						result.put("versionCount", Long.valueOf("0"));
						resultCode = "01";
						resultMessage = "Successful device activation. Please proceed to publish menu.";
					}
				}
				else {
					resultCode = "E05";
					resultMessage = "Failed to activate device.";
				}	
			}
			else {			
				resultCode = "E02";
				resultMessage = "Invalid activation info";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (Exception e) {
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch(Exception ex) {
				}
			}
			
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		System.out.println(result);
		return result.toString();
	}
	
	@RequestMapping(value = "/syncMenu", method = { RequestMethod.POST })
	public String getMenuData(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "storeId", required = true) Long storeId, 
			@RequestParam(value = "versionCount", required = true) Long versionCount,
			@RequestParam(value = "activationId", required = true) String activationId,
			@RequestParam(value = "timeStamp", required = true) String timeStamp,
			@RequestParam(value = "authToken", required = true) String authToken,
			@RequestParam(value = "brandId", required = true) Long brandId) {
		
		JSONObject result = new JSONObject();
		JSONObject deviceInfo = null;
		Connection connection = null;
		JSONObject menuInfo = null;
		String menuFile = null;
		String secureHash = "";
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		
		try {
			connection = dbConnectionUtil.getConnection(brandId);
			deviceInfo = getDeviceInfoByActivationId(connection, activationId, storeId);
			if(deviceInfo!=null) {
				secureHash = byodUtil.genSecureHash("SHA-256", activationId.concat(deviceInfo.getString("mac_address")).concat(timeStamp));
			}
				
			System.out.println("authToken:" + authToken);
			System.out.println("secureHash:" + secureHash);
			
			if(deviceInfo!=null && deviceInfo.getLong("statusLookupId")!=2) {
				// not active
				resultCode = "E02";
				resultMessage = "Activation info is already deactivated.";
			}
			else if(!authToken.equals(secureHash)) {
				resultCode = "E03";
				resultMessage = "Invalid authentication token.";
			}
			else if(deviceInfo.getLong("storeStatus")==0) {
				// store status is not active
				resultCode = "E06";
				resultMessage = "Current store is not published at cloud.";
			}
			else if(versionCount==0) {
				// first time get menu
				menuInfo = getLatestMenuFile(connection, storeId);
				if(menuInfo!=null) {
					menuFile = menuInfo.getString("menuFilePath");
					Long groupCategoryId = menuInfo.getLong("groupCategoryId");
					String url = byodUrl + displayFilePath + brandId + "/" + groupCategoryId;
					String menuFilePath = url + "/latest/menuFilePath.json";
					String imageFilePath = url + "/latest/image.zip";	
					String queryFilePath = url + "/latest/query.txt";
					String queryFilePath2 = url + "/latest/queryMySql.txt";
					result.put("menuFilePath", menuFilePath); 
					result.put("imageFilePath", imageFilePath);
					result.put("queryFilePath", queryFilePath);
					result.put("queryMySqlFilePath", queryFilePath2);
					result.put("versionCount", menuInfo.getLong("versionCount"));
					resultCode = "00";
					resultMessage = "Successful get full menu.";
				}
				else {
					resultCode = "E04";
					resultMessage = "Never publish menu before";
				}
			}
			else {
				// sync menu
				JSONArray versionArray = getVersionUpdates(connection, brandId, versionCount, storeId, deviceInfo.getLong("deviceType"));			
				if(versionArray.length()!=0) {
					result.put("versionSync", versionArray);
					resultCode = "01";
					resultMessage = "Successful menu sync.";
				}
				else {
					// up-to-date
					resultCode = "E05";
					resultMessage = "Current menu is the latest version.";
				}			
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch(Exception ex) {
				}
			}
			
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		System.out.println(result);
		return result.toString();
	}
	
	@RequestMapping(value = "/syncStore", method = { RequestMethod.POST })
	public String getStoreData(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "storeId", required = true) Long storeId, 
			@RequestParam(value = "activationId", required = true) String activationId,
			@RequestParam(value = "timeStamp", required = true) String timeStamp,
			@RequestParam(value = "authToken", required = true) String authToken,
			@RequestParam(value = "brandId", required = true) Long brandId) {
		
		JSONObject result = new JSONObject();
		JSONObject deviceInfo = null;
		Connection connection = null;
		String secureHash = "";
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		
		try {
			connection = dbConnectionUtil.getConnection(brandId);
			deviceInfo = getDeviceInfoByActivationId(connection, activationId, storeId);
			if(deviceInfo!=null) {
				secureHash = byodUtil.genSecureHash("SHA-256", activationId.concat(deviceInfo.getString("mac_address")).concat(timeStamp));
			}
				
			System.out.println("authToken:" + authToken);
			System.out.println("secureHash:" + secureHash);
			
			if(deviceInfo!=null && deviceInfo.getLong("statusLookupId")!=2) {
				// not active
				resultCode = "E02";
				resultMessage = "Activation info is already deactivated.";
			}
			else if(!authToken.equals(secureHash)) {
				resultCode = "E03";
				resultMessage = "Invalid authentication token.";
			}
			else if(deviceInfo.getLong("storeStatus")==0) {
				// store status is not active
				resultCode = "E04";
				resultMessage = "Current store is not published at cloud.";
			}
			else {
				// sync store
				JSONObject storeInfo = getStoreInfo(connection, storeId, brandId);
				
				if(deviceInfo.getLong("deviceType")==1) {
					// ecpos
					JSONArray ecposStaffInfo = getEcposStaffInfo(connection, storeId);
					JSONArray ecposTableSetting = getEcposTableSetting(connection, storeId);
					JSONArray ecposStaffRole = getEcposStaffRole(connection);
					
					JSONObject ecposSetting = new JSONObject();
					ecposSetting.put("deviceName", deviceInfo.getString("deviceName"));
					ecposSetting.put("deviceId", deviceInfo.getLong("id"));
					
					result.put("staffInfo", ecposStaffInfo);
					result.put("tableSetting", ecposTableSetting);
					result.put("staffRole", ecposStaffRole);
					result.put("ecposSetting", ecposSetting);
				}
				else if(deviceInfo.getLong("deviceType")==3) {
					// kiosk
					result.put("setting", settingRestController.getBrandSetting(brandId, connection));
				}
				
				result.put("storeInfo", storeInfo);				
				resultCode = "00";
				resultMessage = "Successful store synchronization.";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch(Exception ex) {
				}
			}
			
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		System.out.println(result);
		return result.toString();
	}
	
	@RequestMapping(value = "/syncTransaction", method = { RequestMethod.POST })
	public String syncOrderToCloud(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "storeId", required = true) Long storeId, 
			@RequestParam(value = "activationId", required = true) String activationId,
			@RequestParam(value = "timeStamp", required = true) String timeStamp,
			@RequestParam(value = "authToken", required = true) String authToken,
			@RequestParam(value = "brandId", required = true) Long brandId, 
			@RequestParam(value = "data", required = true) String data) {
		
		JSONObject result = new JSONObject();
		JSONObject deviceInfo = null;
		String secureHash = "";
		String resultCode = "E01";
		String resultMessage = "Server error. Please try again later.";
		Connection connection = null;
	
		try {
			connection = dbConnectionUtil.getConnection(brandId);
			deviceInfo = getDeviceInfoByActivationId(connection, activationId, storeId);
			if(deviceInfo!=null) {
				secureHash = byodUtil.genSecureHash("SHA-256", activationId.concat(deviceInfo.getString("mac_address")).concat(timeStamp).concat(data));
			}

			System.out.println("authToken:" + authToken);
			System.out.println("secureHash:" + secureHash);
			
			if(deviceInfo!=null && deviceInfo.getLong("statusLookupId")!=2) {
				// not active
				resultCode = "E02";
				resultMessage = "Activation info is already deactivated.";
			}
			else if(!authToken.equals(secureHash)) {
				resultCode = "E03";
				resultMessage = "Invalid authentication token.";
			}
			else if(deviceInfo.getLong("storeStatus")==0) {
				// store status is not active
				resultCode = "E04";
				resultMessage = "Current store is not published at cloud.";
			}
			else {				
				connection.setAutoCommit(false);
				
				JSONObject jsonData = new JSONObject(data);
				
				JSONArray checks = null;
				JSONArray checkDetails = null;
				JSONArray transactions = null;
				JSONArray settlements = null;
				
				if(jsonData.has("check")) {
					checks = jsonData.optJSONArray("check");
				} else {
					throw new Exception("check not exist");
				}
				
				if(jsonData.has("checkDetail")) {
					checkDetails = jsonData.optJSONArray("checkDetail");
				} else {
					throw new Exception("checkDetail not exist");
				}
				
				if(jsonData.has("transaction")) {
					transactions = jsonData.optJSONArray("transaction");	
				} else {
					throw new Exception("transaction not exist");
				}
				
				if(jsonData.has("settlement")) {
					settlements = jsonData.optJSONArray("settlement");
				} else {
					throw new Exception("settlement not exist");
				}

				boolean checkDetailFlag = false;
				boolean checkFlag = false;
				boolean settlementFlag = false;
				boolean transactionFlag = false;

				if(checks.length() != 0) {
					checkFlag = performCheckOperations(connection, checks, storeId);
				} else {
					checkFlag = true;
				}

				if(checkDetails.length() != 0) {
					checkDetailFlag = performCheckDetailOperations(connection, checkDetails, storeId);
				} else {
					checkDetailFlag = true;
				}

				if(transactions.length() != 0) {
					transactionFlag = performTransactionOperations(connection, transactions, storeId);
					System.out.println("transaction JSON: "+transactions.toString());
				} else {
					transactionFlag = true;
				}

				if(settlements.length() != 0) {
					settlementFlag = performSettlementOperations(connection, settlements, storeId);
				} else {
					settlementFlag = true;
				}
				
				if(checkFlag && checkDetailFlag && settlementFlag && transactionFlag) {
					connection.commit();
					resultCode = "00";
					resultMessage = "Check, transaction and settlement data have been sync to cloud.";	
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e) {
				ex.printStackTrace();
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch(Exception ex) {
				}
			}
			try {
				result.put("resultCode", resultCode);
				result.put("resultMessage", resultMessage);
			} catch (Exception e) {
			}
		}
		System.out.println(result);
		return result.toString();
	}
	
	//not yet tested
	private boolean performCheckDetailOperations(Connection connection, JSONArray checkDetails, Long storeId) throws Exception {
		String searchExistingCheckDetailSqlStatement = null;
		String insertionSqlStatement = null;
		String updateSqlStatement = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		
		try {	
			SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			insertionSqlStatement = "INSERT INTO check_detail (store_id, check_detail_id, check_id, check_number, device_type, parent_check_detail_id, "
					+ "menu_item_id, menu_item_code, menu_item_name, menu_item_price, quantity, total_amount, "
					+ "check_detail_status, transaction_id, created_date, updated_date) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			updateSqlStatement = "UPDATE check_detail SET check_id = ?, check_number = ?, device_type = ?, parent_check_detail_id =?, "
					+ "menu_item_id = ?, menu_item_code = ?, menu_item_name = ?, menu_item_price = ?, quantity = ?, total_amount = ?, "
					+ "check_detail_status = ?, transaction_id = ?, created_date = ?, updated_date = ? WHERE check_detail_id = ? and store_id = ?";

			searchExistingCheckDetailSqlStatement = "SELECT * FROM check_detail WHERE check_detail_id = ? and store_id = ?";

			for(int a = 0; a < checkDetails.length(); a++) {
				JSONObject obj = checkDetails.getJSONObject(a);

				PreparedStatement ps1 = connection.prepareStatement(searchExistingCheckDetailSqlStatement);
				ps1.setLong(1, obj.getLong("check_detail_id"));
				ps1.setLong(2, storeId);
				ResultSet rs = ps1.executeQuery();
				
				//detect existing, update
				if(rs.next()) {
					stmt =  connection.prepareStatement(updateSqlStatement);

					stmt.setLong(1, obj.getLong("check_id"));
					stmt.setLong(2, obj.getLong("check_number"));
					stmt.setLong(3, obj.getLong("device_type"));	
					
					if(obj.isNull("parent_check_detail_id")) stmt.setNull(4, java.sql.Types.BIGINT);
					else stmt.setLong(4, obj.getLong("parent_check_detail_id"));
			
					stmt.setLong(5, obj.getLong("menu_item_id"));
					stmt.setString(6, obj.getString("menu_item_code"));
					stmt.setString(7, obj.getString("menu_item_name"));
					stmt.setBigDecimal(8, BigDecimal.valueOf(obj.getDouble("menu_item_price")));
					stmt.setInt(9, obj.getInt("quantity"));
					stmt.setBigDecimal(10, BigDecimal.valueOf(obj.getDouble("total_amount")));
					stmt.setLong(11, obj.getLong("check_detail_status"));	
					
					if(obj.isNull("transaction_id")) stmt.setNull(12, java.sql.Types.BIGINT);
					else stmt.setLong(12, obj.getLong("transaction_id"));
					
					stmt.setTimestamp(13, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));
					
					if(obj.isNull("updated_date")) stmt.setNull(14, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(14, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));
					
					stmt.setLong(15, obj.getLong("check_detail_id"));
					stmt.setLong(16, storeId);
					
					stmt.executeUpdate();
					stmt.close();
				} else {		
					stmt =  connection.prepareStatement(insertionSqlStatement);
		
					stmt.setLong(1, storeId);
					stmt.setLong(2, obj.getLong("check_detail_id"));
					stmt.setLong(3, obj.getLong("check_id"));
					stmt.setLong(4, obj.getLong("check_number"));		
					stmt.setLong(5, obj.getLong("device_type"));	
					
					if(obj.isNull("parent_check_detail_id")) stmt.setNull(6, java.sql.Types.BIGINT);
					else stmt.setLong(6, obj.getLong("parent_check_detail_id"));

					stmt.setLong(7, obj.getLong("menu_item_id"));			
					stmt.setString(8, obj.getString("menu_item_code"));
					stmt.setString(9, obj.getString("menu_item_name"));
					stmt.setBigDecimal(10, BigDecimal.valueOf(obj.getDouble("menu_item_price")));	
					stmt.setInt(11, obj.getInt("quantity"));
					stmt.setBigDecimal(12, BigDecimal.valueOf(obj.getDouble("total_amount")));
					stmt.setLong(13, obj.getLong("check_detail_status"));		
					
					if(obj.isNull("transaction_id")) stmt.setNull(14, java.sql.Types.BIGINT);
					else stmt.setLong(14, obj.getLong("transaction_id"));
					
					stmt.setTimestamp(15, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));
					
					if(obj.isNull("updated_date")) stmt.setNull(16, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(16, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));

					stmt.executeUpdate();
					stmt.close();
				}
				ps1.close();
				rs.close();
			}
			flag = true;
		} catch(Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
	
	//Remember
	//not yet tested
	private boolean performCheckOperations(Connection connection, JSONArray checks, Long storeId) throws Exception {
		String searchExistingCheckDetailSqlStatement = null;
		String searchExistingCheckDetailSqlStatement2 = null;
		String insertionSqlStatement = null;
		String insertionSqlStatement2 = null;
		String updateSqlStatement = null;
		String updateSqlStatement2 = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		
		try {	
			SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			insertionSqlStatement = "INSERT INTO `check`(store_id, check_id, check_number, staff_id, order_type, table_number, "
					+ "total_item_quantity, total_amount, total_amount_with_tax, total_amount_with_tax_rounding_adjustment, "
					+ "grand_total_amount, tender_amount, overdue_amount, "
					+ "check_status, created_date, updated_date, customer_name, device_id) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			insertionSqlStatement2 = "INSERT INTO `check_tax_charge`(store_id, check_id, check_number, tax_charge_id, total_charge_amount,total_charge_amount_rounding_adjustment,grand_total_charge_amount) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?)";
			
			updateSqlStatement = "UPDATE `check` SET check_number = ?, staff_id = ?, order_type = ?, table_number = ?, total_item_quantity = ?, "
					+ "total_amount = ?, total_amount_with_tax = ?, total_amount_with_tax_rounding_adjustment = ?, "
					+ "grand_total_amount = ?, tender_amount = ?, overdue_amount = ?, "
					+ "check_status = ?, created_date = ?, updated_date = ?, customer_name = ?, device_id = ? WHERE check_id = ? and store_id = ?";
			
			updateSqlStatement2 = "UPDATE `check_tax_charge` SET check_number = ?, tax_charge_id = ?, total_charge_amount = ?, total_charge_amount_rounding_adjustment = ?, grand_total_charge_amount = ? WHERE check_id = ? and store_id = ?";

			searchExistingCheckDetailSqlStatement = "SELECT check_id FROM `check` WHERE check_id = ? and store_id = ?";
			
			searchExistingCheckDetailSqlStatement2 = "SELECT check_id FROM `check_tax_charge` WHERE check_id = ? and store_id = ?";
			
			for(int a=0; a<checks.length(); a++) {
				JSONObject obj = checks.getJSONObject(a);
				
				PreparedStatement ps1 = connection.prepareStatement(searchExistingCheckDetailSqlStatement);
				ps1.setLong(1, obj.getLong("check_id"));
				ps1.setLong(2, storeId);
				ResultSet rs = ps1.executeQuery();
				
				//detect existing, update
				if(rs.next()) {
					stmt =  connection.prepareStatement(updateSqlStatement);
					
					stmt.setLong(1, obj.getLong("check_number"));
					
					if(obj.isNull("staff_id")) stmt.setNull(2, java.sql.Types.BIGINT);
					else stmt.setLong(2, obj.getLong("staff_id"));

					stmt.setLong(3, obj.getLong("order_type"));

					if(obj.isNull("table_number")) stmt.setNull(4, java.sql.Types.INTEGER);
					else stmt.setInt(4, obj.getInt("table_number"));

					stmt.setInt(5, obj.getInt("total_item_quantity"));
					stmt.setBigDecimal(6, BigDecimal.valueOf(obj.getDouble("total_amount")));
					stmt.setBigDecimal(7, BigDecimal.valueOf(obj.getDouble("total_amount_with_tax")));
					stmt.setBigDecimal(8, BigDecimal.valueOf(obj.getDouble("total_amount_with_tax_rounding_adjustment")));			
					stmt.setBigDecimal(9, BigDecimal.valueOf(obj.getDouble("grand_total_amount")));	
					stmt.setBigDecimal(10, BigDecimal.valueOf(obj.getDouble("tender_amount")));
					stmt.setBigDecimal(11, BigDecimal.valueOf(obj.getDouble("overdue_amount")));	
					stmt.setLong(12, obj.getLong("check_status"));
					stmt.setTimestamp(13, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));
					
					if(obj.isNull("updated_date")) stmt.setNull(14, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(14, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));
					
					if(obj.isNull("customer_name")) stmt.setNull(15, java.sql.Types.VARCHAR);
					else stmt.setString(15, obj.getString("customer_name"));
					
					if(obj.isNull("device_id")) stmt.setNull(16, java.sql.Types.BIGINT);
					else stmt.setString(16, obj.getString("device_id"));

					stmt.setLong(17, obj.getLong("check_id"));
					stmt.setLong(18, storeId);

					stmt.executeUpdate();
					stmt.close();
					
					if (obj.has("taxCharges") && !obj.isNull("taxCharges") && obj.getJSONArray("taxCharges").length() > 0) {
						for (int b= 0; b < obj.getJSONArray("taxCharges").length(); b++) {
							JSONObject taxCharge = obj.getJSONArray("taxCharges").getJSONObject(b);
							
							PreparedStatement ps2 = connection.prepareStatement(searchExistingCheckDetailSqlStatement2);
							ps2.setLong(1, taxCharge.getLong("check_id"));
							ps2.setLong(2, storeId);
							ResultSet rs2 = ps2.executeQuery();
							
							if (rs2.next()) {
								stmt =  connection.prepareStatement(updateSqlStatement2);
								
								stmt.setLong(1, taxCharge.getLong("check_number"));				
								stmt.setLong(2, taxCharge.getLong("tax_charge_id"));
								stmt.setDouble(3, taxCharge.getDouble("total_charge_amount"));
								stmt.setDouble(4, taxCharge.getDouble("total_charge_amount_rounding_adjustment"));
								stmt.setDouble(5, taxCharge.getDouble("grand_total_charge_amount"));
								stmt.setLong(6, taxCharge.getLong("check_id"));	
								stmt.setLong(7, storeId);
								
								stmt.executeUpdate();
								stmt.close();
							} else {
								stmt =  connection.prepareStatement(insertionSqlStatement2);
								stmt.setLong(1, storeId);
								stmt.setLong(2, taxCharge.getLong("check_id"));	
								stmt.setLong(3, taxCharge.getLong("check_number"));				
								stmt.setLong(4, taxCharge.getLong("tax_charge_id"));
								stmt.setDouble(5, taxCharge.getDouble("total_charge_amount"));
								stmt.setDouble(6, taxCharge.getDouble("total_charge_amount_rounding_adjustment"));
								stmt.setDouble(7, taxCharge.getDouble("grand_total_charge_amount"));
								
								stmt.executeUpdate();
								stmt.close();
							}
							ps2.close();
							rs2.close();
						}
					}
				} else {
					stmt =  connection.prepareStatement(insertionSqlStatement);
					
					stmt.setLong(1, storeId);
					stmt.setLong(2, obj.getLong("check_id"));	
					stmt.setLong(3, obj.getLong("check_number"));
					
					if(obj.isNull("staff_id")) stmt.setNull(4,  java.sql.Types.BIGINT);
					else stmt.setLong(4, obj.getLong("staff_id"));
					
					stmt.setLong(5, obj.getLong("order_type"));
					
					if(obj.isNull("table_number")) stmt.setNull(6,  java.sql.Types.INTEGER);
					else stmt.setInt(6, obj.getInt("table_number"));
					
					stmt.setInt(7, obj.getInt("total_item_quantity"));		
					stmt.setBigDecimal(8, BigDecimal.valueOf(obj.getDouble("total_amount")));
					stmt.setBigDecimal(9, BigDecimal.valueOf(obj.getDouble("total_amount_with_tax")));
					stmt.setBigDecimal(10, BigDecimal.valueOf(obj.getDouble("total_amount_with_tax_rounding_adjustment")));
					stmt.setBigDecimal(11, BigDecimal.valueOf(obj.getDouble("grand_total_amount")));
					stmt.setBigDecimal(12, BigDecimal.valueOf(obj.getDouble("tender_amount")));
					stmt.setBigDecimal(13, BigDecimal.valueOf(obj.getDouble("overdue_amount")));
					stmt.setLong(14, obj.getLong("check_status"));		
					stmt.setTimestamp(15, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));
					
					if(obj.isNull("updated_date")) stmt.setNull(16, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(16, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));
					
					if(obj.isNull("customer_name")) stmt.setNull(17, java.sql.Types.VARCHAR);
					else stmt.setString(17, obj.getString("customer_name"));
					
					if(obj.isNull("device_id")) stmt.setNull(18, java.sql.Types.BIGINT);
					else stmt.setString(18, obj.getString("device_id"));
					
					stmt.executeUpdate();
					stmt.close();
					
					if (obj.has("taxCharges") && !obj.isNull("taxCharges") && obj.getJSONArray("taxCharges").length() > 0) {
						for (int b= 0; b < obj.getJSONArray("taxCharges").length(); b++) {
							JSONObject taxCharge = obj.getJSONArray("taxCharges").getJSONObject(b);
							
							stmt =  connection.prepareStatement(insertionSqlStatement2);
							stmt.setLong(1, storeId);
							stmt.setLong(2, taxCharge.getLong("check_id"));	
							stmt.setLong(3, taxCharge.getLong("check_number"));				
							stmt.setLong(4, taxCharge.getLong("tax_charge_id"));
							stmt.setDouble(5, taxCharge.getDouble("total_charge_amount"));
							stmt.setDouble(6, taxCharge.getDouble("total_charge_amount_rounding_adjustment"));
							stmt.setDouble(7, taxCharge.getDouble("grand_total_charge_amount"));
							
							stmt.executeUpdate();
							stmt.close();
						}
					}
				}	
				ps1.close();
				rs.close();
			}
			
			flag =true;
		} catch(Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
	
	//not yet tested
	private boolean performTransactionOperations(Connection connection, JSONArray transactions, Long storeId) throws Exception {
		String searchExistingCheckDetailSqlStatement = null;
		String insertionSqlStatement = null;
		String updateSqlStatement = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		
		try {	
			SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			insertionSqlStatement = "INSERT INTO `transaction`(store_id, transaction_id, staff_id, check_id, check_number, transaction_type, payment_method, payment_type, terminal_serial_number, "  
											+ "transaction_currency, transaction_amount, transaction_tips, transaction_status, unique_trans_number, " 
											+ "qr_content, created_date, response_code, response_message, updated_date, wifi_ip, wifi_port, approval_code, "  
											+ "bank_mid, bank_tid, transaction_date, transaction_time, original_invoice_number, invoice_number, merchant_info, card_issuer_name, masked_card_number, card_expiry_date, " 
											+ "batch_number, rrn, card_issuer_id, cardholder_name, aid, app_label, tc, terminal_verification_result, " 
											+ "original_trace_number, trace_number, qr_issuer_type, mpay_mid, mpay_tid, qr_ref_id, qr_user_id, qr_amount_myr, qr_amount_rmb, received_amount, change_amount, device_id) VALUES "
											+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ? , ?, ? , ?, ? , ?, ?, ?," 
											+ " ?, ? , ?, ?, ? , ?, ? , ?, ? , ?, ? , ? , ?, ?, ? ,? ,?, ?, ?, ?, ?)";
			
			updateSqlStatement = "UPDATE `transaction` SET staff_id = ?, check_id = ?, check_number = ?, transaction_type = ?, payment_method = ?, payment_type = ?, "
					+ "terminal_serial_number = ?, transaction_currency = ?, transaction_amount = ?, transaction_tips = ?, "
					+ "transaction_status = ?, unique_trans_number = ?, qr_content = ?, created_date = ?, "
					+ "response_code = ? ,response_message = ?, updated_date = ?, wifi_ip = ?, wifi_port = ?, approval_code = ?, "
					+ "bank_mid = ?, bank_tid = ?, transaction_date = ?, transaction_time = ?, original_invoice_number = ?, "
					+ "invoice_number = ?, merchant_info = ?, card_issuer_name = ?, masked_card_number = ?, card_expiry_date = ?, "
					+ "batch_number = ?, rrn = ?, card_issuer_id = ?, cardholder_name = ?, aid = ?, app_label = ?, tc = ?, terminal_verification_result =?, "
					+ "original_trace_number = ?, trace_number = ?, qr_issuer_type = ?, mpay_mid = ?, mpay_tid = ?, "
					+ "qr_ref_id = ?, qr_user_id = ?, qr_amount_myr = ?, qr_amount_rmb = ?, received_amount = ?, change_amount = ?, device_id = ? WHERE transaction_id = ? and store_id = ?";
			
			searchExistingCheckDetailSqlStatement = "SELECT transaction_id FROM `transaction` WHERE transaction_id = ? and store_id = ?";
			
			for(int a=0; a<transactions.length(); a++) {
				JSONObject obj = transactions.getJSONObject(a);
				
				PreparedStatement ps1 = connection.prepareStatement(searchExistingCheckDetailSqlStatement);
				ps1.setLong(1, obj.getLong("transaction_id"));
				ps1.setLong(2, storeId);
				ResultSet rs = ps1.executeQuery();
			
				//detect existing, update
				if(rs.next()) {
					stmt =  connection.prepareStatement(updateSqlStatement);
					
					stmt.setLong(1, obj.getLong("staff_id"));
					stmt.setLong(2, obj.getLong("check_id"));
					stmt.setLong(3, obj.getLong("check_number"));
					stmt.setLong(4, obj.getLong("transaction_type"));
					stmt.setLong(5, obj.getLong("payment_method"));
					stmt.setLong(6, obj.getLong("payment_type"));
					
					if(obj.isNull("terminal_serial_number")) stmt.setNull(7, java.sql.Types.NVARCHAR);
					else stmt.setString(7,obj.getString("terminal_serial_number"));

					stmt.setString(8, obj.getString("transaction_currency"));	
					stmt.setBigDecimal(9, BigDecimal.valueOf(obj.getDouble("transaction_amount")));
					
					if(obj.isNull("transaction_tips")) stmt.setNull(10, java.sql.Types.DECIMAL);
					else stmt.setBigDecimal(10, BigDecimal.valueOf(obj.getDouble("transaction_tips")));

					stmt.setLong(11, obj.getLong("transaction_status"));
					
					if(obj.isNull("unique_trans_number")) stmt.setNull(12, java.sql.Types.NVARCHAR);
					else stmt.setString(12, obj.getString("unique_trans_number"));
					
					if(obj.isNull("qr_content")) stmt.setNull(13, java.sql.Types.NVARCHAR);
					else stmt.setString(13, obj.getString("qr_content"));

					stmt.setTimestamp(14, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));				
					
					if(obj.isNull("response_code")) stmt.setNull(15, java.sql.Types.NVARCHAR);
					else stmt.setString(15, obj.getString("response_code"));
					
					if(obj.isNull("response_message")) stmt.setNull(16, java.sql.Types.NVARCHAR);
					else stmt.setString(16, obj.getString("response_message"));
					
					if(obj.isNull("updated_date")) stmt.setNull(17, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(17, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));

					if(obj.isNull("wifi_ip")) stmt.setNull(18, java.sql.Types.NVARCHAR);
					else stmt.setString(18, obj.getString("wifi_ip"));
					
					if(obj.isNull("wifi_port")) stmt.setNull(19, java.sql.Types.NVARCHAR);
					else stmt.setString(19, obj.getString("wifi_port"));
					
					if(obj.isNull("approval_code")) stmt.setNull(20, java.sql.Types.NVARCHAR);
					else stmt.setString(20, obj.getString("approval_code"));		
					
					if(obj.isNull("bank_mid")) stmt.setNull(21, java.sql.Types.NVARCHAR);
					else stmt.setString(21, obj.getString("bank_mid"));
					
					if(obj.isNull("bank_tid")) stmt.setNull(22, java.sql.Types.NVARCHAR);
					else stmt.setString(22, obj.getString("bank_tid"));			
					
					if(obj.isNull("transaction_date")) stmt.setNull(23, java.sql.Types.NVARCHAR);
					else stmt.setString(23, obj.getString("transaction_date"));

					if(obj.isNull("transaction_time")) stmt.setNull(24, java.sql.Types.NVARCHAR);
					else stmt.setString(24, obj.getString("transaction_time"));
					
					if(obj.isNull("original_invoice_number")) stmt.setNull(25, java.sql.Types.NVARCHAR);
					else stmt.setString(25, obj.getString("original_invoice_number"));
					
					if(obj.isNull("invoice_number")) stmt.setNull(26, java.sql.Types.NVARCHAR);
					else stmt.setString(26, obj.getString("invoice_number"));	
						
					if(obj.isNull("merchant_info")) stmt.setNull(27, java.sql.Types.NVARCHAR);
					else stmt.setString(27, obj.getString("merchant_info"));
					
					if(obj.isNull("card_issuer_name")) stmt.setNull(28, java.sql.Types.NVARCHAR);
					else stmt.setString(28, obj.getString("card_issuer_name"));
						
					if(obj.isNull("masked_card_number")) stmt.setNull(29, java.sql.Types.NVARCHAR);
					else stmt.setString(29, obj.getString("masked_card_number"));
					
					if(obj.isNull("card_expiry_date")) stmt.setNull(30, java.sql.Types.NVARCHAR);
					else stmt.setString(30, obj.getString("card_expiry_date"));
						
					if(obj.isNull("batch_number")) stmt.setNull(31, java.sql.Types.NVARCHAR);
					else stmt.setString(31, obj.getString("batch_number"));
					
					if(obj.isNull("rrn")) stmt.setNull(32, java.sql.Types.NVARCHAR);
					else stmt.setString(32, obj.getString("rrn"));		
						
					if(obj.isNull("card_issuer_id")) stmt.setNull(33, java.sql.Types.NVARCHAR);
					else stmt.setString(33, obj.getString("card_issuer_id"));
					
					if(obj.isNull("cardholder_name")) stmt.setNull(34, java.sql.Types.NVARCHAR);
					else stmt.setString(34, obj.getString("cardholder_name"));
					
					if(obj.isNull("aid")) stmt.setNull(35, java.sql.Types.NVARCHAR);
					else stmt.setString(35, obj.getString("aid"));
					
					if(obj.isNull("app_label")) stmt.setNull(36, java.sql.Types.NVARCHAR);
					else stmt.setString(36, obj.getString("app_label"));
						
					if(obj.isNull("tc")) stmt.setNull(37, java.sql.Types.NVARCHAR);
					else stmt.setString(37, obj.getString("tc"));
					
					if(obj.isNull("terminal_verification_result")) stmt.setNull(38, java.sql.Types.NVARCHAR);
					else stmt.setString(38, obj.getString("terminal_verification_result"));
					
					if(obj.isNull("original_trace_number")) stmt.setNull(39, java.sql.Types.NVARCHAR);
					else stmt.setString(39, obj.getString("original_trace_number"));
					
					if(obj.isNull("trace_number")) stmt.setNull(40, java.sql.Types.NVARCHAR);
					else stmt.setString(40, obj.getString("trace_number"));
						
					if(obj.isNull("qr_issuer_type")) stmt.setNull(41, java.sql.Types.NVARCHAR);
					else stmt.setString(41, obj.getString("qr_issuer_type"));	
						
					if(obj.isNull("mpay_mid")) stmt.setNull(42, java.sql.Types.NVARCHAR);
					else stmt.setString(42, obj.getString("mpay_mid"));
								
					if(obj.isNull("mpay_tid")) stmt.setNull(43, java.sql.Types.NVARCHAR);
					else stmt.setString(43, obj.getString("mpay_tid"));		
						
					if(obj.isNull("qr_ref_id")) stmt.setNull(44, java.sql.Types.NVARCHAR);
					else stmt.setString(44, obj.getString("qr_ref_id"));	
							
					if(obj.isNull("qr_user_id")) stmt.setNull(45, java.sql.Types.NVARCHAR);
					else stmt.setString(45, obj.getString("qr_user_id"));
					
					if(obj.isNull("qr_amount_myr")) stmt.setNull(46, java.sql.Types.NVARCHAR);
					else stmt.setString(46, obj.getString("qr_amount_myr"));
						
					if(obj.isNull("qr_amount_rmb")) stmt.setNull(47, java.sql.Types.NVARCHAR);
					else stmt.setString(47, obj.getString("qr_amount_rmb"));
					
					if(obj.isNull("received_amount")) stmt.setNull(48, java.sql.Types.NVARCHAR);
					else stmt.setString(48, obj.getString("received_amount"));
						
					if(obj.isNull("change_amount")) stmt.setNull(49, java.sql.Types.NVARCHAR);
					else stmt.setString(49, obj.getString("change_amount"));
					
					if(obj.isNull("device_id")) stmt.setNull(50, java.sql.Types.BIGINT);
					else stmt.setString(50, obj.getString("device_id"));

					stmt.setLong(51, obj.getLong("transaction_id"));	
					stmt.setLong(52, storeId);
					System.out.println("executeUpdate: "+stmt.toString());
					stmt.executeUpdate();
					stmt.close();
				} 
				else {
					stmt =  connection.prepareStatement(insertionSqlStatement);
					
					stmt.setLong(1, storeId);
					stmt.setLong(2, obj.getLong("transaction_id"));
					stmt.setLong(3, obj.getLong("staff_id"));
					stmt.setLong(4, obj.getLong("check_id"));
					stmt.setLong(5, obj.getLong("check_number"));
					stmt.setLong(6, obj.getLong("transaction_type"));
					stmt.setLong(7, obj.getLong("payment_method"));
					stmt.setLong(8, obj.getLong("payment_type"));
					
					if(obj.isNull("terminal_serial_number")) stmt.setNull(9, java.sql.Types.NVARCHAR);
					else stmt.setString(9,obj.getString("terminal_serial_number"));

					stmt.setString(10, obj.getString("transaction_currency"));	
					stmt.setBigDecimal(11, BigDecimal.valueOf(obj.getDouble("transaction_amount")));
					
					if(obj.isNull("transaction_tips")) stmt.setNull(12, java.sql.Types.DECIMAL);
					else stmt.setBigDecimal(12, BigDecimal.valueOf(obj.getDouble("transaction_tips")));

					stmt.setLong(13, obj.getLong("transaction_status"));
					
					if(obj.isNull("unique_trans_number")) stmt.setNull(14, java.sql.Types.NVARCHAR);
					else stmt.setString(14, obj.getString("unique_trans_number"));
					
					if(obj.isNull("qr_content")) stmt.setNull(15, java.sql.Types.NVARCHAR);
					else stmt.setString(15, obj.getString("qr_content"));

					stmt.setTimestamp(16, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));				
					
					if(obj.isNull("response_code")) stmt.setNull(17, java.sql.Types.NVARCHAR);
					else stmt.setString(17, obj.getString("response_code"));
					
					if(obj.isNull("response_message")) stmt.setNull(18, java.sql.Types.NVARCHAR);
					else stmt.setString(18, obj.getString("response_message"));
					
					if(obj.isNull("updated_date")) stmt.setNull(19, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(19, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));
					
					if(obj.isNull("wifi_ip")) stmt.setNull(20, java.sql.Types.NVARCHAR);
					else stmt.setString(20, obj.getString("wifi_ip"));
					
					if(obj.isNull("wifi_port")) stmt.setNull(21, java.sql.Types.NVARCHAR);
					else stmt.setString(21, obj.getString("wifi_port"));
					
					if(obj.isNull("approval_code")) stmt.setNull(22, java.sql.Types.NVARCHAR);
					else stmt.setString(22, obj.getString("approval_code"));		
					
					if(obj.isNull("bank_mid")) stmt.setNull(23, java.sql.Types.NVARCHAR);
					else stmt.setString(23, obj.getString("bank_mid"));
					
					if(obj.isNull("bank_tid")) stmt.setNull(24, java.sql.Types.NVARCHAR);
					else stmt.setString(24, obj.getString("bank_tid"));			
					
					if(obj.isNull("transaction_date")) stmt.setNull(25, java.sql.Types.NVARCHAR);
					else stmt.setString(25, obj.getString("transaction_date"));

					if(obj.isNull("transaction_time")) stmt.setNull(26, java.sql.Types.NVARCHAR);
					else stmt.setString(26, obj.getString("transaction_time"));
					
					if(obj.isNull("original_invoice_number")) stmt.setNull(27, java.sql.Types.NVARCHAR);
					else stmt.setString(27, obj.getString("original_invoice_number"));
					
					if(obj.isNull("invoice_number")) stmt.setNull(28, java.sql.Types.NVARCHAR);
					else stmt.setString(28, obj.getString("invoice_number"));	
						
					if(obj.isNull("merchant_info")) stmt.setNull(29, java.sql.Types.NVARCHAR);
					else stmt.setString(29, obj.getString("merchant_info"));
					
					if(obj.isNull("card_issuer_name")) stmt.setNull(30, java.sql.Types.NVARCHAR);
					else stmt.setString(30, obj.getString("card_issuer_name"));
						
					if(obj.isNull("masked_card_number")) stmt.setNull(31, java.sql.Types.NVARCHAR);
					else stmt.setString(31, obj.getString("masked_card_number"));
					
					if(obj.isNull("card_expiry_date")) stmt.setNull(32, java.sql.Types.NVARCHAR);
					else stmt.setString(32, obj.getString("card_expiry_date"));
						
					if(obj.isNull("batch_number")) stmt.setNull(33, java.sql.Types.NVARCHAR);
					else stmt.setString(33, obj.getString("batch_number"));
					
					if(obj.isNull("rrn")) stmt.setNull(34, java.sql.Types.NVARCHAR);
					else stmt.setString(34, obj.getString("rrn"));		
						
					if(obj.isNull("card_issuer_id")) stmt.setNull(35, java.sql.Types.NVARCHAR);
					else stmt.setString(35, obj.getString("card_issuer_id"));
					
					if(obj.isNull("cardholder_name")) stmt.setNull(36, java.sql.Types.NVARCHAR);
					else stmt.setString(36, obj.getString("cardholder_name"));
					
					if(obj.isNull("aid")) stmt.setNull(37, java.sql.Types.NVARCHAR);
					else stmt.setString(37, obj.getString("aid"));
					
					if(obj.isNull("app_label")) stmt.setNull(38, java.sql.Types.NVARCHAR);
					else stmt.setString(38, obj.getString("app_label"));
						
					if(obj.isNull("tc")) stmt.setNull(39, java.sql.Types.NVARCHAR);
					else stmt.setString(39, obj.getString("tc"));
								
					if(obj.isNull("terminal_verification_result")) stmt.setNull(40, java.sql.Types.NVARCHAR);
					else stmt.setString(40, obj.getString("terminal_verification_result"));
					
					if(obj.isNull("original_trace_number")) stmt.setNull(41, java.sql.Types.NVARCHAR);
					else stmt.setString(41, obj.getString("original_trace_number"));
					
					if(obj.isNull("trace_number")) stmt.setNull(42, java.sql.Types.NVARCHAR);
					else stmt.setString(42, obj.getString("trace_number"));
						
					if(obj.isNull("qr_issuer_type")) stmt.setNull(43, java.sql.Types.NVARCHAR);
					else stmt.setString(43, obj.getString("qr_issuer_type"));	
						
					if(obj.isNull("mpay_mid")) stmt.setNull(44, java.sql.Types.NVARCHAR);
					else stmt.setString(44, obj.getString("mpay_mid"));
								
					if(obj.isNull("mpay_tid")) stmt.setNull(45, java.sql.Types.NVARCHAR);
					else stmt.setString(45, obj.getString("mpay_tid"));		
						
					if(obj.isNull("qr_ref_id")) stmt.setNull(46, java.sql.Types.NVARCHAR);
					else stmt.setString(46, obj.getString("qr_ref_id"));	
							
					if(obj.isNull("qr_user_id")) stmt.setNull(47, java.sql.Types.NVARCHAR);
					else stmt.setString(47, obj.getString("qr_user_id"));
					
					if(obj.isNull("qr_amount_myr")) stmt.setNull(48, java.sql.Types.NVARCHAR);
					else stmt.setString(48, obj.getString("qr_amount_myr"));
						
					if(obj.isNull("qr_amount_rmb")) stmt.setNull(49, java.sql.Types.NVARCHAR);
					else stmt.setString(49, obj.getString("qr_amount_rmb"));
					
					if(obj.isNull("received_amount")) stmt.setNull(50, java.sql.Types.NVARCHAR);
					else stmt.setString(50, obj.getString("received_amount"));
						
					if(obj.isNull("change_amount")) stmt.setNull(51, java.sql.Types.NVARCHAR);
					else stmt.setString(51, obj.getString("change_amount"));
					
					if(obj.isNull("device_id")) stmt.setNull(52, java.sql.Types.BIGINT);
					else stmt.setString(52, obj.getString("device_id"));
					
					stmt.executeUpdate();
					stmt.close();
				}
				ps1.close();
				rs.close();
			}
			
			flag = true;
		} catch(Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
	
	//not completed
	private boolean performSettlementOperations(Connection connection, JSONArray settlements, Long storeId) throws Exception {
		String searchExistingCheckDetailSqlStatement = null;
		String insertionSqlStatement = null;
		String updateSqlStatement = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		
		try {
			SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			insertionSqlStatement = "INSERT INTO `settlement`(store_id, settlement_id, staff_id ,nii_type, settlement_status, created_date, response_code, response_message, updated_date, wifi_ip, wifi_port, merchant_info, bank_mid, bank_tid, batch_number, transaction_date, transaction_time, batch_total, nii, device_id) "
					+ "VALUES (?, ?, ?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,? ,?, ?)";
			
			updateSqlStatement = "UPDATE `settlement` SET staff_id = ?, nii_type = ?, "
					+ "settlement_status = ?, created_date = ?, response_code = ?, response_message = ?, "
					+ "updated_date = ?, wifi_ip = ?, wifi_port = ?, merchant_info = ?, "
					+ "bank_mid = ? , bank_tid = ?, batch_number = ?, transaction_date = ?, transaction_time = ?, "
					+ "batch_total = ?, nii = ?, device_id = ? WHERE settlement_id = ? and store_id = ?";

			searchExistingCheckDetailSqlStatement = "SELECT settlement_id FROM `settlement` WHERE settlement_id = ? and store_id = ?";

			for(int a=0; a<settlements.length(); a++) {
				JSONObject obj = settlements.getJSONObject(a);
				
				PreparedStatement ps1 = connection.prepareStatement(searchExistingCheckDetailSqlStatement);
				ps1.setLong(1, obj.getLong("settlement_id"));
				ps1.setLong(2, storeId);
				ResultSet rs = ps1.executeQuery();
				
				//detect existing, update
				if(rs.next()) {
					stmt =  connection.prepareStatement(updateSqlStatement);
					
					stmt.setLong(1, obj.getLong("staff_id"));
					stmt.setLong(2, obj.getLong("nii_type"));	
					stmt.setLong(3, obj.getLong("settlement_status"));
					stmt.setTimestamp(4, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));
					
					if(obj.isNull("response_code")) stmt.setNull(5, java.sql.Types.NVARCHAR);
					else stmt.setString(5, obj.getString("response_code"));
					
					if(obj.isNull("response_message")) stmt.setNull(6, java.sql.Types.NVARCHAR);
					else stmt.setString(6, obj.getString("response_message"));
					
					if(obj.isNull("updated_date")) stmt.setNull(7, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(7, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));	
					
					if(obj.isNull("wifi_ip")) stmt.setNull(8, java.sql.Types.NVARCHAR);
					else stmt.setString(8,obj.getString("wifi_ip"));
					
					if(obj.isNull("wifi_port")) stmt.setNull(9, java.sql.Types.NVARCHAR);
					else stmt.setString(9, obj.getString("wifi_port"));		
						
					if(obj.isNull("merchant_info")) stmt.setNull(10, java.sql.Types.NVARCHAR);
					else stmt.setString(10, obj.getString("merchant_info"));	
					
					if(obj.isNull("bank_mid")) stmt.setNull(11, java.sql.Types.NVARCHAR);
					else stmt.setString(11, obj.getString("bank_mid"));
				
					if(obj.isNull("bank_tid")) stmt.setNull(12, java.sql.Types.NVARCHAR);
					else stmt.setString(12, obj.getString("bank_tid"));
				
					if(obj.isNull("batch_number")) stmt.setNull(13, java.sql.Types.NVARCHAR);
					else stmt.setString(13, obj.getString("batch_number"));
						
					if(obj.isNull("transaction_date")) stmt.setNull(14, java.sql.Types.NVARCHAR);
					else stmt.setString(14, obj.getString("transaction_date"));
							
					if(obj.isNull("transaction_time")) stmt.setNull(15, java.sql.Types.NVARCHAR);
					else stmt.setString(15, obj.getString("transaction_time"));	
								
					if(obj.isNull("batch_total")) stmt.setNull(16, java.sql.Types.NVARCHAR);
					else stmt.setString(16, obj.getString("batch_total"));
									
					if(obj.isNull("nii")) stmt.setNull(17, java.sql.Types.NVARCHAR);
					else stmt.setString(17, obj.getString("nii"));	
					
					if(obj.isNull("device_id")) stmt.setNull(18, java.sql.Types.BIGINT);
					else stmt.setString(18, obj.getString("device_id"));
					
					stmt.setLong(19, obj.getLong("settlement_id"));
					stmt.setLong(20, storeId);
					
					stmt.executeUpdate();
					stmt.close();
				} else {
					stmt =  connection.prepareStatement(insertionSqlStatement);
					
					stmt.setLong(1, storeId);	
					stmt.setLong(2, obj.getLong("settlement_id"));
					stmt.setLong(3, obj.getLong("staff_id"));
					stmt.setLong(4, obj.getLong("nii_type"));
					stmt.setLong(5, obj.getLong("settlement_status"));
					stmt.setTimestamp(6, new Timestamp(datetimeFormatter.parse(obj.getString("created_date")).getTime()));	
					
					if(obj.isNull("response_code")) stmt.setNull(7, java.sql.Types.NVARCHAR);
					else stmt.setString(7, obj.getString("response_code"));
					
					if(obj.isNull("response_message")) stmt.setNull(8, java.sql.Types.NVARCHAR);
					else stmt.setString(8, obj.getString("response_message"));
					
					if(obj.isNull("updated_date")) stmt.setNull(9, java.sql.Types.TIMESTAMP);
					else stmt.setTimestamp(9, new Timestamp(datetimeFormatter.parse(obj.getString("updated_date")).getTime()));	
					
					if(obj.isNull("wifi_ip")) stmt.setNull(10, java.sql.Types.NVARCHAR);
					else stmt.setString(10,obj.getString("wifi_ip"));
					
					if(obj.isNull("wifi_port")) stmt.setNull(11, java.sql.Types.NVARCHAR);
					else stmt.setString(11, obj.getString("wifi_port"));		
						
					if(obj.isNull("merchant_info")) stmt.setNull(12, java.sql.Types.NVARCHAR);
					else stmt.setString(12, obj.getString("merchant_info"));	
					
					if(obj.isNull("bank_mid")) stmt.setNull(13, java.sql.Types.NVARCHAR);
					else stmt.setString(13, obj.getString("bank_mid"));
				
					if(obj.isNull("bank_tid")) stmt.setNull(14, java.sql.Types.NVARCHAR);
					else stmt.setString(14, obj.getString("bank_tid"));
				
					if(obj.isNull("batch_number")) stmt.setNull(15, java.sql.Types.NVARCHAR);
					else stmt.setString(15, obj.getString("batch_number"));
						
					if(obj.isNull("transaction_date")) stmt.setNull(16, java.sql.Types.NVARCHAR);
					else stmt.setString(16, obj.getString("transaction_date"));
							
					if(obj.isNull("transaction_time")) stmt.setNull(17, java.sql.Types.NVARCHAR);
					else stmt.setString(17, obj.getString("transaction_time"));	
								
					if(obj.isNull("batch_total")) stmt.setNull(18, java.sql.Types.NVARCHAR);
					else stmt.setString(18, obj.getString("batch_total"));
									
					if(obj.isNull("nii")) stmt.setNull(19, java.sql.Types.NVARCHAR);
					else stmt.setString(19, obj.getString("nii"));	
					
					if(obj.isNull("device_id")) stmt.setNull(20, java.sql.Types.BIGINT);
					else stmt.setString(20, obj.getString("device_id"));

					stmt.executeUpdate();
					stmt.close();
				}	
				ps1.close();
				rs.close();
			}
			flag =true;
		} catch(Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}

	private JSONObject verifyActivation(Connection connection, String activationId, String activationKey, Long type) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			//connection = dataSource.getConnection();
			// #important: use store group category id to check
			sqlStatement = "SELECT a.*, b.is_publish, b.group_category_id AS 'groupCategoryId', c.device_name FROM device_info a "
					+ "INNER JOIN store b ON b.id = a.ref_id "
					+ "LEFT JOIN device_info_detail c ON c.device_info_id = a.id "
					+ "WHERE a.activation_id = ? AND a.activation_key = ? AND a.device_type_lookup_id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, activationId);
			ps1.setString(2, activationKey);
			ps1.setLong(3, type);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = new JSONObject();
				result.put("id", rs1.getLong("id"));
				result.put("refId", rs1.getLong("ref_id"));
				result.put("statusLookupId", rs1.getLong("status_lookup_id"));
				result.put("storeStatus", rs1.getLong("is_publish"));
				result.put("groupCategoryId", rs1.getLong("groupCategoryId"));
				result.put("deviceName", rs1.getString("device_name"));
				//result.put("typeId", rs1.getLong("device_type_lookup_id"));
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
		return result;
	}
	
	private boolean activateDevice(Connection connection, Long id, String macAddress, Long groupCategoryId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		int rowAffected = 0;
		boolean flag = false;
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "UPDATE device_info SET mac_address = ? , last_update_date = NOW(), status_lookup_id = ?, group_category_id = ? WHERE id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, macAddress);
			ps1.setInt(2, 2);
			ps1.setLong(3, groupCategoryId);
			ps1.setLong(4, id);
			rowAffected = ps1.executeUpdate();

			if (rowAffected==1) {
				flag = true;
			}
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps1 != null) {
				ps1.close();
			}
		}
		return flag;
	}
	
	private JSONObject getStoreInfo(Connection connection, Long storeId, Long brandId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT id, backend_id, store_name, store_logo_path, store_address, store_longitude, store_latitude, store_country, store_currency, " + 
					"store_start_operating_time, store_end_operating_time, last_update_date, is_publish, created_date, store_contact_person, store_contact_hp_number, store_contact_email, store_type_id, kiosk_payment_delay_id, byod_payment_delay_id, ecpos_takeaway_detail_flag, login_type_id, login_switch_flag FROM store WHERE id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();	
			
			if (rs1.next()) {
				result = new JSONObject();
				result.put("storeId", rs1.getLong("id"));
				result.put("backEndId", rs1.getString("backend_id"));
				result.put("name", rs1.getString("store_name"));
				result.put("logoPath", byodUrl + displayImagePath + brandId + "/" + rs1.getString("store_logo_path"));
				result.put("address", rs1.getString("store_address"));
				result.put("longitude", rs1.getString("store_longitude"));
				result.put("latitude", rs1.getString("store_latitude"));
				result.put("country", rs1.getString("store_country"));
				result.put("currency", rs1.getString("store_currency"));
				result.put("startOperatingTime", rs1.getString("store_start_operating_time"));
				result.put("endOperatingTime", rs1.getString("store_end_operating_time"));
				result.put("lastUpdateDate", rs1.getString("last_update_date"));
				result.put("isPublish", rs1.getLong("is_publish"));
				result.put("createdDate", rs1.getString("created_date"));
				result.put("contactPerson", rs1.getString("store_contact_person"));
				result.put("mobileNumber", rs1.getString("store_contact_hp_number"));
				result.put("email", rs1.getString("store_contact_email"));
				result.put("storeTypeId", rs1.getLong("store_type_id"));
				result.put("kioskPaymentDelayId", rs1.getLong("kiosk_payment_delay_id"));
				result.put("byodPaymentDelayId", rs1.getLong("byod_payment_delay_id"));
				result.put("ecposTakeawayDetailFlag", rs1.getBoolean("ecpos_takeaway_detail_flag"));
				result.put("loginTypeId", rs1.getLong("login_type_id"));
				result.put("loginSwitchFlag", rs1.getBoolean("login_switch_flag"));
				
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
		return result;
	}
	
	private JSONArray getEcposStaffInfo(Connection connection, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONArray result = new JSONArray();
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT a.id, a.staff_name, a.staff_username, a.staff_password, a.staff_role, a.staff_contact_hp_number, a.staff_contact_email, a.is_active, a.created_date, a.last_update_date " +
					"FROM staff a " + 
					"INNER JOIN store b ON a.store_id = b.id " + 
					"WHERE b.id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();	
			
			while (rs1.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs1.getLong("id"));
				jsonObject.put("name", rs1.getString("staff_name"));
				jsonObject.put("username", rs1.getString("staff_username"));
				jsonObject.put("password", rs1.getString("staff_password"));
				jsonObject.put("role", rs1.getLong("staff_role"));
				jsonObject.put("phoneNumber", rs1.getString("staff_contact_hp_number"));
				jsonObject.put("email", rs1.getString("staff_contact_email"));
				jsonObject.put("isActive", rs1.getLong("is_active"));
				jsonObject.put("createdDate", rs1.getString("created_date"));
				jsonObject.put("lastUpdateDate", rs1.getString("last_update_date")==null?"":rs1.getString("last_update_date"));
				result.put(jsonObject);
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
		return result;
	}
	
	private JSONArray getEcposStaffRole(Connection connection) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONArray result = new JSONArray();
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT id, role_name FROM role_lookup";
			ps1 = connection.prepareStatement(sqlStatement);
			rs1 = ps1.executeQuery();	
			
			while (rs1.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs1.getLong("id"));
				jsonObject.put("roleName", rs1.getString("role_name"));
				result.put(jsonObject);
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
		return result;
	}
	
	private JSONArray getEcposTableSetting(Connection connection, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONArray result = new JSONArray();
		try {
			sqlStatement = "SELECT id, table_name, status_lookup_id, created_date, last_update_date FROM table_setting "
					+ "WHERE store_id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();	
			
			while (rs1.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", rs1.getLong("id"));
				jsonObject.put("tableName", rs1.getString("table_name"));
				jsonObject.put("statusLookupId", rs1.getLong("status_lookup_id"));
				jsonObject.put("createdDate", rs1.getString("created_date"));
				jsonObject.put("lastUpdateDate", rs1.getString("last_update_date")==null?"":rs1.getString("last_update_date"));
				result.put(jsonObject);
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
		return result;
	}
	
	public boolean extractLatestImages(String brandId, Long groupCategoryId) throws Exception {
		boolean flag = false;
		// grab menu images
		File directory = new File(imagePath + brandId);
		if(directory.isDirectory() && directory.list().length == 0) {
		    System.out.println("No images found");
		} else {			
			File[] imageFiles = directory.listFiles();
			ArrayList<String> imageList = new ArrayList<String>();
			
			for (int i = 0; i < imageFiles.length; i++) {
			  if (imageFiles[i].isFile()) {
				  imageList.add(imageFiles[i].getName());
			  } 
			}
			// zipping images
			FileOutputStream fos = new FileOutputStream(filePath + brandId + "/" + groupCategoryId +"/latest/image.zip");
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			for (String srcFile : imageList) {
				File fileToZip = new File(imagePath + brandId, srcFile);
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
			flag = true;
		}
		return flag;
	}
	
	private JSONObject getLatestMenuFile(Connection connection, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT a.group_category_id, c.* FROM store a " + 
					"INNER JOIN group_category b ON a.group_category_id = b.id " + 
					"INNER JOIN publish_version c ON c.id = b.publish_version_id AND c.group_category_id = b.id " +
					"WHERE a.id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = new JSONObject();
				result.put("groupCategoryId", rs1.getLong("group_category_id"));
				result.put("menuFilePath", rs1.getString("menu_file_path"));
				result.put("versionCount", rs1.getLong("version_count"));
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
		return result;
	}
	
	private JSONArray getVersionUpdates(Connection connection, Long brandId, Long versionCount, Long storeId, Long deviceType) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONArray versionArray = new JSONArray();

		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT c.* FROM store a " + 
					"INNER JOIN group_category b ON a.group_category_id = b.id " + 
					"INNER JOIN publish_version c ON c.id IN ( " + 
					"SELECT id FROM publish_version WHERE group_category_id = b.id AND version_count > ?) AND c.group_category_id = b.id " +
					"WHERE a.id = ? " + 
					"ORDER BY c.version_count ASC ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, versionCount);
			ps1.setLong(2, storeId);
			rs1 = ps1.executeQuery();
			
			while (rs1.next()) {
				JSONObject jsonObject = new JSONObject();
				String menuFilePath = rs1.getString("menu_file_path");
				String menuQueryFile = rs1.getString("menu_query_file_path");
				String menuImageFile = rs1.getString("menu_img_file_path");
				String menuQueryFilePath = "";
				String menuImageFilePath = "";
				Long groupCategoryId = rs1.getLong("group_category_id");
				String url = byodUrl + displayFilePath + brandId + "/" + groupCategoryId + "/" ;
				
				if(menuQueryFile!=null) {
					if(deviceType==1) {
						// ecpos (MySQL)
						// process query file for mysql usage		
						File mysqlFile = new File(filePath + brandId + "/" + groupCategoryId + "/" + menuFilePath, menuQueryFile + "_mysql.txt");
						File queryFile = new File(filePath + brandId + "/" + groupCategoryId + "/" + menuFilePath, menuQueryFile + ".txt");		
						if (!mysqlFile.exists()) {
							// read file
							BufferedReader br = new BufferedReader(new FileReader(queryFile));
							try {
							    StringBuilder sb = new StringBuilder();
							    String line = br.readLine();	
							    while (line != null) {
							        sb.append(line);
							        sb.append(System.lineSeparator());
							        line = br.readLine();
							    }
							    String everything = sb.toString();
							    // remove SET IDENTITY_INSERT
							    String regex = "^SET IDENTITY_INSERT.*;$\r?\n";
							    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
							    Matcher matcher = pattern.matcher(everything);
							    while (matcher.find()) {
							        everything = everything.replace(matcher.group(0), "");
							    }
							    
							   /* // remove empty lines
							    regex = "(?m)^[ \t]*\r?\n";
							    pattern = Pattern.compile(regex, Pattern.MULTILINE);
							    matcher = pattern.matcher(everything);
							    while (matcher.find()) {
							        everything = everything.replace(matcher.group(0), "");
							    }					    
							    */
							    // replace NOW() with NOW()
							    everything = everything.replaceAll("GETDATE\\(\\)", "NOW()");				    							  					
 
							    // new mysql file
								Writer output = new BufferedWriter(new FileWriter(mysqlFile));
					            output.write(everything);
					            output.close();
							} finally {
							    br.close();
							}
						}
						menuQueryFilePath = url + menuFilePath + "/" + menuQueryFile + "_mysql.txt";
					}
					else {
						menuQueryFilePath = url + menuFilePath + "/" + menuQueryFile + ".txt";
					}
				}	
				if(menuImageFile!=null)
					menuImageFilePath = url + menuFilePath + "/" + menuImageFile + ".zip";
				
				jsonObject.put("versionCount", rs1.getLong("version_count"));
				jsonObject.put("menuQueryFilePath", menuQueryFilePath);
				jsonObject.put("menuImageFilePath", menuImageFilePath);
				versionArray.put(jsonObject);
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
		return versionArray;
	}
	
	private JSONObject getDeviceInfoByActivationId(Connection connection, String activationId, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT a.id, a.status_lookup_id, a.device_type_lookup_id, a.mac_address, a.group_category_id, b.is_publish, c.device_name FROM device_info a "
					+ "INNER JOIN store b ON b.id = a.ref_id AND a.group_category_id = b.group_category_id "
					+ "LEFT JOIN device_info_detail c ON c.device_info_id = a.id "
					+ "WHERE a.activation_id = ? AND a.ref_id = ?";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, activationId);
			ps1.setLong(2, storeId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = new JSONObject();
				result.put("id", rs1.getLong("id"));
				result.put("statusLookupId", rs1.getLong("status_lookup_id"));
				result.put("deviceType", rs1.getLong("device_type_lookup_id"));
				result.put("mac_address", rs1.getString("mac_address")==null?"":rs1.getString("mac_address"));
				result.put("storeStatus", rs1.getLong("is_publish"));
				result.put("groupCategoryId", rs1.getLong("group_category_id"));
				result.put("deviceName", rs1.getString("device_name"));
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
		return result;
	}
	
	public void generateLatestQueryFile(Connection connection, String brandId, Long groupCategoryId) throws Exception {
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		ResultSetMetaData metaData = null;
		String result = "";
		String insertSql = "";
		String sql = "";
		StringJoiner columnName = null;
		StringJoiner columnValue = null;
		StringJoiner columnSubValue = null;

		String[] tableNames = { "category", "category_menu_item", "combo_detail", "combo_item_detail", "menu_item",
				"menu_item_display_period", "menu_item_group", "menu_item_group_sequence", "menu_item_modifier_group",
				"menu_item_promo_period", "modifier_group", "modifier_item_sequence", "tax_charge" };

		try {
			for (String table : tableNames) {

				// filter for group category
				if (table.equals("category")) {
					sql = "select * from category where group_category_id = " + groupCategoryId + ";";
				} else if (table.equals("tax_charge")) {
					sql = "select a.* from tax_charge a INNER JOIN group_category_tax_charge b ON a.id = b.tax_charge_id "
							+ "where b.group_category_id = " + groupCategoryId + ";";
				} else {
					sql = "select * from " + table + ";";
				}

				// get records from cloud db
				ps1 = connection.prepareStatement(sql);
				rs1 = ps1.executeQuery();

				if (rs1.next()) {

					// get column name
					columnName = new StringJoiner(",");
					metaData = rs1.getMetaData();
					for (int a = 1; a < metaData.getColumnCount() + 1; a++) {
						columnName.add(metaData.getColumnName(a));
					}

					columnValue = new StringJoiner(",");
					String temp = "";

					// construct insert statement
					do {
						columnSubValue = new StringJoiner(",");
						for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
							String val = "";
							int type = metaData.getColumnType(i);
							
							if (rs1.getString(i) == null) {
								val = rs1.getString(i);
							}else if (type == Types.BLOB || type == Types.CHAR || type == Types.CLOB || type == Types.DATE
									|| type == Types.LONGNVARCHAR || type == Types.LONGVARCHAR || type == Types.NCHAR
									|| type == Types.NVARCHAR || type == Types.TIME || type == Types.TIMESTAMP
									|| type == Types.VARCHAR) {
								val = "'" + rs1.getString(i) + "'";
							} else {
								val = rs1.getString(i);
							}

							columnSubValue.add(val);
						}
						temp = "(" + columnSubValue.toString() + ")";
						columnValue.add(temp);
					} while (rs1.next());

					insertSql = "Insert into " + table + " (" + columnName + ") Values " + columnValue + ";";
					System.out.println("insertSql " + insertSql);
					result += insertSql + "\r\n";
				}
			}

			File checkFile = new File(filePath + brandId + "/" + groupCategoryId, "latest/query.txt");
			// new file
			Writer output = new BufferedWriter(new FileWriter(checkFile));
			output.write(result);
			output.close();

			checkFile = new File(filePath + brandId + "/" + groupCategoryId, "latest/queryMySql.txt");
			output = new BufferedWriter(new FileWriter(checkFile));
			output.write(result);
			output.close();

		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null)
				rs1.close();
			if (ps1 != null)
				ps1.close();
		}
	}
	
	public void generateLatestMenuFile(JSONObject result, String brandId, Long groupCategoryId) throws Exception {
		// write to json file
		try {
			File checkFile = new File(filePath + brandId + "/" + groupCategoryId, "latest/menuFilePath.json");
			// new file
			Writer output = new BufferedWriter(new FileWriter(checkFile));
            output.write(result.toString());
            output.close();
            
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private boolean checkBrandExist(Long brandId) throws Exception {
		Connection connection = null;
		PreparedStatement stmt = null;
		String sqlStatement = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			connection = dataSource.getConnection();
			sqlStatement = "SELECT * FROM brands WHERE id = ? ";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, brandId);
			rs = stmt.executeQuery();
			if(rs.next()) {
				flag = true;
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(connection!=null) {
				connection.close();
			}
			if(stmt!=null) {
				stmt.close();
			}
			if(rs!=null) {
				rs.close();
			}
		}
		return flag;
	}
}
