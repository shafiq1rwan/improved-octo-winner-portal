package my.com.byod.admin.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import my.com.byod.admin.util.DbConnectionUtil;
import my.com.byod.logger.Logger;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@RestController
@RequestMapping("/report")
public class ReportRestController {
	
	@Autowired
	private DbConnectionUtil dbConnectionUtil;	

	@RequestMapping(value = { "/transaction_report" }, method = { RequestMethod.POST }, produces = "application/json")
	public String generateTransactionReport(@RequestBody String data, HttpServletRequest request) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject jsonResult = new JSONObject();

		try {
			JSONObject jsonData = new JSONObject(data);
			System.out.println(jsonData.toString());

			connection = dbConnectionUtil.retrieveConnection(request);	
			stmt = connection.prepareStatement("SELECT * FROM store");
			rs = stmt.executeQuery();

			if (rs.next()) {
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");

				SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String dateString = formatter2.format(new Date());

				Date startDate = formatter1.parse(jsonData.getString("startDate"));
				Date endDate = formatter1.parse(jsonData.getString("endDate"));

				String startDateText = "";
				String endDateText = formatter1.format(endDate);

				if (startDate.compareTo(endDate) > 0) {
					System.out.println("startDate is after endDate");
					startDateText = formatter1.format(endDate);
				} else if (startDate.compareTo(endDate) < 0) {
					System.out.println("startDate is before endDate");
					startDateText = formatter1.format(startDate);
				} else if (startDate.compareTo(endDate) == 0) {
					System.out.println("startDate is equal to endDate");
					startDateText = formatter1.format(startDate);
				}

				System.out.println("startDateText : " + startDateText);
				System.out.println("endDateText : " + endDateText);

				// report data
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("storeName", rs.getString("store_name"));
				parameters.put("storeAddress", rs.getString("store_address"));
				parameters.put("startDate", startDateText);
				parameters.put("endDate", endDateText);

				System.out.println("Parameters Length: " + parameters.size());

				JasperReport jasperReport = null;

				try {
					File jasperFile = ResourceUtils.getFile("classpath:reports/jasper/TransactionReport.jasper");
					if (jasperFile.exists()) {
						jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
					}
				} catch (FileNotFoundException ex) {
					File file = new ClassPathResource("reports/jrxml/TransactionReport.jrxml").getFile();
					InputStream transactionReportStream = new FileInputStream(file);
					jasperReport = JasperCompileManager.compileReport(transactionReportStream);
					JRSaver.saveObject(jasperReport, "TransactionReport.jasper");
				}

				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
						connection);

				// Create folder to store reports
				File outDir = new File("C:/jasperoutput");
				outDir.mkdirs();

				// Expost file as pdf
				JRPdfExporter exporter = new JRPdfExporter();

				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
						"C:/jasperoutput/" + "TransactionReport" + dateString + ".pdf"));

				SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
				reportConfig.setSizePageToContent(true);
				reportConfig.setForceLineBreakPolicy(false);

				SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
				exportConfig.setEncrypted(true);
				exportConfig.setAllowedPermissionsHint("PRINTING");

				exporter.setConfiguration(reportConfig);
				exporter.setConfiguration(exportConfig);

				exporter.exportReport();

				jsonResult.put("responseCode", "00");
				jsonResult.put("responseMessage", "Generate Report Success");

			} else {
				jsonResult.put("responseCode", "01");
				jsonResult.put("responseMessage", "Generate Report Failed");
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

		System.out.println(jsonResult.toString());
		return jsonResult.toString();
	}

}
