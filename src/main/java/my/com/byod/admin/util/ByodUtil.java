package my.com.byod.admin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ByodUtil {

	@Value("${upload-path}")
	private String filePath;

	private DbConnectionUtil dbConnectionUtil;
	private HttpServletRequest request;
	
	@Autowired
	public ByodUtil(DbConnectionUtil dbConnectionUtil, HttpServletRequest request) {
		this.dbConnectionUtil = dbConnectionUtil;
		this.request = request;
	}

	public String createUniqueBackendId(String prefix) throws ParseException {
		String resultString = "";
		Date currentDate = new Date();	
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("ddMMyyyy");
		int sequence = getSequence(prefix);
		
		resultString = prefix + "_" + standardDateFormat.format(currentDate) + sequence;
		return resultString;
	}

	private int getSequence(String code) throws ParseException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		boolean isSameDay = false;
		int sequence = 0;
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Map<String, Object> backendSequenceResult = jdbcTemplate.queryForMap("SELECT * FROM backend_sequence WHERE backend_sequence_code = ?", new Object[] {code});
//		Date databaseDate = (Date) backendSequenceResult.get("modified_date");
		Date databaseDate = sdf.parse(backendSequenceResult.get("modified_date").toString().replace("T", " "));
		Date currentDate = new Date();
		
		System.out.println("Yesterday " + standardDateFormat.format(databaseDate));
		System.out.println("Today " + standardDateFormat.format(currentDate));
		
		if (standardDateFormat.format(databaseDate).compareTo(standardDateFormat.format(currentDate)) == 0)
			isSameDay = true;

		if (isSameDay)
			sequence = (int) backendSequenceResult.get("backend_sequence") + 1;
		else
			sequence = 1;

		jdbcTemplate.update("UPDATE backend_sequence SET backend_sequence = ? ,modified_date = ? WHERE backend_sequence_code = ?", new Object[] { sequence, currentDate,code });
		return sequence;
	}
	
	public String createUniqueActivationId(String prefix) throws ParseException {
		String resultString = "";
		Date currentDate = new Date();	
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("ddMMyyyy");
		String sequence = getActivationIdSequence(prefix);
		
		resultString = prefix + standardDateFormat.format(currentDate) + sequence;
		return resultString;
	}

	private String getActivationIdSequence(String code) throws ParseException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dbConnectionUtil.setupDataSource(request));
		boolean isSameDay = false;
		int sequence = 0;
		SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Map<String, Object> backendSequenceResult = jdbcTemplate.queryForMap("SELECT * FROM device_type_lookup WHERE prefix = ?", new Object[] {code});
		Date databaseDate = sdf.parse(backendSequenceResult.get("modified_date").toString().replace("T", " "));
		Date currentDate = new Date();
		
		System.out.println("Yesterday " + standardDateFormat.format(databaseDate));
		System.out.println("Today " + standardDateFormat.format(currentDate));
		
		if (standardDateFormat.format(databaseDate).compareTo(standardDateFormat.format(currentDate)) == 0)
			isSameDay = true;

		if (isSameDay)
			sequence = (int) backendSequenceResult.get("backend_sequence") + 1;
		else
			sequence = 1;
		
		jdbcTemplate.update("UPDATE device_type_lookup SET backend_sequence = ? ,modified_date = ? WHERE prefix = ?", new Object[] { sequence, currentDate,code });
		return String.format("%05d", sequence);
	}


	public String createRandomDigit(int length) {
		String possibleChar = "0123456789";
		StringBuilder builder = new StringBuilder();
		Random random = new Random();

		while (builder.length() < length) {
			int index = (int) (random.nextFloat() * possibleChar.length());
			builder.append(possibleChar.charAt(index));
		}
		return builder.toString();
	}

/*	public String saveImageFile(String base64_img, String existing) {
		boolean checker = false;
		String uploadPath = filePath;
		String imageName = createRandomString(10);
		String[] splitString = base64_img.split(",");
		byte[] imageBytes = Base64.getDecoder().decode(splitString[1]);

		try {
			File checkdir = new File(uploadPath);
			checkdir.mkdirs();

			if (existing != null) {
				File tempfile = new File(uploadPath + existing);
				tempfile.delete();
			}

			do {
				File checkFile = new File(uploadPath + imageName);
				if (checkFile.exists()) {
					checker = true;
					imageName = createRandomString(10);
				} else {
					checker = false;
				}
			} while (checker);

			File file = new File(uploadPath, imageName + ".png");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(imageBytes);
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return imageName + ".png";
	}*/
	
	public String saveImageFile(String brandId, String image_prefix, String base64_img, String existing) throws ParseException {
		boolean checker = false;
		String uploadPath = filePath;
		String imageName = createUniqueBackendId(image_prefix);
		String[] splitString = base64_img.split(",");
		byte[] imageBytes = Base64.getDecoder().decode(splitString[1]);

		try {
			File checkdir = new File(uploadPath + brandId);
			checkdir.mkdirs();

			if (existing != null) {
				File tempfile = new File(uploadPath + brandId, existing);
				tempfile.delete();
			}

			do {
				File checkFile = new File(uploadPath + brandId, imageName);
				if (checkFile.exists()) {
					checker = true;
					imageName = createUniqueBackendId(image_prefix);
				} else {
					checker = false;
				}
			} while (checker);

			File file = new File(uploadPath + brandId, imageName + ".png");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(imageBytes);
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return imageName + ".png";
	}
	
	public static String genSecureHash(String mdInstance, String originalString) {
		MessageDigest md = null;
		byte[] ba = null;
		
		//create the md hash and ISO-8859-1 encode it
		try {
			md = MessageDigest.getInstance(mdInstance);
			ba = md.digest(originalString.getBytes("ISO-8859-1"));
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	    return hex(ba);   
	}
	
	static String hex(byte[] input) {
		// create a StringBuffer 2x the size of the hash array
		StringBuffer sb = new StringBuffer(input.length * 2);

		// retrieve the byte array data, convert it to hex and add it to the StringBuffer
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		
		return sb.toString();
	}
	
	static final char[] HEX_TABLE = new char[] {
			'0', '1', '2', '3', '4', '5', '6', '7', 
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	public String createRandomString(int length) {
		String possibleChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder builder = new StringBuilder();
		Random random = new Random();

		while (builder.length() < length) {
			int index = (int) (random.nextFloat() * possibleChar.length());
			builder.append(possibleChar.charAt(index));
		}
		return builder.toString();
	}
	
	public String getGeneralConfig(Connection connection, String parameter) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String value = null;
		try {
			stmt = connection.prepareStatement("SELECT * FROM general_configuration WHERE parameter = ?");
			stmt.setString(1, parameter);
			rs = (ResultSet) stmt.executeQuery();

			if(rs.next()) {
				value = rs.getString("value");		
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
		return value;
	}
	
	public boolean updateGeneralConfig(Connection connection, String parameter, String value) throws Exception {
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			stmt = connection.prepareStatement("UPDATE general_configuration SET value = ? WHERE parameter = ?");
			stmt.setString(1, value);
			stmt.setString(2, parameter);
			int rowAffected = stmt.executeUpdate();
			
			if(rowAffected != 0) {
				flag = true;		
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return flag;
	}
}
