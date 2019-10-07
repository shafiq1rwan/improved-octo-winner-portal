package my.com.byod.login.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class GeneralConfig {

	/**
	 * @author shafiq.irwan
	 * @date 27/09/2019
	 */

	@Autowired
	private DataSource dataSource;

	public JSONObject getConfigJSON(String param) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query = "SELECT config_value FROM general_config WHERE config_name = ?";
		JSONObject temp = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(query);
			stmt.setString(1, param);

			rs = stmt.executeQuery();

			while (rs.next()) {
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
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return temp;
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
			stmt.setString(1, param);

			rs = stmt.executeQuery();

			while (rs.next()) {
				result = rs.getString("config_value");
				System.out.println("result" + result);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
