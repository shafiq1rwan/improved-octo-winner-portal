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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.entity.Location;
import my.com.byod.admin.entity.Store;
import my.com.byod.admin.util.ByodUtil;
import my.com.byod.admin.util.DbConnectionUtil;

@RestController
@RequestMapping("/menu/transaction")
public class TransactionRestController {
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;
	
	@Autowired
	DataSource dataSource;
	
	@GetMapping("")
	public ResponseEntity<?> findStoreById(@RequestParam("id") Long storeId, HttpServletRequest request, HttpServletResponse response) {
		JSONArray jsonArray = new JSONArray();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			System.out.println("storeId:" + storeId);
			connection = dbConnectionUtil.retrieveConnection(request);
			//String brandId = byodUtil.getGeneralConfig(connection, "BRAND_ID");
			String sqlStatement = "select t.id,s.staff_name,t.check_number,tt.name as transaction_type,pm.name as payment_method, " + 
					"pt.name as payment_type,case when pm.id = 1 then '-' else case when t.terminal_serial_number is null then '' else t.terminal_serial_number end end as terminal, " + 
					"t.transaction_amount,tss.name as transaction_status, " + 
					"case when pm.id = 1 then t.created_date else case when t.transaction_date is not null and t.transaction_time is not null then " + 
					"concat('20',SUBSTRING(t.transaction_date, 1, 2),'-',SUBSTRING(t.transaction_date, 3, 2),'-',SUBSTRING(t.transaction_date, 5, 2),' ',SUBSTRING(t.transaction_time, 1, 2),':',SUBSTRING(t.transaction_time, 3, 2),':',SUBSTRING(t.transaction_time, 5, 2)) else '' end end as transaction_date " + 
					"from [transaction] t " + 
					"inner join staff s on s.id = t.staff_id " + 
					"inner join [check] c on c.check_id = t.check_id and c.check_number = t.check_number " + 
					"inner join transaction_type_lookup tt on tt.id = t.transaction_type " + 
					"inner join payment_method_lookup pm on pm.id = t.payment_method " + 
					"inner join payment_type_lookup pt on pt.id = t.payment_type " + 
					"inner join transaction_settlement_status_lookup tss on tss.id = t.transaction_status " +
					"WHERE t.store_id = ? " +
					"order by t.transaction_date desc;";
			stmt = connection.prepareStatement(sqlStatement);
			stmt.setLong(1, storeId);
			rs = stmt.executeQuery();
			while(rs.next()) {
				JSONObject transaction = new JSONObject();
				transaction.put("id", rs.getString("id"));
				transaction.put("staffName", rs.getString("staff_name"));
				transaction.put("checkNumber", rs.getString("check_number"));
				transaction.put("transactionType", rs.getString("transaction_type"));
				transaction.put("paymentMethod", rs.getString("payment_method") + " (" + rs.getString("terminal") + ")");
				transaction.put("paymentType", rs.getString("payment_type"));
				transaction.put("terminal", rs.getString("terminal"));
				transaction.put("transactionAmount", String.format("%.2f", rs.getBigDecimal("transaction_amount")));
				transaction.put("transactionStatus", rs.getString("transaction_status"));
				transaction.put("transactionDate", rs.getString("transaction_date"));
				jsonArray.put(transaction);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).body("Server error. Please contact support."); 
		} finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new ResponseEntity<String>(jsonArray.toString(), HttpStatus.OK);
	}
}
