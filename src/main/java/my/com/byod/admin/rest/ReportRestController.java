package my.com.byod.admin.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/report")
public class ReportRestController {

	@Autowired
	private DbConnectionUtil dbConnectionUtil;

	@RequestMapping(value = {
			"/transaction_report/{date1}/{date2}/{reportType}/{store}/{employee}/{paymentType}" }, method = {
					RequestMethod.GET })
	public void generateTransactionReport(@PathVariable String date1, @PathVariable String date2,
			@PathVariable String reportType, @PathVariable String store, @PathVariable String employee,
			@PathVariable String paymentType, HttpServletRequest request, HttpServletResponse response) {

		Connection connection = null;

		try {

			System.out.println("date1: " + date1);
			System.out.println("date2: " + date2);
			System.out.println("reportType: " + reportType);
			System.out.println("storeName: " + store);
			System.out.println("employeeName: " + employee);

			String subStr1 = date1.substring(0, 10);
			String subStr2 = date2.substring(0, 10);

			Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(subStr2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datePlusOne);
			cal.add(Calendar.DATE, 1);
			datePlusOne = cal.getTime();

			String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);

			if (reportType.equalsIgnoreCase("1")) {
				salesByStoreReport(subStr1, newSubStr2, request, connection, response, store);
			} else if (reportType.equalsIgnoreCase("2")) {
				bestSellingItemReport(subStr1, newSubStr2, request, connection, response, store);
			} else if (reportType.equalsIgnoreCase("3") || reportType.equalsIgnoreCase("4")) {
				String title = "";

				if (reportType.equalsIgnoreCase("3")) {
					title = "Sales by Employee Report";
				} else if (reportType.equalsIgnoreCase("4")) {
					title = "Sales by Payment Type Report";
				}

				summaryReport(subStr1, newSubStr2, request, connection, response, store, title, employee, paymentType,
						reportType);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JSONArray getStateLookup(Connection connection) throws Exception {
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.prepareStatement("SELECT * FROM state_lookup ");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("name"));
				jsonArr.put(jsonObj);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return jsonArr;
	}

	@GetMapping(value = "/getState", produces = "application/json")
	public ResponseEntity<?> getAllStoreLookup(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonResult = new JSONArray();
		Connection connection = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonResult = getStateLookup(connection);
			return ResponseEntity.ok(jsonResult.toString());
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
	}

	@GetMapping(value = {
			"/transaction_report_list/{date1}/{date2}/{reportType}/{store}/{employee}/{paymentType}" }, produces = "application/json")
	public ResponseEntity<?> getTransactionReport(@PathVariable String date1, @PathVariable String date2,
			@PathVariable String reportType, @PathVariable String store, @PathVariable String employee,
			@PathVariable String paymentType, HttpServletRequest request, HttpServletResponse response) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();

		System.out.println("date1: " + date1);
		System.out.println("date2: " + date2);
		System.out.println("reportType: " + reportType);
		System.out.println("storeName: " + store);
		System.out.println("employeeName: " + employee);
		System.out.println("paymentType: " + paymentType);

		try {

			String subStr1 = date1.substring(0, 10);
			String subStr2 = date2.substring(0, 10);

			Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(subStr2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datePlusOne);
			cal.add(Calendar.DATE, 1);
			datePlusOne = cal.getTime();

			String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);
			StringBuffer query = new StringBuffer(
					"select st.store_name as store_name, st.store_address as store_address, ");
			query.append("stf.staff_name as staff_name, pml.name as method_pay, ptl.name as type_pay, ");
			query.append("truncate(tt.transaction_amount,2) as money, tt.created_date as trx_date ");
			// Add receipt number
			query.append(", cc.receipt_number as receipt_number ");
			query.append("from transaction tt ");
			query.append("left join `check` cc on (tt.check_id = cc.id) ");
			query.append("left join payment_method_lookup pml on (tt.payment_method = pml.id) ");
			query.append("left join payment_type_lookup ptl on (tt.payment_type = ptl.id) ");
			query.append("left join store st on (tt.store_id = st.id) ");
			query.append("left join staff stf on (tt.staff_id = stf.id) ");
			query.append("where tt.transaction_status = 3 ");
			query.append("and tt.created_date between '" + subStr1 + "' and '" + newSubStr2 + "'");

			if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
				query.append(" and st.id = " + store);
			}

			if (!employee.equalsIgnoreCase("undefined") && !employee.equalsIgnoreCase("0")) {
				query.append(" and stf.id = " + employee);
			}

			if (!paymentType.equalsIgnoreCase("undefined") && !paymentType.equalsIgnoreCase("0")) {
				query.append(" and pml.id = " + paymentType);
			}

			query.append(" ORDER BY tt.created_date DESC");

			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();
			int i = 1;

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("no", i++);
				jsonObj.put("store_name", rs.getString("store_name"));
				jsonObj.put("store_address", rs.getString("store_address"));
				jsonObj.put("staff_name", rs.getString("staff_name"));
				jsonObj.put("method_pay", rs.getString("method_pay"));
				jsonObj.put("type_pay", rs.getString("type_pay"));
				jsonObj.put("money", rs.getString("money"));
				jsonObj.put("trx_date", rs.getString("trx_date"));
				jsonObj.put("receipt_number", rs.getString("receipt_number"));
				jsonArray.put(jsonObj);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().body(jsonArray.toString());
	}

	public void summaryReport(String subStr1, String subStr2, HttpServletRequest request, Connection connection,
			HttpServletResponse response, String store, String title, String employee, String paymentType,
			String reportType) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer(
				"select st.store_name as store_name, st.store_address as store_address, stf.staff_name as staff_name, pml.name as method_pay, ptl.name as type_pay, ");
		query.append("truncate(tt.transaction_amount,2) as money, tt.created_date as trx_date ");
		// Add receipt number
		query.append(", cc.receipt_number as receipt_number ");
		query.append("from transaction tt ");
		query.append("left join `check` cc on (tt.check_id = cc.id) ");
		query.append("left join payment_method_lookup pml on (tt.payment_method = pml.id) ");
		query.append("left join payment_type_lookup ptl on (tt.payment_type = ptl.id) ");
		query.append("left join store st on (tt.store_id = st.id) ");
		query.append("left join staff stf on (tt.staff_id = stf.id) ");
		query.append("where tt.transaction_status = 3 ");
		query.append("and tt.created_date between '" + subStr1 + "' and '" + subStr2 + "' ");

		if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
			query.append(" and st.id = " + store);
		}

		if (!employee.equalsIgnoreCase("undefined") && !employee.equalsIgnoreCase("0")) {
			query.append(" and stf.id = " + employee);
		}

		if (!paymentType.equalsIgnoreCase("undefined") && !paymentType.equalsIgnoreCase("0")) {
			query.append(" and pml.id = " + paymentType);
		}

		query.append(" ORDER BY tt.created_date DESC");

		connection = dbConnectionUtil.retrieveConnection(request);
		try {
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/ddHH:mm:ss");
			LocalDateTime now = LocalDateTime.now();

			// create a small spreadsheet
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
					0, // last row (0-based)
					0, // first column (0-based)
					7 // last column (0-based)
			));

			int iNumbering = 1;
			int iLoopData = 0;
			double totalSales = 0.00;

			// Report Title
			row = sheet.createRow(iLoopData++);
			HSSFCell cell = row.createCell(0);
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBold(true);
			style.setFont(font);

			CellStyle style2 = wb.createCellStyle();
			style2.setBorderTop(BorderStyle.DOUBLE);
			style2.setBorderBottom(BorderStyle.DOUBLE);

			cell = row.createCell(0);
			cell.setCellValue(title);
			cell.setCellStyle(style);

			// Report Header
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("#");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("Branch Name");
//			cell.setCellStyle(style);
//			cell = row.createCell(2);
//			cell.setCellValue("Branch Address");

			if (reportType.equalsIgnoreCase("3")) {
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("Staff Name");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("Sales (RM)");
				cell.setCellStyle(style);
				cell = row.createCell(4);
				cell.setCellValue("Date");
				cell.setCellStyle(style);
			} else if (reportType.equalsIgnoreCase("4")) {
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("Receipt Number");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("Payment Type");
				cell.setCellStyle(style);
				cell = row.createCell(4);
				cell.setCellValue("Payment Method");
				cell.setCellStyle(style);
				cell = row.createCell(5);
				cell.setCellValue("Sales (RM)");
				cell.setCellStyle(style);
				cell = row.createCell(6);
				cell.setCellValue("Date");
				cell.setCellStyle(style);
			}

			while (rs.next()) {

				// Report Content
				row = sheet.createRow(iLoopData++);
				cell = row.createCell(0);
				cell.setCellValue(iNumbering++);
				cell = row.createCell(1);
				cell.setCellValue(rs.getString("store_name"));
//				cell = row.createCell(2);
//				cell.setCellValue(rs.getString("store_address"));

				if (reportType.equalsIgnoreCase("3")) {
					cell = row.createCell(2);
					cell.setCellValue(rs.getString("staff_name"));
					cell = row.createCell(3);
					cell.setCellValue(rs.getDouble("money"));
					cell = row.createCell(4);
					cell.setCellValue(rs.getString("trx_date"));
				} else if (reportType.equalsIgnoreCase("4")) {
					cell = row.createCell(2);
					cell.setCellValue(rs.getString("receipt_number"));
					cell = row.createCell(3);
					cell.setCellValue(rs.getString("method_pay"));
					cell = row.createCell(4);
					cell.setCellValue(rs.getString("type_pay"));
					cell = row.createCell(5);
					cell.setCellValue(rs.getDouble("money"));
					cell = row.createCell(6);
					cell.setCellValue(rs.getString("trx_date"));
				}

				totalSales = totalSales + rs.getDouble("money");

			}

			// Get total sales
//			row = sheet.createRow(iLoopData++);
//			row = sheet.createRow(iLoopData++);
//			cell = row.createCell(0);
//			cell.setCellValue("");
//			cell = row.createCell(1);
//			cell.setCellValue("");
//			cell = row.createCell(2);
//			cell.setCellValue("");
//			cell = row.createCell(3);
//			cell.setCellValue("");
//			cell = row.createCell(4);
//			cell.setCellValue("");
//			cell = row.createCell(5);
//			cell.setCellValue("Total Sales");
//			cell = row.createCell(6);
//			cell.setCellValue(totalSales);
//			cell.setCellStyle(style2);
//			cell = row.createCell(7);
//			cell.setCellValue("");

			// Adjust content to fit column size
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);

			// write it as an excel attachment
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition",
					"attachment; filename=" + title.replaceAll("\\s+", "") + dtf.format(now) + ".xls");
			OutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @author shafiq.irwan
	 * @param store
	 * @date 17-07-2020
	 */
	public void bestSellingItemReport(String subStr1, String subStr2, HttpServletRequest request, Connection connection,
			HttpServletResponse response, String store) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer query = new StringBuffer(
				"select count(cd.menu_item_name) as total_item, cd.menu_item_name as item_name, truncate(cd.menu_item_price, 2) as item_price, tt.created_date as trxdate, cc.category_name as category_name from check_detail cd ");
		query.append("left join `check` ch on (cd.check_id = ch.id) ");
		// Add Category - Start
		query.append("left join category_menu_item cmi on (cd.menu_item_id = cmi.menu_item_id) ");
		query.append("left join category cc on (cmi.category_id = cc.group_category_id) ");
		// Add Category - End
		query.append("left join transaction tt on (cd.check_id = tt.check_id) ");
		query.append("where tt.transaction_status = 3 ");
		query.append("and tt.created_date between '" + subStr1 + "' and '" + subStr2 + "' ");

		if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
			query.append("and tt.store_id = " + store);
		}

		query.append(" group by cd.menu_item_name");

		connection = dbConnectionUtil.retrieveConnection(request);
		try {
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();

			System.out.println(stmt);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();

			// create a small spreadsheet
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
					0, // last row (0-based)
					0, // first column (0-based)
					4 // last column (0-based)
			));

			int iNumbering = 1;
			int iLoopData = 0;

			// Report Title
			row = sheet.createRow(iLoopData++);
			HSSFCell cell = row.createCell(0);
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBold(true);
			style.setFont(font);

			CellStyle style2 = wb.createCellStyle();
			style2.setBorderTop(BorderStyle.DOUBLE);
			style2.setBorderBottom(BorderStyle.DOUBLE);

			cell = row.createCell(0);
			cell.setCellValue("Best Selling Item Report");
			cell.setCellStyle(style);

			// Report Header
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("#");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("Category");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("Item Sold");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("Item Name");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("Price (RM)");
			cell.setCellStyle(style);
//			cell = row.createCell(4);
//			cell.setCellValue("Date");
//			cell.setCellStyle(style);

			while (rs.next()) {

				// Report Content
				row = sheet.createRow(iLoopData++);
				cell = row.createCell(0);
				cell.setCellValue(iNumbering++);
				cell = row.createCell(1);
				cell.setCellValue(rs.getString("category_name"));
				cell = row.createCell(2);
				cell.setCellValue(rs.getInt("total_item"));
				cell = row.createCell(3);
				cell.setCellValue(rs.getString("item_name"));
				cell = row.createCell(4);
				cell.setCellValue(rs.getDouble("item_price"));
//				cell = row.createCell(4);
//				cell.setCellValue(rs.getString("trxdate"));

			}

			// Adjust content to fit column size
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);

			// write it as an excel attachment
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition",
					"attachment; filename=BestSellingItemReport" + dtf.format(now) + ".xls");
			OutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping(value = "/getReportType", produces = "application/json")
	public ResponseEntity<?> getReportType(HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonResult = new JSONArray();
		Connection connection = null;
		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			jsonResult = getReportTypeLookup(connection);
			return ResponseEntity.ok(jsonResult.toString());
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
	}

	public JSONArray getReportTypeLookup(Connection connection) throws Exception {
		JSONArray jsonArr = new JSONArray();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonDefault = new JSONObject();
		jsonDefault.put("id", "0");
		jsonDefault.put("name", "All");
		jsonArr.put(jsonDefault);

		try {
			stmt = connection.prepareStatement("SELECT * FROM reporttype_lookup ");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("name"));
				jsonArr.put(jsonObj);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return jsonArr;
	}

	@GetMapping(value = "/getStoreList", produces = "application/json")
	public ResponseEntity<?> getStoreList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONArray jsonArr = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject jsonDefault = new JSONObject();
		jsonDefault.put("id", "0");
		jsonDefault.put("name", "All");
		jsonArr.put(jsonDefault);

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM store ");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("store_name"));
				jsonArr.put(jsonObj);
			}
			return ResponseEntity.ok(jsonArr.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}

	@GetMapping(value = { "/getBestSellingItem/{date1}/{date2}/{store}/{category}" }, produces = "application/json")
	public ResponseEntity<?> getBestSellingItem(@PathVariable String date1, @PathVariable String date2,
			@PathVariable String store, @PathVariable String category, HttpServletRequest request, HttpServletResponse response) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();

		try {

			String subStr1 = date1.substring(0, 10);
			String subStr2 = date2.substring(0, 10);

			Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(subStr2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datePlusOne);
			cal.add(Calendar.DATE, 1);
			datePlusOne = cal.getTime();

			String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);
			StringBuffer query = new StringBuffer(
					"select count(cd.menu_item_name) as total_item, cd.menu_item_name as item_name, truncate(cd.menu_item_price, 2) as item_price, cc.category_name as category, tt.created_date as trxdate from check_detail cd ");
			query.append("left join category_menu_item cmi on (cd.menu_item_id = cmi.menu_item_id) ");
			query.append("left join category cc on (cmi.category_id = cc.id) ");
			query.append("left join `check` ch on (cd.check_id = ch.id) ");
			query.append("left join transaction tt on (cd.check_id = tt.check_id) ");
			query.append("where tt.transaction_status = 3 ");
			query.append("and tt.created_date between '" + subStr1 + "' and '" + newSubStr2 + "' ");

			if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
				query.append(" and tt.store_id = " + store);
			}
			
			if(!category.equalsIgnoreCase("undefined") && !category.equalsIgnoreCase("0")) {
				query.append(" and cc.id = " + category);
			}

			query.append(" group by cd.menu_item_name");

			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();
			int i = 1;

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("no", i++);
				jsonObj.put("category", rs.getString("category"));
				jsonObj.put("total_item", rs.getString("total_item"));
				jsonObj.put("item_name", rs.getString("item_name"));
				jsonObj.put("item_price", rs.getString("item_price"));
				jsonArray.put(jsonObj);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().body(jsonArray.toString());
	}

	@GetMapping(value = "/getEmployeeName/{storeId}", produces = "application/json")
	public ResponseEntity<?> getEmployeeName(@PathVariable String storeId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONArray jsonArr = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonDefault = new JSONObject();
		jsonDefault.put("id", "0");
		jsonDefault.put("name", "All");
		jsonArr.put(jsonDefault);

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT id, staff_name FROM staff where store_id=" + storeId);
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("staff_name"));
				jsonArr.put(jsonObj);
			}
			return ResponseEntity.ok(jsonArr.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}

	@GetMapping(value = "/getPaymentMethod/", produces = "application/json")
	public ResponseEntity<?> getPaymentMethod(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JSONArray jsonArr = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONObject jsonDefault = new JSONObject();
		jsonDefault.put("id", "0");
		jsonDefault.put("name", "All");
		jsonArr.put(jsonDefault);

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("select * from payment_method_lookup");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("name"));
				jsonArr.put(jsonObj);
			}
			return ResponseEntity.ok(jsonArr.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}

	@GetMapping(value = { "/salesByStore/{date1}/{date2}/{reportType}/{store}" }, produces = "application/json")
	public ResponseEntity<?> salesByStore(@PathVariable String date1, @PathVariable String date2,
			@PathVariable String reportType, @PathVariable String store, HttpServletRequest request,
			HttpServletResponse response) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();

		System.out.println("date1: " + date1);
		System.out.println("date2: " + date2);
		System.out.println("reportType: " + reportType);
		System.out.println("storeName: " + store);
		/*
		 * System.out.println("employeeName: " + employee);
		 * System.out.println("paymentType: " + paymentType);
		 */

		try {

			String subStr1 = date1.substring(0, 10);
			String subStr2 = date2.substring(0, 10);

			Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(subStr2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datePlusOne);
			cal.add(Calendar.DATE, 1);
			datePlusOne = cal.getTime();

			String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);

			StringBuffer query = new StringBuffer("SELECT st.store_name AS store_name, ");
			query.append("st.store_address AS store_address, ");
			query.append("substring(tt.created_date, 1, 10) as datetrx, ");
			query.append("cc.total_item_quantity as quantity, ");
			query.append("SUM(TRUNCATE(tt.transaction_amount, 2)) AS money ");
			query.append("FROM transaction tt LEFT JOIN `check` cc ON (tt.check_id = cc.id) ");
			query.append("LEFT JOIN payment_method_lookup pml ON (tt.payment_method = pml.id) ");
			query.append("LEFT JOIN payment_type_lookup ptl ON (tt.payment_type = ptl.id) ");
			query.append("LEFT JOIN store st ON (tt.store_id = st.id) ");
			query.append("LEFT JOIN staff stf ON (tt.staff_id = stf.id) ");
			query.append("WHERE tt.transaction_status = 3 ");
			query.append("AND tt.created_date BETWEEN '" + subStr1 + "' AND '" + newSubStr2 + "' ");

			if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
				query.append("AND st.id = " + store);
			}

			query.append(
					" GROUP BY DAY(tt.created_date) , MONTH(tt.created_date) , YEAR(tt.created_date) , st.store_name , st.store_address ");
			query.append("ORDER BY tt.created_date DESC");

			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();
			int i = 1;

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("no", i++);
				jsonObj.put("trx_date", rs.getString("datetrx"));
				jsonObj.put("store_name", rs.getString("store_name"));
//				jsonObj.put("store_address", rs.getString("store_address"));
				jsonObj.put("quantity", rs.getString("quantity"));
				jsonObj.put("money", rs.getString("money"));
				jsonArray.put(jsonObj);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().body(jsonArray.toString());
	}

	/**
	 * @author shafiq.irwan
	 * @param store
	 * @throws ParseException
	 * @date 17-07-2020
	 */
	public void salesByStoreReport(String subStr1, String subStr2, HttpServletRequest request, Connection connection,
			HttpServletResponse response, String store) throws ParseException {
		PreparedStatement stmt = null;
		PreparedStatement stmtSummary = null;
		ResultSet rs = null;
		ResultSet rsSummary = null;

		String getDate = subStr2.substring(0, 10);

		Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(getDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(datePlusOne);
		cal.add(Calendar.DATE, 1);
		datePlusOne = cal.getTime();

		String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);

		//Detail Report
		StringBuffer query = new StringBuffer("SELECT st.store_name AS store_name, ");
		query.append("st.store_address AS store_address, ");
		query.append("substring(tt.created_date, 1, 10) as datetrx, ");
		query.append("SUM(cc.total_item_quantity) as quantity, ");
		query.append("SUM(TRUNCATE(tt.transaction_amount, 2)) AS money ");
		query.append("FROM transaction tt LEFT JOIN `check` cc ON (tt.check_id = cc.id) ");
		query.append("LEFT JOIN payment_method_lookup pml ON (tt.payment_method = pml.id) ");
		query.append("LEFT JOIN payment_type_lookup ptl ON (tt.payment_type = ptl.id) ");
		query.append("LEFT JOIN store st ON (tt.store_id = st.id) ");
		query.append("LEFT JOIN staff stf ON (tt.staff_id = stf.id) ");
		query.append("WHERE tt.transaction_status = 3 ");
		query.append("AND tt.created_date BETWEEN '" + subStr1 + "' AND '" + newSubStr2 + "' ");

		if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
			query.append("and st.id = " + store);
		}

		query.append(
				" GROUP BY DAY(tt.created_date) , MONTH(tt.created_date) , YEAR(tt.created_date) , st.store_name , st.store_address ");
		query.append("ORDER BY tt.created_date DESC");
		
		// Summary Report
		StringBuffer querySummary = new StringBuffer("SELECT st.store_name AS store_name, ");
		querySummary.append("st.store_address AS store_address, ");
		querySummary.append("substring(tt.created_date, 1, 10) as datetrx, ");
		querySummary.append("SUM(cc.total_item_quantity) as quantity, ");
		querySummary.append("SUM(TRUNCATE(tt.transaction_amount, 2)) AS money ");
		querySummary.append("FROM transaction tt LEFT JOIN `check` cc ON (tt.check_id = cc.id) ");
		querySummary.append("LEFT JOIN payment_method_lookup pml ON (tt.payment_method = pml.id) ");
		querySummary.append("LEFT JOIN payment_type_lookup ptl ON (tt.payment_type = ptl.id) ");
		querySummary.append("LEFT JOIN store st ON (tt.store_id = st.id) ");
		querySummary.append("LEFT JOIN staff stf ON (tt.staff_id = stf.id) ");
		querySummary.append("WHERE tt.transaction_status = 3 ");
		querySummary.append("AND tt.created_date BETWEEN '" + subStr1 + "' AND '" + newSubStr2 + "' ");

		if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
			querySummary.append("AND st.id = " + store);
		}

		connection = dbConnectionUtil.retrieveConnection(request);
		try {
			stmt = connection.prepareStatement(query.toString());
			stmtSummary = connection.prepareStatement(querySummary.toString());
			rs = stmt.executeQuery();
			rsSummary = stmtSummary.executeQuery();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();

			// create a small spreadsheet
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
					0, // last row (0-based)
					0, // first column (0-based)
					4 // last column (0-based)
			));

			int iNumbering = 1;
			int iLoopData = 0;

			// Report Title
			row = sheet.createRow(iLoopData++);
			HSSFCell cell = row.createCell(0);
			
			CellStyle styleHeader = wb.createCellStyle();
			Font fontHeader = wb.createFont();
			fontHeader.setBold(true);
			styleHeader.setFont(fontHeader);
			
			CellStyle style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setBold(true);
			style.setFont(font);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());

			CellStyle style2 = wb.createCellStyle();
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);

			cell = row.createCell(0);
			cell.setCellValue("Sales by Store Report (From "+subStr1+" to "+subStr2+")");
			cell.setCellStyle(styleHeader);

			// Add Blank Spacing
			row = sheet.createRow(iLoopData++);
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("Summary");
			cell.setCellStyle(styleHeader);
			
			// Report Summary
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("#");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("Date");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("Store Name");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("Quantity Sold");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("Total Sales (RM)");
			cell.setCellStyle(style);
			
			while (rsSummary.next()) {

				// Report Content
				row = sheet.createRow(iLoopData++);
				cell = row.createCell(0);
				cell.setCellValue(iNumbering++);
				cell.setCellStyle(style2);
				cell = row.createCell(1);
				cell.setCellValue(rsSummary.getString("datetrx"));
				cell.setCellStyle(style2);
				cell = row.createCell(2);
				cell.setCellValue(rsSummary.getString("store_name"));
				cell.setCellStyle(style2);
				cell = row.createCell(3);
				cell.setCellValue(rsSummary.getString("quantity"));
				cell.setCellStyle(style2);
				cell = row.createCell(4);
				cell.setCellValue(rsSummary.getString("money"));
				cell.setCellStyle(style2);

			}
			
			// Add Blank Spacing
			row = sheet.createRow(iLoopData++);
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("Details");
			cell.setCellStyle(styleHeader);
			
			// Report Header
			row = sheet.createRow(iLoopData++);
			cell = row.createCell(0);
			cell.setCellValue("#");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("Date");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("Store Name");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("Quantity Sold");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("Total Sales (RM)");
			cell.setCellStyle(style);
			
			iNumbering = 1;
			while (rs.next()) {

				// Report Content
				row = sheet.createRow(iLoopData++);
				cell = row.createCell(0);
				cell.setCellValue(iNumbering++);
				cell.setCellStyle(style2);
				cell = row.createCell(1);
				cell.setCellValue(rs.getString("datetrx"));
				cell.setCellStyle(style2);
				cell = row.createCell(2);
				cell.setCellValue(rs.getString("store_name"));
				cell.setCellStyle(style2);
				cell = row.createCell(3);
//				cell.setCellValue(rs.getString("store_address"));
				cell.setCellValue(rs.getString("quantity"));
				cell.setCellStyle(style2);
				cell = row.createCell(4);
				cell.setCellValue(rs.getString("money"));
				cell.setCellStyle(style2);

			}

			// Adjust content to fit column size
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);

			// write it as an excel attachment
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition",
					"attachment; filename=SalesByStoreReport" + dtf.format(now) + ".xls");
			OutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping(value = { "/salesByStoreSummary/{date1}/{date2}/{store}" }, produces = "application/json")
	public ResponseEntity<?> salesByStoreSummary(@PathVariable String date1, @PathVariable String date2,
			@PathVariable String store, HttpServletRequest request, HttpServletResponse response) {

		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONArray jsonArray = new JSONArray();

		System.out.println("date1: " + date1);
		System.out.println("date2: " + date2);
		System.out.println("storeName: " + store);

		try {

			String subStr1 = date1.substring(0, 10);
			String subStr2 = date2.substring(0, 10);

			Date datePlusOne = new SimpleDateFormat("yyyy-MM-dd").parse(subStr2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(datePlusOne);
			cal.add(Calendar.DATE, 1);
			datePlusOne = cal.getTime();

			String newSubStr2 = new SimpleDateFormat("yyyy-MM-dd").format(datePlusOne);

			StringBuffer query = new StringBuffer("SELECT st.store_name AS store_name, ");
			query.append("st.store_address AS store_address, ");
			query.append("substring(tt.created_date, 1, 10) as datetrx, ");
			query.append("SUM(cc.total_item_quantity) as quantity, ");
			query.append("SUM(TRUNCATE(tt.transaction_amount, 2)) AS money ");
			query.append("FROM transaction tt LEFT JOIN `check` cc ON (tt.check_id = cc.id) ");
			query.append("LEFT JOIN payment_method_lookup pml ON (tt.payment_method = pml.id) ");
			query.append("LEFT JOIN payment_type_lookup ptl ON (tt.payment_type = ptl.id) ");
			query.append("LEFT JOIN store st ON (tt.store_id = st.id) ");
			query.append("LEFT JOIN staff stf ON (tt.staff_id = stf.id) ");
			query.append("WHERE tt.transaction_status = 3 ");
			query.append("AND tt.created_date BETWEEN '" + subStr1 + "' AND '" + newSubStr2 + "' ");

			if (!store.equalsIgnoreCase("undefined") && !store.equalsIgnoreCase("0")) {
				query.append("AND st.id = " + store);
			}

			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement(query.toString());
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				if (!(rs.getString("store_name") == null)) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("no", 1);
					jsonObj.put("date_range", subStr1.replace("-", "/") + " - " + newSubStr2.replace("-", "/"));
					jsonObj.put("store_name", rs.getString("store_name"));
					jsonObj.put("quantity", rs.getString("quantity"));
					jsonObj.put("money", rs.getString("money"));
					jsonArray.put(jsonObj);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().body(jsonArray.toString());
	}
	
	@GetMapping(value = "/getCategoryList", produces = "application/json")
	public ResponseEntity<?> getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONArray jsonArr = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject jsonDefault = new JSONObject();
		jsonDefault.put("id", "0");
		jsonDefault.put("name", "All");
		jsonArr.put(jsonDefault);

		try {
			connection = dbConnectionUtil.retrieveConnection(request);
			stmt = connection.prepareStatement("SELECT * FROM category ");
			rs = (ResultSet) stmt.executeQuery();

			while (rs.next()) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("id", rs.getLong("id"));
				jsonObj.put("name", rs.getString("category_name"));
				jsonArr.put(jsonObj);
			}
			return ResponseEntity.ok(jsonArr.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.badRequest().body(ex.getMessage());
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}
}
