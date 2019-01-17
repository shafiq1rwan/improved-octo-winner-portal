package my.com.byod.admin.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu/combo")
public class ComboRestController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

/*	@PostMapping(value = "/createComboDetail", produces = "application/json")
	public ResponseEntity<?> createComboDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			// JSONObject jsonComboData = new JSONObject(data);
			JSONArray jsonComboDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();

			for (int i = 0; i < jsonComboDetailArray.length(); i++) {
				int index = i + 1;
				JSONObject jsonComboDetailObj = jsonComboDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement(
						"INSERT INTO combo_detail(menu_item_id, combo_detail_name, combo_detail_quantity, combo_detail_sequence) VALUES (?,?,?,?)");
				stmt.setLong(1, jsonComboDetailObj.getLong("menu_item_id"));
				stmt.setString(2, jsonComboDetailObj.getString("combo_detail_name"));
				stmt.setInt(3, jsonComboDetailObj.getInt("combo_detail_quantity"));
				stmt.setInt(4, index);
				stmt.executeUpdate();
			}
			return ResponseEntity.ok(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	@PostMapping(value = "/createComboDetail", produces = "application/json")
	public ResponseEntity<?> createComboDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONObject jsonComboDetailData = new JSONObject(data);
			connection = dataSource.getConnection();
			
				stmt = connection.prepareStatement(
						"INSERT INTO combo_detail(menu_item_id, combo_detail_name, combo_detail_quantity, combo_detail_sequence) VALUES (?,?,?,?)");
				stmt.setLong(1, jsonComboDetailData.getLong("menu_item_id"));
				stmt.setString(2, jsonComboDetailData.getString("combo_detail_name"));
				stmt.setInt(3, jsonComboDetailData.getInt("combo_detail_quantity"));
				stmt.setInt(4, checkComboDetailSequence(jsonComboDetailData.getLong("menu_item_id"))+ 1);
				int rowAffected = stmt.executeUpdate();
				
				if(rowAffected == 0)
					return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot create combo detail");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}
	
	private int checkComboDetailSequence(Long menu_item_id) {
		try {
			return jdbcTemplate.queryForObject("SELECT TOP 1 combo_detail_sequence FROM combo_detail WHERE menu_item_id = ? ORDER BY combo_detail_sequence DESC", new Object[] {menu_item_id}, Integer.class);
		} catch(DataAccessException ex) {
			return 0;
		}
	}

	@GetMapping(value = "/getComboDetailByMenuItemId", produces = "application/json")
	public ResponseEntity<?> getComboDetailByMenuItemId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("menuItemId") Long menuItemId) {
		JSONArray jsonComboDetailArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(
					"SELECT * FROM combo_detail WHERE menu_item_id = ? ORDER BY combo_detail_sequence");
			stmt.setLong(1, menuItemId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonComboDetailObj = new JSONObject();
				jsonComboDetailObj.put("id", rs.getLong("id"));
				jsonComboDetailObj.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonComboDetailObj.put("name", rs.getString("combo_detail_name"));
				jsonComboDetailObj.put("quantity", rs.getInt("combo_detail_quantity"));
				jsonComboDetailObj.put("order", rs.getInt("combo_detail_sequence"));

				jsonComboDetailArray.put(jsonComboDetailObj);
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
		return ResponseEntity.ok(jsonComboDetailArray.toString());
	}
	
	@GetMapping(value = "/getComboDetailById", produces = "application/json")
	public ResponseEntity<?> getComboDetailById(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		JSONObject jsonResult = new JSONObject();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(
					"SELECT * FROM combo_detail WHERE id = ?");
			stmt.setLong(1, id);
			rs = (ResultSet) stmt.executeQuery();

			if (rs.next()) {
				jsonResult.put("id", rs.getLong("id"));
				jsonResult.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonResult.put("name", rs.getString("combo_detail_name"));
				jsonResult.put("quantity", rs.getInt("combo_detail_quantity"));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body("Combo detail not found");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(jsonResult.toString());
	}

	@PostMapping(value = "/editComboDetail", produces = "application/json")
	public ResponseEntity<?> editComboDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;		

		try {
			JSONObject jsonComboDetailData = new JSONObject(data);
				connection = dataSource.getConnection();
				stmt = connection.prepareStatement(
						"UPDATE combo_detail SET combo_detail_name = ?, combo_detail_quantity = ? WHERE id = ?");
				stmt.setString(1, jsonComboDetailData.getString("combo_detail_name"));
				stmt.setInt(2, jsonComboDetailData.getInt("combo_detail_quantity"));
				stmt.setLong(3, jsonComboDetailData.getLong("id"));
				int affectedRow = stmt.executeUpdate();
				
				if(affectedRow == 0) {
					return ResponseEntity.badRequest().body("Cannot update combo detail");
				}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}
	
	@PostMapping(value = "/editComboDetailSequence", produces = "application/json")
	public ResponseEntity<?> editComboDetailSequence(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;	
		PreparedStatement stmt2 = null;

		try {
			JSONObject jsonComboDetailData = new JSONObject(data);
			Long menuItemId = jsonComboDetailData.getLong("menu_item_id");
			JSONArray jsonComboDetailArray = jsonComboDetailData.getJSONArray("tier_items");
			
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			//Blank all Sequence
			stmt = connection.prepareStatement(
						"UPDATE combo_detail SET combo_detail_sequence = 0 WHERE menu_item_id = ?");
			stmt.setLong(1, menuItemId);
			stmt.executeUpdate();

			//Reassign All Sequence
			stmt2 = connection.prepareStatement("UPDATE combo_detail SET combo_detail_sequence = ? WHERE id = ?");
			for(int i=0;i<jsonComboDetailArray.length();i++) {
				int index = i + 1;
				JSONObject jsonObj = jsonComboDetailArray.getJSONObject(i);	
				stmt2.setLong(1, index);
				stmt2.setLong(2, jsonObj.getLong("id"));
				stmt2.executeUpdate();
			}
			connection.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	// TODO delete combo detail item
	@DeleteMapping(value = "/deleteComboDetail", produces = "application/json")
	public ResponseEntity<?> deleteComboDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {

		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM combo_detail WHERE id = ?");
			stmt.setLong(1, id);
			int deletedRow = stmt.executeUpdate();

			if (deletedRow == 0) {
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot delete combo detail");
			} else {			
				stmt = connection.prepareStatement("DELETE FROM combo_item_detail WHERE combo_detail_id = ?");
				stmt.setLong(1, id);
				stmt.executeUpdate();
				// may change
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	// =============== Combo Item Details =========================

	@GetMapping(value = "/getComboItemDetailByComboDetailId", produces = "application/json")
	public ResponseEntity<?> getComboItemDetailByComboDetailId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("comboDetailId") Long comboDetailId) {
		JSONArray jsonComboItemDetailArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(
					"SELECT * FROM combo_item_detail WHERE combo_detail_id = ? ORDER BY combo_item_detail_sequence");
			stmt.setLong(1, comboDetailId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonComboItemDetailObj = new JSONObject();
				jsonComboItemDetailObj.put("id", rs.getLong("id"));
				jsonComboItemDetailObj.put("combo_detail_id", rs.getLong("combo_detail_id"));
				jsonComboItemDetailObj.put("menu_item_id", rs.getLong("menu_item_id"));
				jsonComboItemDetailObj.put("menu_item_group_id", rs.getLong("menu_item_group_id"));
				jsonComboItemDetailObj.put("combo_item_detail_sequence", rs.getInt("combo_item_detail_sequence"));

				jsonComboItemDetailArray.put(jsonComboItemDetailObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(jsonComboItemDetailArray.toString());
	}

	@PostMapping(value = "/createComboItemDetail", produces = "application/json")
	public ResponseEntity<?> createComboItemDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {		
			JSONObject jsonComboItemDetail = new JSONObject(data);
			JSONArray jsonComboItemDetailArray = jsonComboItemDetail.getJSONArray("item_arrays");

			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			String InsertionSql = "INSERT INTO combo_item_detail(combo_detail_id, menu_item_id, menu_item_group_id, combo_item_detail_sequence) VALUES (?,?,?,?)";
			for(int count=0; count<jsonComboItemDetailArray.length() -1;count++) {
				InsertionSql += ",(?,?,?,?)";
			}
			
			System.out.println(InsertionSql);
			stmt = connection.prepareStatement(InsertionSql);
			int index = 0;
			
			for (int i = 0; i < jsonComboItemDetailArray.length(); i++) {
				JSONObject jsonComboItemDetailObj = jsonComboItemDetailArray.getJSONObject(i);	
				stmt.setLong(++index, jsonComboItemDetail.getLong("combo_detail_id"));

				if (jsonComboItemDetailObj.getString("type").equals("Item")) {
					stmt.setLong(++index, jsonComboItemDetailObj.getLong("id"));
					stmt.setLong(++index, 0);
				} else if (jsonComboItemDetailObj.getString("type").equals("Group")) {
					stmt.setLong(++index, 0);
					stmt.setLong(++index, jsonComboItemDetailObj.getLong("id"));
				}
				stmt.setInt(++index, getComboItemDetailSequence(jsonComboItemDetail.getLong("combo_detail_id"))+(i+1));
				System.out.println("Index count: " + index);
			}
			
			stmt.executeUpdate();
			System.out.println("Index Result: " + index);
			
			connection.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	private int getComboItemDetailSequence(long comboDetailId) {		
		try {			
			return jdbcTemplate.queryForObject("SELECT TOP 1 combo_item_detail_sequence FROM combo_item_detail WHERE combo_detail_id = ? ORDER BY combo_item_detail_sequence DESC",
					new Object[] { comboDetailId }, Integer.class);
		} catch(Exception ex) {
			return 0;
		}
	}

	@PostMapping(value = "/editComboItemDetail", produces = "application/json")
	public ResponseEntity<?> editComboItemDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			JSONArray jsonComboItemDetailArray = new JSONArray(data);
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			for (int i = 0; i < jsonComboItemDetailArray.length(); i++) {
				JSONObject jsonComboItemDetailObj = jsonComboItemDetailArray.getJSONObject(i);
				stmt = connection.prepareStatement(
						"UPDATE combo_item_detail SET combo_item_detail_sequence = ? WHERE id = ?");
				stmt.setInt(1, jsonComboItemDetailObj.getInt("sequence"));
				stmt.setLong(2, jsonComboItemDetailObj.getLong("id"));
				stmt.executeUpdate();
			}
			
			connection.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	@DeleteMapping(value = "/deleteComboItemDetail", produces = "application/json")
	public ResponseEntity<?> deleteComboItemDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = dataSource.getConnection();
			stmt = connection.prepareStatement("DELETE FROM combo_item_detail WHERE id = ?");
			stmt.setLong(1, id);
			int deletedRow = stmt.executeUpdate();

			if (deletedRow == 0)
				return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("Cannot delete combo item detail");	
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ResponseEntity.ok(null);
	}

	@GetMapping(value = "/getMenuItemAndItemGroupInCombo", produces = "application/json")
	public ResponseEntity<?> getMenuItemAndItemGroupInCombo(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("comboDetailId") Long comboDetailId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		//ResultSet rs3 = null;

		try {
			List<JSONObject> menuItemAndGroupJsonList = new ArrayList<>();

			connection = dataSource.getConnection();
			stmt = connection.prepareStatement(
					"SELECT mi.*, cid.id AS combo_item_detail_id, cid.combo_item_detail_sequence FROM menu_item mi INNER JOIN combo_item_detail cid ON mi.id = cid.menu_item_id WHERE cid.combo_detail_id = ? AND mi.menu_item_type = 0");
			stmt.setLong(1, comboDetailId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonMenuItemObj = new JSONObject();
				jsonMenuItemObj.put("combo_item_detail_id", rs.getLong("combo_item_detail_id"));
				jsonMenuItemObj.put("id", rs.getLong("id"));
				jsonMenuItemObj.put("name", rs.getString("menu_item_name"));
				jsonMenuItemObj.put("type", "Item");
				jsonMenuItemObj.put("price", rs.getBigDecimal("menu_item_base_price"));
				jsonMenuItemObj.put("sequence", rs.getInt("combo_item_detail_sequence"));

				menuItemAndGroupJsonList.add(jsonMenuItemObj);
			}

			stmt = connection.prepareStatement(
					"SELECT mig.*, cid.id AS combo_item_detail_id, cid.combo_item_detail_sequence FROM menu_item_group mig INNER JOIN combo_item_detail cid ON mig.id = cid.menu_item_group_id WHERE cid.combo_detail_id = ?");
			stmt.setLong(1, comboDetailId);
			rs2 = (ResultSet) stmt.executeQuery();

			while (rs2.next()) {
				JSONObject jsonMenuItemGroupObj = new JSONObject();
				jsonMenuItemGroupObj.put("combo_item_detail_id", rs2.getLong("combo_item_detail_id"));
				jsonMenuItemGroupObj.put("id", rs2.getLong("id"));
				jsonMenuItemGroupObj.put("name", rs2.getString("menu_item_group_name"));
				jsonMenuItemGroupObj.put("type", "Group");
				jsonMenuItemGroupObj.put("price", "");
				jsonMenuItemGroupObj.put("sequence", rs2.getInt("combo_item_detail_sequence"));

/*				stmt = connection.prepareStatement(
						"SELECT mi.* FROM menu_item mi INNER JOIN menu_item_group_sequence migs ON mi.id = migs.menu_item_id WHERE migs.menu_item_group_id = ? ORDER BY migs.menu_item_group_sequence");
				stmt.setLong(1, rs2.getLong("id"));
				rs3 = (ResultSet) stmt.executeQuery();*/

				/*JSONArray jsonItemGroupMenuItemArray = new JSONArray();*/

			/*	while (rs3.next()) {
					JSONObject jsonMenuItemObj = new JSONObject();
					jsonMenuItemObj.put("id", rs3.getLong("id"));
					jsonMenuItemObj.put("name", rs3.getString("menu_item_name"));
					jsonMenuItemObj.put("price", rs3.getBigDecimal("menu_item_base_price"));

					jsonItemGroupMenuItemArray.put(jsonMenuItemObj);
				}

				jsonMenuItemGroupObj.put("menu_items", jsonItemGroupMenuItemArray);*/

				menuItemAndGroupJsonList.add(jsonMenuItemGroupObj);
			}

			Collections.sort(menuItemAndGroupJsonList, (jsonObjectA, jsonObjectB) -> {
				int compare = 0;
				try {
					int keyA = jsonObjectA.getInt("sequence");
					int keyB = jsonObjectB.getInt("sequence");
					compare = Integer.compare(keyA, keyB);
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
				return compare;
			});
			JSONArray jsonMenuItemAndGroupArray = new JSONArray(menuItemAndGroupJsonList);
			System.out.println("Returned Result: " + jsonMenuItemAndGroupArray.toString());
			return ResponseEntity.ok(jsonMenuItemAndGroupArray.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Internal Server Error");
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

}
