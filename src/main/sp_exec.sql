DELIMITER \\

DROP PROCEDURE IF EXISTS `tenant`.`sp_exec`\\
CREATE PROCEDURE `tenant`.`sp_exec` (IN in_sql_query LONGTEXT)
BEGIN
	DECLARE v_sql LONGTEXT;
		set @v_sql:=in_sql_query;
		PREPARE dynamic_statement FROM @v_sql;
		EXECUTE dynamic_statement;
		DEALLOCATE PREPARE dynamic_statement;
END\\

DELIMITER ;