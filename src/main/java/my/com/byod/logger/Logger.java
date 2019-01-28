package my.com.byod.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Logger {

	@Value("${log-path}")
	private String LOG_PATH;

	public void writeActivity(String log, String folderName) {
		String cDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {
			String fullLogPath = LOG_PATH + "/" + folderName;
			File logPath = new File(fullLogPath);
			logPath.mkdirs();

			fw = new FileWriter(fullLogPath + "/" + cDate + "_act.txt", true);

			bw = new BufferedWriter(fw);
			bw.write(((Calendar.getInstance()).getTime()).toString());
			bw.write(" : ");
			bw.write(log);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public void writeError(Exception ex, String folderName) {
		String cDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintStream ps = null;

		try {
			String fullLogPath = LOG_PATH + "/" + folderName;
			File logPath = new File(fullLogPath);
			logPath.mkdirs();
			
			fw = new FileWriter(fullLogPath + "/" + cDate + "_err.txt", true);

			bw = new BufferedWriter(fw);
			bw.write(((Calendar.getInstance()).getTime()).toString());
			bw.write(" : ");
			bw.flush();
			bw.close();
			fw.close();

			File logFile = new File(fullLogPath + "/" + cDate + "_err.txt");
			ps = new PrintStream(new FileOutputStream(logFile, true));
			
			ex.printStackTrace(ps);
			ps.println();
			ps.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
