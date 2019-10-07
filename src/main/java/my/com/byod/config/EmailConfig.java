package my.com.byod.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public JavaMailSender getJavaMailSender() throws JSONException {
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(getConfig().getString("mail.host"));
		mailSender.setPort(getConfig().getInt("mail.port"));
		
		mailSender.setUsername(getConfig().getString("mail.username"));
		mailSender.setPassword(getConfig().getString("mail.password"));
	
		Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", getConfig().getString("mail.transport.protocol"));
	    props.put("mail.smtp.auth", getConfig().getString("mail.smtp.auth"));
	    props.put("mail.smtp.starttls.enable", getConfig().getString("mail.smtp.starttls.enable"));
	    props.put("mail.debug", getConfig().getString("mail.debug"));
	    return mailSender;
	}
	
	public JSONObject getConfig() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "SELECT config_value FROM general_config WHERE config_name = ?";
		JSONObject temp = null;
		
		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(query);
			stmt.setString(1,"mail_properties");
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				try {
					temp = new JSONObject(rs.getString("config_value"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		return temp;
	}

}
