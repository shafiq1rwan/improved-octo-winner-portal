package managepay.com.admin.byod.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import managepay.com.admin.byod.entity.Log;

@Repository
public class LogRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public LogRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void writeLog(Log log) {
		jdbcTemplate.update("INSERT INTO log(user_id, username, action, table_name) VALUES(?,?,?,?)", new Object[] {
				log.getUserId(), log.getUsername(), log.getAction(), log.getTableName() 
		});
	}
	
}
