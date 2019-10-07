package my.com.byod.admin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class UserEmailUtil {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private DataSource dataSource;
	
	public boolean sendUserRegisterPassword(String username, String randomPass ,String email) {

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String sender = getConfigStr("mail_sender");
			String portal_url = getConfigStr("portal_url");
			
			helper.setFrom(sender);
			helper.setTo(email);
			helper.setSubject("Cloud Login Password");
			helper.setText("Dear " + username + ","
					+ "<br><br>Thank you for registering with our Cloud solution. Here is you password for login :" + randomPass
					+ "<br>Please click on the link to login : <a href='"+portal_url+"'>Cloud Portal</a>", true);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendActivationInfo(String username, JSONObject activationInfo , String brandId, String email) {

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			String sender = getConfigStr("mail_sender");
			
			helper.setFrom(sender);
			helper.setTo(email);
			helper.setSubject(activationInfo.getString("device") + " Activation Info");
			helper.setText("Dear " + username + ","
					+ "<br><br>Kindly be informed that your activation info for "+ activationInfo.getString("device") +" are as followed,"
					+ "<br>Brand ID : " + brandId
					+ "<br>Activation ID : " + activationInfo.getString("activation_id")
					+ "<br>Activation Key: " + activationInfo.getString("activation_key")
					+ "<br><br>Please use the activation info to activate your device.", true);

			emailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getConfigStr(String param) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "SELECT config_value FROM general_config WHERE config_name = ?";
		String result = "";
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(query);
			stmt.setString(1,param);
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				result = rs.getString("config_value");
				System.out.println("result" +result);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
