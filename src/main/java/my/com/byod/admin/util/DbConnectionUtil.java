package my.com.byod.admin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class DbConnectionUtil {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

    public Connection getConnection(Long brandId) throws SQLException, ClassNotFoundException, DataAccessException {
    	Connection con = null;
    	Map<String, Object> brand = jdbcTemplate.queryForMap("SELECT * FROM brands WHERE id = ?", new Object[] {brandId});

    	if(!brand.isEmpty()) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String dbURL = "jdbc:sqlserver://"+ (String)brand.get("brand_db_domain")+":"+ (Integer)brand.get("brand_db_port")+";databaseName="+ 
    					(String)brand.get("brand_db_name");
            con = DriverManager.getConnection(dbURL, (String)brand.get("brand_db_user"), (String)brand.get("brand_db_password"));
    	}
    	return con;
    }
    
    public Connection retrieveConnection(HttpServletRequest request) {
    	Connection con = null;
    	try {
        	if(getBrandId(request) != null) {
        		con = getConnection(getBrandId(request));
        	}
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return con;	
    }

    public void setBrandId(Long brandId, HttpServletRequest request) {
		HttpSession session = request.getSession();
    	session.setAttribute("brand_id", brandId);
    }
    
    public Long getBrandId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Long brandId = (Long)session.getAttribute("brand_id");	
		return brandId;
    }
    
    //For jdbcTemplate usage only
    public DataSource setupDataSource(HttpServletRequest request) {
    	DriverManagerDataSource ds = null;
    	Map<String, Object> brand = jdbcTemplate.queryForMap("SELECT * FROM brands WHERE id = ?", new Object[] {getBrandId(request)});
    	
    	if(!brand.isEmpty()) {
    		String dbURL = "jdbc:sqlserver://"+ (String)brand.get("brand_db_domain")+":"+ (Integer)brand.get("brand_db_port")+";databaseName="+ 
   					(String)brand.get("brand_db_name");
    		
            ds = new DriverManagerDataSource();
            ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            ds.setUrl(dbURL);
            ds.setUsername((String)brand.get("brand_db_user"));
            ds.setPassword((String)brand.get("brand_db_password"));
    	}
    	System.out.println("Hello "+ ds.toString());
    	return ds;
    }
    
}
