package my.com.byod.admin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccessRightsUtil {

	@Autowired
	private DataSource dataSource;

	public JSONObject setupAccessRightByUsername(String username, Long brandId, HttpServletRequest request) {
		JSONObject jsonResult = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String role = auth.getAuthorities().iterator().next().toString();

			if (role.equals("ROLE_SUPER_ADMIN")) {
				HttpSession session = request.getSession();
				jsonResult = convertAccessRightsToJSON(null, role);
				jsonResult.put("role", role);
				session.setAttribute("access_rights", jsonResult);

			} else {
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(
						"SELECT ub.permission FROM users_brands ub INNER JOIN users u ON ub.user_id = u.id WHERE u.username = ? AND ub.brand_id = ?");
				stmt.setString(1, username);
				stmt.setLong(2, brandId);
				rs = stmt.executeQuery();

				if (rs.next()) {
					HttpSession session = request.getSession();
					jsonResult = convertAccessRightsToJSON(rs.getString("permission"), role);
					session.setAttribute("access_rights", jsonResult);
				}
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return jsonResult;
	}

	private JSONObject convertAccessRightsToJSON(String permission, String role) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonResult = null;

		try {
			JSONObject obj = new JSONObject();
			jsonResult = new JSONObject();

			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("SELECT * FROM permission_lookup");
			rs = stmt.executeQuery();

			if (role.equals("ROLE_SUPER_ADMIN")) {
				while (rs.next()) {
					obj.put(rs.getString("perm_name"), true);
				}
			} else {
				int index = 0;
				boolean[] permissions = hexToBinaryBoolean(permission);

				while (rs.next()) {
					obj.put(rs.getString("perm_name"), permissions[index]);
					index++;
				}
			}
			jsonResult.put("accessRights", obj);
			System.out.println("Access Rights Result: " + jsonResult.toString());
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonResult;
	}

	public boolean[] hexToBinaryBoolean(String hexString) {
		int i = Integer.parseInt(hexString, 16);
		String binaryString = String.format("%8s", Integer.toBinaryString(i)).replace(" ", "0");
		String[] resultArray = binaryString.split("");

		System.out.println("MyHex " + hexString);
		System.out.println("MyBinary " + binaryString);

		boolean[] permissionBooleans = new boolean[resultArray.length];

		for (int j = 0; j < permissionBooleans.length; j++) {
			permissionBooleans[j] = (!resultArray[j].equals("0"));
			// System.out.println(permissionBooleans[j]);
		}

		return permissionBooleans;
	}

}
