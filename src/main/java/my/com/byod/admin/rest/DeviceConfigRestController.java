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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private ByodUtil byodUtil;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
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
					JSONArray ecposStaffRole = getEcposStaffRole(connection);					
					if(ecposStaffInfo.length()==0) {
						resultCode = "E07";
						resultMessage = "Please create at least one staff login before activation.";
						throw new IllegalArgumentException("There is no staff login info.");
					}		
					result.put("staffInfo", ecposStaffInfo);
					result.put("staffRole", ecposStaffRole);
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
				JSONArray ecposStaffInfo = getEcposStaffInfo(connection, storeId);
				JSONArray ecposStaffRole = getEcposStaffRole(connection);
				result.put("storeInfo", storeInfo);	
				result.put("staffInfo", ecposStaffInfo);
				result.put("staffRole", ecposStaffRole);
				
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
	
	private JSONObject verifyActivation(Connection connection, String activationId, String activationKey, Long type) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			//connection = dataSource.getConnection();
			sqlStatement = "SELECT b.group_category_id AS 'groupCategoryId', * FROM device_info a "
					+ "INNER JOIN store b ON b.id = a.ref_id "
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
			sqlStatement = "UPDATE device_info SET mac_address = ? , last_update_date = GETDATE(), status_lookup_id = ?, group_category_id = ? WHERE id = ? ";
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
			sqlStatement = "SELECT id, tax_charge_id, backend_id, store_name, store_logo_path, store_address, store_longitude, store_latitude, store_country, store_currency, " + 
					"store_table_count, store_start_operating_time, store_end_operating_time, last_update_date, is_publish, created_date FROM store WHERE id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();	
			
			if (rs1.next()) {
				result = new JSONObject();
				result.put("storeId", rs1.getLong("id"));
				result.put("taxChargeId", rs1.getLong("tax_charge_id"));
				result.put("backEndId", rs1.getString("backend_id"));
				result.put("name", rs1.getString("store_name"));
				result.put("logoPath", byodUrl + displayImagePath + brandId + "/" + rs1.getString("store_logo_path"));
				result.put("address", rs1.getString("store_address"));
				result.put("longitude", rs1.getString("store_longitude"));
				result.put("latitude", rs1.getString("store_latitude"));
				result.put("country", rs1.getString("store_country"));
				result.put("currency", rs1.getString("store_currency"));
				result.put("tableCount", rs1.getString("store_table_count"));
				result.put("startOperatingTime", rs1.getString("store_start_operating_time"));
				result.put("endOperatingTime", rs1.getString("store_end_operating_time"));
				result.put("lastUpdateDate", rs1.getString("last_update_date"));
				result.put("isPublish", rs1.getLong("is_publish"));
				result.put("createdDate", rs1.getString("created_date"));
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
							    // replace GETDATE() with NOW()
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
			sqlStatement = "SELECT * FROM device_info a "
					+ "INNER JOIN store b ON b.id = a.ref_id AND a.group_category_id = b.group_category_id "
					+ "WHERE a.activation_id = ? AND a.ref_id = ?";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, activationId);
			ps1.setLong(2, storeId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = new JSONObject();
				result.put("statusLookupId", rs1.getLong("status_lookup_id"));
				result.put("deviceType", rs1.getLong("device_type_lookup_id"));
				result.put("mac_address", rs1.getString("mac_address")==null?"":rs1.getString("mac_address"));
				result.put("storeStatus", rs1.getLong("is_publish"));
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
		// sqlStatement - sql server script
		// sqlStatement2 - my sql script
		String sqlStatement = null;
		String sqlStatement2 = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String result = "";
		String result2 = "";
		
		String[] tableNames = {
				"category",
				"category_menu_item", 
				"combo_detail",
				"combo_item_detail",
				"menu_item",
				"menu_item_display_period",
				"menu_item_group",
				"menu_item_group_sequence",
				"menu_item_modifier_group",
				"menu_item_promo_period",
				"menu_item_tax_charge",
				"modifier_group",
				"modifier_item_sequence"};
		try {
			for(String table : tableNames) {
				sqlStatement = "EXEC sp_generate_inserts ?";
				sqlStatement2 = "EXEC sp_generate_inserts_mysql ?";
				
				// filter for group category
				if(table.equals("category")) {
					sqlStatement += ", @from = \"from category where group_category_id = "+groupCategoryId+"\"";
					sqlStatement2 += ", @from = \"from category where group_category_id = "+groupCategoryId+"\"";
				}
					
				ps1 = connection.prepareStatement(sqlStatement);
				ps1.setString(1, table);
				rs1 = ps1.executeQuery();			
				while (rs1.next()) {
					result += rs1.getString(1) +";\r\n";
				}
				
				ps2 = connection.prepareStatement(sqlStatement2);
				ps2.setString(1, table);
				rs2 = ps2.executeQuery();				
				while (rs2.next()) {
					result2 += rs2.getString(1) +";\r\n";
				}			
			}

			File checkFile = new File(filePath + brandId + "/" + groupCategoryId, "latest/query.txt");
			// new file
			Writer output = new BufferedWriter(new FileWriter(checkFile));
            output.write(result);
            output.close();
            
            checkFile = new File(filePath + brandId + "/" + groupCategoryId, "latest/queryMySql.txt");
            output = new BufferedWriter(new FileWriter(checkFile));
            output.write(result2);
            output.close();       
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
			if (ps2 != null) {
				ps2.close();
			}
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
	
}
