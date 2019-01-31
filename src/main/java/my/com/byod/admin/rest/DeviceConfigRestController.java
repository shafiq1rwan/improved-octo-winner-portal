package my.com.byod.admin.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

@RestController
@RequestMapping("/api/device")
public class DeviceConfigRestController {
	
	@Value("${menu-path}")
	private String filePath;
	
	@Value("${upload-path}")
	private String imagePath;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ByodUtil byodUtil;
	
	
	@RequestMapping(value = "/activation", method = { RequestMethod.POST })
	public String activation(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "activationId", required = true) String activationId, 
			@RequestParam(value = "activationKey", required = true) String activationKey,
			@RequestParam(value = "macAddress", required = true) String macAddress) {

		// device activation api
		JSONObject result = new JSONObject();
		JSONObject deviceInfo = null;
		Connection connection = null;
		String menuFile = null;

		try {
			connection = dataSource.getConnection();
			deviceInfo = verifyActivation(connection, activationId, activationKey);
			if(deviceInfo==null) {
				result.put("resultCode", "01");
				result.put("resultMessage", "Invalid activation info");
				return result.toString();
			}
			
			if(deviceInfo.getLong("statusLookupId")!=1) {
				// not pending
				result.put("resultCode", "02");
				result.put("resultMessage", "Activation device is already activated or terminated");
				return result.toString();
			}
			
			if(activateDevice(connection, deviceInfo.getLong("id"), macAddress)) {
				// successful activation
				
				// ecpos retrieve extra info
				if(deviceInfo.getLong("typeId")==1) {
					JSONObject storeInfo = getStoreInfo(connection, deviceInfo.getLong("refId"));
					JSONArray ecposStaffInfo = getEcposStaffInfo(connection, deviceInfo.getLong("refId"));
					
					result.put("storeInfo", storeInfo);
					result.put("staffInfo", ecposStaffInfo);
				}
				
				menuFile = getLatestMenuFile(connection, deviceInfo.getLong("refId"));			
				File file = new File(filePath + menuFile, menuFile + ".json");
				String menuFilePath = file.getAbsolutePath();
				String imageFilePath = extractImageFromMenuFilePath(menuFile);
				
				result.put("menuFilePath", menuFilePath);
				result.put("imageFilePath", imageFilePath);
				result.put("resultCode", "00");
				result.put("resultMessage", "Successful device activation");
			}
			else {
				result.put("resultCode", "03");
				result.put("resultMessage", "Failed to activate device");
				return result.toString();
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
		}
		
		return result.toString();
	}
	
	@RequestMapping(value = "/syncMenu", method = { RequestMethod.POST })
	public String getMenuData(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "storeId", required = true) Long storeId, 
			@RequestParam(value = "versionCount", required = true) Long versionCount,
			@RequestParam(value = "activationId", required = true) String activationId,
			@RequestParam(value = "timeStamp", required = true) String timeStamp,
			@RequestParam(value = "authToken", required = true) String authToken) {
		
		JSONObject result = new JSONObject();
		Connection connection = null;
		String menuFile = null;

		try {
			connection = dataSource.getConnection();
			String macAddress = getMacAddressByActivationId(connection, activationId);
			String secureHash = byodUtil.genSecureHash("SHA-256", activationId.concat(macAddress).concat(timeStamp));
			System.out.println("authToken:" + authToken);
			System.out.println("secureHash:" + secureHash);
			if(!authToken.equals(secureHash)) {
				result.put("resultCode", "01");
				result.put("resultMessage", "Invalid authentication token");
				return result.toString();
			}
			
			if(versionCount==0) {
				// first time get menu
				menuFile = getLatestMenuFile(connection, storeId);
				if(menuFile!=null) {
					File file = new File(filePath + menuFile, menuFile + ".json");
					String menuFilePath = file.getAbsolutePath();
					String imageFilePath = extractImageFromMenuFilePath(menuFile);
					
					result.put("menuFilePath", menuFilePath); 
					result.put("imageFilePath", imageFilePath);
				}
				else {
					result.put("resultCode", "02");
					result.put("resultMessage", "Never publish menu before");
					return result.toString();
				}
			}
			else {
				// sync menu
				JSONArray versionArray = getVersionUpdates(connection, versionCount, storeId);			
				if(versionArray.length()!=0) {
					result.put("versionSync", versionArray);
					result.put("resultCode", "00");
					result.put("resultCode", "Successful menu sync");
				}
				else {
					// up-to-date
					result.put("resultCode", "03");
					result.put("resultMessage", "Current menu is the latest version");
					return result.toString();
				}			
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		return result.toString();
	}
	
	private JSONObject verifyActivation(Connection connection, String activationId, String activationKey) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		Long id = null;
		JSONObject result = null;
		try {
			connection = dataSource.getConnection();
			sqlStatement = "SELECT * FROM device_info WHERE activation_id = ? AND activation_key = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, activationId);
			ps1.setString(2, activationKey);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = new JSONObject();
				result.put("id", rs1.getLong("id"));
				result.put("refId", rs1.getLong("ref_id"));
				result.put("statusLookupId", rs1.getLong("status_lookup_id"));
				result.put("typeId", rs1.getLong("device_type_lookup_id"));
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
	
	private boolean activateDevice(Connection connection, Long id, String macAddress) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		int rowAffected = 0;
		boolean flag = false;
		try {
			connection = dataSource.getConnection();
			sqlStatement = "UPDATE device_info SET mac_address = ? , last_update_date = GETDATE(), status_lookup_id = ? WHERE id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, macAddress);
			ps1.setInt(2, 2);
			ps1.setLong(3, id);
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
	
	private JSONObject getStoreInfo(Connection connection, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONObject result = null;
		try {
			connection = dataSource.getConnection();
			sqlStatement = "SELECT tax_charge_id, backend_id, store_name, store_logo_path, store_address, store_longitude, store_latitude, store_country, store_currency, " + 
					"store_table_count, store_start_operating_time, store_end_operating_time, last_update_date, is_publish, created_date FROM store WHERE id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();	
			
			if (rs1.next()) {
				result = new JSONObject();
				result.put("taxChargeId", rs1.getLong("tax_charge_id"));
				result.put("backeEndId", rs1.getString("backend_id"));
				result.put("name", rs1.getString("store_name"));
				result.put("logoPath", imagePath + rs1.getString("store_logo_path") + ".png");
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
			connection = dataSource.getConnection();
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
				jsonObject.put("lastUpdateDate", rs1.getString("last_update_date"));
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
	
	private String extractImageFromMenuFilePath(String menuFilePath) throws Exception {
		String zipFilePath = "";
		// grab menu images
		File directory = new File(imagePath);
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
			System.out.println("my menu file:" + menuFilePath);
			// zipping images
			File zipFile = new File(filePath + menuFilePath , menuFilePath+".zip");
			FileOutputStream fos = new FileOutputStream(filePath + menuFilePath + "/" +  menuFilePath +".zip");
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
			zipFilePath = zipFile.getAbsolutePath();
			zipOut.close();
			fos.close();
		}
		return zipFilePath;
	}
	
	private String getLatestMenuFile(Connection connection, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		String result = null;
		try {
			connection = dataSource.getConnection();
			sqlStatement = "SELECT c.* FROM store a " + 
					"INNER JOIN group_category b ON a.group_category_id = b.id " + 
					"INNER JOIN publish_version c ON c.id = b.publish_version_id AND c.group_category_id = b.id " +
					"WHERE a.id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setLong(1, storeId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = rs1.getString("menu_file_path");
				/*File file = new File(filePath + rs1.getString("menu_file_path"), rs1.getString("menu_file_path") + ".json");
				result = file.getAbsolutePath();*/
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
	
	private JSONArray getVersionUpdates(Connection connection, Long versionCount, Long storeId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		JSONArray versionArray = new JSONArray();
		File file = null;
		try {
			connection = dataSource.getConnection();
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
				
				file = new File(filePath + menuFilePath, menuQueryFile + ".txt");
				String menuQueryFilePath = file.getAbsolutePath();
				
				file = new File(imagePath + menuFilePath, menuImageFile + ".zip");
				String menuImageFilePath = file.getAbsolutePath();
				
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
	
	private String getMacAddressByActivationId(Connection connection, String activationId) throws Exception {
		String sqlStatement = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		String result = "";
		try {
			connection = dataSource.getConnection();
			sqlStatement = "SELECT mac_address FROM device_info WHERE activation_id = ? ";
			ps1 = connection.prepareStatement(sqlStatement);
			ps1.setString(1, activationId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				result = rs1.getString("mac_address")==null?"":rs1.getString("mac_address");
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
	
}
