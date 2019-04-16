package my.com.byod.admin.rest;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/setting")
public class SettingRestController {

	@Value("${get-upload-path}")
	private String displayFilePath;
	
	@Value("${byod-cloud-url}")
	private String byodUrl;
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@Autowired
	private ByodUtil byodUtil;
	
	@GetMapping(value = "/getConfig", produces = "application/json")
	public ResponseEntity<?> getConfig(HttpServletRequest request, HttpServletResponse response){
		JSONObject jsonObj  = new JSONObject();
		Connection connection = null;	
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonObj.put("mainLogoPath", byodUtil.getGeneralConfig(connection, "mainLogoPath"));
			jsonObj.put("shortcutLogoPath", byodUtil.getGeneralConfig(connection, "shortcutLogoPath"));
			jsonObj.put("mainBackgroundPath", byodUtil.getGeneralConfig(connection, "mainBackgroundPath"));
			jsonObj.put("landingLogoPath",byodUtil.getGeneralConfig(connection, "landingLogoPath"));		
			jsonObj.put("appName", byodUtil.getGeneralConfig(connection, "appName"));
			jsonObj.put("mainColor", byodUtil.getGeneralConfig(connection, "mainColor"));
			jsonObj.put("subColor", byodUtil.getGeneralConfig(connection, "subColor"));
			jsonObj.put("mainTextColor", byodUtil.getGeneralConfig(connection, "mainTextColor"));
			jsonObj.put("subTextColor", byodUtil.getGeneralConfig(connection, "subTextColor"));
			jsonObj.put("localeButtonColor", byodUtil.getGeneralConfig(connection, "localeButtonColor"));
			jsonObj.put("mainButtonTextColor", byodUtil.getGeneralConfig(connection, "mainButtonTextColor"));
			jsonObj.put("mainButtonBackgroundColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundColor"));
			jsonObj.put("mainButtonBackgroundHoverColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundHoverColor"));
			jsonObj.put("mainButtonBackgroundFocusColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundFocusColor"));
			return ResponseEntity.ok(jsonObj.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Server error. Unable to retrieve setting.");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	@PostMapping(value = "/updateConfig", produces = "application/json")
	public ResponseEntity<?> updateConfig(@RequestBody String data, HttpServletRequest request, HttpServletResponse response){
		Connection connection = null;	
		try {
			JSONObject jsonObj  = new JSONObject(data);
			System.out.println(data);
			connection = dbConnectionUtil.retrieveConnection(request);
			String brandId = String.valueOf(dbConnectionUtil.getBrandId(request));
			if(jsonObj.has("mainLogoPath")) {
				/*String existing = byodUtil.getGeneralConfig(connection, "mainLogoPath");
				if(!existing.equals("")) {
					existing = existing.substring(existing.lastIndexOf('/')+1);
				}*/
				byodUtil.updateGeneralConfig(connection, "mainLogoPath", displayFilePath + brandId + "/" + byodUtil.saveImageFile(brandId,"ST", jsonObj.getString("mainLogoPath"), null));
			}
			if(jsonObj.has("shortcutLogoPath"))
				byodUtil.updateGeneralConfig(connection, "shortcutLogoPath", displayFilePath + brandId + "/" + byodUtil.saveImageFile(brandId,"ST", jsonObj.getString("shortcutLogoPath"), null));
			if(jsonObj.has("mainBackgroundPath"))
				byodUtil.updateGeneralConfig(connection, "mainBackgroundPath", displayFilePath + brandId + "/" + byodUtil.saveImageFile(brandId,"ST", jsonObj.getString("mainBackgroundPath"), null));
			if(jsonObj.has("landingLogoPath"))
				byodUtil.updateGeneralConfig(connection, "landingLogoPath", displayFilePath + brandId + "/" + byodUtil.saveImageFile(brandId,"ST", jsonObj.getString("landingLogoPath"), null));
			byodUtil.updateGeneralConfig(connection, "appName", jsonObj.getString("appName"));
			byodUtil.updateGeneralConfig(connection, "mainColor", jsonObj.getString("mainColor"));
			byodUtil.updateGeneralConfig(connection, "subColor", jsonObj.getString("subColor"));
			byodUtil.updateGeneralConfig(connection, "mainTextColor", jsonObj.getString("mainTextColor"));
			byodUtil.updateGeneralConfig(connection, "subTextColor", jsonObj.getString("subTextColor"));
			byodUtil.updateGeneralConfig(connection, "localeButtonColor", jsonObj.getString("localeButtonColor"));
			byodUtil.updateGeneralConfig(connection, "mainButtonTextColor", jsonObj.getString("mainButtonTextColor"));
			byodUtil.updateGeneralConfig(connection, "mainButtonBackgroundColor", jsonObj.getString("mainButtonBackgroundColor"));
			byodUtil.updateGeneralConfig(connection, "mainButtonBackgroundHoverColor", jsonObj.getString("mainButtonBackgroundHoverColor"));
			byodUtil.updateGeneralConfig(connection, "mainButtonBackgroundFocusColor", jsonObj.getString("mainButtonBackgroundFocusColor"));

			return ResponseEntity.ok(jsonObj.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("Server error. Unable to update setting.");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public JSONObject getBrandSetting(Long brandId){
		JSONObject jsonObj = new JSONObject();
		Connection connection = null;
		try {
			connection = dbConnectionUtil.getConnection(brandId);		
			jsonObj.put("mainLogoPath", byodUtil.getGeneralConfig(connection, "mainLogoPath"));
			jsonObj.put("shortcutLogoPath", byodUtil.getGeneralConfig(connection, "shortcutLogoPath"));
			jsonObj.put("mainBackgroundPath", byodUtil.getGeneralConfig(connection, "mainBackgroundPath"));	
			jsonObj.put("landingLogoPath", byodUtil.getGeneralConfig(connection, "landingLogoPath"));
			jsonObj.put("appName", byodUtil.getGeneralConfig(connection, "appName"));
			jsonObj.put("mainColor", byodUtil.getGeneralConfig(connection, "mainColor"));
			jsonObj.put("subColor", byodUtil.getGeneralConfig(connection, "subColor"));
			jsonObj.put("mainTextColor", byodUtil.getGeneralConfig(connection, "mainTextColor"));
			jsonObj.put("subTextColor", byodUtil.getGeneralConfig(connection, "subTextColor"));
			jsonObj.put("localeButtonColor", byodUtil.getGeneralConfig(connection, "localeButtonColor"));
			jsonObj.put("mainButtonTextColor", byodUtil.getGeneralConfig(connection, "mainButtonTextColor"));
			jsonObj.put("mainButtonBackgroundColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundColor"));
			jsonObj.put("mainButtonBackgroundHoverColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundHoverColor"));
			jsonObj.put("mainButtonBackgroundFocusColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundFocusColor"));
		} catch (Exception ex) {
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
		return jsonObj;
	}
	
	public JSONObject getBrandSetting(Long brandId, Connection connection) throws Exception {
		JSONObject jsonObj = new JSONObject();
		try {
			connection = dbConnectionUtil.getConnection(brandId);
			jsonObj.put("mainLogoPath", byodUtil.getGeneralConfig(connection, "mainLogoPath"));
			jsonObj.put("shortcutLogoPath", byodUtil.getGeneralConfig(connection, "shortcutLogoPath"));
			jsonObj.put("mainBackgroundPath", byodUtil.getGeneralConfig(connection, "mainBackgroundPath"));	
			jsonObj.put("landingLogoPath", byodUtil.getGeneralConfig(connection, "landingLogoPath"));
			jsonObj.put("appName", byodUtil.getGeneralConfig(connection, "appName"));
			jsonObj.put("mainColor", byodUtil.getGeneralConfig(connection, "mainColor"));
			jsonObj.put("subColor", byodUtil.getGeneralConfig(connection, "subColor"));
			jsonObj.put("mainTextColor", byodUtil.getGeneralConfig(connection, "mainTextColor"));
			jsonObj.put("subTextColor", byodUtil.getGeneralConfig(connection, "subTextColor"));
			jsonObj.put("localeButtonColor", byodUtil.getGeneralConfig(connection, "localeButtonColor"));
			jsonObj.put("mainButtonTextColor", byodUtil.getGeneralConfig(connection, "mainButtonTextColor"));
			jsonObj.put("mainButtonBackgroundColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundColor"));
			jsonObj.put("mainButtonBackgroundHoverColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundHoverColor"));
			jsonObj.put("mainButtonBackgroundFocusColor", byodUtil.getGeneralConfig(connection, "mainButtonBackgroundFocusColor"));
		} catch (Exception ex) {
			throw ex;
		} 
		return jsonObj;
	}
	
	
}
