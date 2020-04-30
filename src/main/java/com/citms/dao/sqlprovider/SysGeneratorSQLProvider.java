package com.citms.dao.sqlprovider;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.citms.startup.DataBaseContextHolder;

public class SysGeneratorSQLProvider {
	
	
	private String buildQueryListSqlForMysql(Map<String, Object> map) {
		
		StringBuilder sql = new StringBuilder()
				.append(" SELECT table_name tableName,engine,table_comment tableComment,create_time createTime ")
				.append(" FROM information_schema.tables ")
				.append(" WHERE ( table_schema = ( select database() )) ");
				
		if(StringUtils.isNotBlank((String) map.get("tableName"))) {
			sql.append(" AND (table_name like CONCAT('%',#{tableName},'%') or table_comment like CONCAT('%',#{tableName},'%') )");
		}
		
		sql.append(" ORDER BY create_time desc ");
		return sql.toString();
	}
	
	private String buildQueryListSqlForOracle(Map<String, Object> map) {
		StringBuilder sql = new StringBuilder()
				.append(" select ut.table_name as tableName,utcm.comments tableComment,ut.last_analyzed as createTime ")
				.append(" from user_tables ut left join user_tab_comments utcm on ut.table_name = utcm.table_name ");
		
		if(StringUtils.isNotBlank((String) map.get("tableName"))) {
			sql.append(" WHERE (ut.table_name like '%'||#{tableName}||'%' or utcm.comments like '%'||#{tableName}||'%' )");
		}
		
		sql.append(" ORDER BY ut.last_analyzed desc ");
		return sql.toString();
	}

	
	private static final StringBuilder QUERY_TABLE_SQL_MYSQL = new StringBuilder()
			.append(" select table_name tableName, engine, table_comment tableComment, create_time createTime ")
			.append(" from information_schema.tables ")
			.append(" where table_schema = (select database()) and table_name = #{tableName} ");


	private static final StringBuilder QUERY_TABLE_SQL_ORACLE = new StringBuilder()
			.append(" select ut.table_name as tableName,utcm.comments tableComment,ut.last_analyzed as createTime ")
			.append(" from user_tables ut left join user_tab_comments utcm on ut.table_name = utcm.table_name ")
			.append(" where ut.table_name = #{tableName} ");


	private static final StringBuilder QUERY_COLUMNS_SQL_MYSQL = new StringBuilder()
			.append(" select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra ")
			.append(" from information_schema.columns ")
			.append(" where table_name = #{tableName} and table_schema = (select database()) ")
			.append(" order by ordinal_position ");
	

	private static final StringBuilder QUERY_COLUMNS_SQL_ORACLE = new StringBuilder()
			//  where Table_Name='ASSETS'
			.append(" select col.column_name , col.data_type dataType, ucm.comments columnComment,case t.constraint_type when 'P' then 'PRI' else '' end \"columnKey\" ")
			.append(" from user_tab_columns col  ")
			.append(" left join ")
			.append(" (SELECT uc.constraint_type,uc.table_name,ucc.column_name ")
			.append(" FROM user_cons_columns ucc ")
			.append(" left join user_constraints uc on uc.table_name = ucc.table_name and uc.constraint_name = ucc.constraint_name ")
			.append(" where uc.constraint_type='P') t ")
			.append(" on t.table_name=col.table_name and t.column_name=col.column_name ")
			.append(" left join user_col_comments ucm on ucm.table_name=col.table_name and col.column_name = ucm.column_name ") 
			.append(" where col.table_name= #{tableName} ")
			.append(" order by col.column_id ");
	
	// 拿到查询总行数的SQL语句
	public String getQueryListSql(Map<String, Object> map) {
		// 创建SQL对象
		/*SQL sql = new SQL();
		addQueryListSelect(sql);
		addQueryListFrom(sql); // 添加from语句
		addQueryListWhere(sql, map); // 添加where语句
		addQueryListOrderBy(sql);
		return sql.toString();*/
		if("mysql".equals(DataBaseContextHolder.DB_TYPE)) {
			return buildQueryListSqlForMysql(map);
		}else if("oracle".equals(DataBaseContextHolder.DB_TYPE)) {
			return buildQueryListSqlForOracle(map);
		}else {
			throw new RuntimeException("不支持的数据库类型>>>>"+DataBaseContextHolder.DB_TYPE);
		}
		
	}
	

/*	// 抽取拼接的 orderBy语句的方法
	private void addQueryListOrderBy(SQL sql) {
		sql.ORDER_BY("create_time desc");
	}


	// 抽取拼接的select语句的方法
	private void addQueryListSelect(SQL sql) {
		sql.SELECT("table_name tableName, engine, table_comment tableComment, create_time createTime");
	}
	
	// 抽取拼接的from语句的方法
	private void addQueryListFrom(SQL sql) {
		sql.FROM("information_schema.tables"); // 往SQL对象中添加from语句
	}
	
	// 抽取拼接的where语句的方法
	private void addQueryListWhere(SQL sql, Map<String, Object> qo) {
		sql.WHERE("table_schema = (select database())");
		if(StringUtils.isNotBlank((String) (qo.get("tableName")))) {
			sql.WHERE("table_name like concat('%', #{tableName}, '%')");
		}		
	}
	*/
	
	/*public String getQueryTotalSql(Map<String, Object> map) {
		// 创建SQL对象
		SQL sql = new SQL();
		addQueryTotalSelect(sql);
		addQueryTotalFrom(sql); // 添加from语句
		addQueryTotalWhere(sql, map); // 添加where语句
		return sql.toString();
	}
	

	private void addQueryTotalSelect(SQL sql) {
		sql.SELECT("count(*)");
		
	}

	private void addQueryTotalFrom(SQL sql) {
		sql.FROM("information_schema.tables");
	}

	private void addQueryTotalWhere(SQL sql, Map<String, Object> qo) {
		sql.WHERE("table_schema = (select database())");
		if(StringUtils.isNotBlank((String) (qo.get("tableName")))) {
			sql.WHERE("table_name like concat('%', #{tableName}, '%')");
		}		
	}
	
	 */

	


	public String getQueryTableSql(String tableName){
		/*
		// 创建SQL对象
		SQL sql = new SQL();
		addQueryTableSelect(sql);
		addQueryTableFrom(sql); // 添加from语句
		addQueryTableWhere(sql, tableName); // 添加where语句
		return sql.toString();*/
		if("mysql".equals(DataBaseContextHolder.DB_TYPE)) {
			return QUERY_TABLE_SQL_MYSQL.toString();
		}else if("oracle".equals(DataBaseContextHolder.DB_TYPE)) {
			return QUERY_TABLE_SQL_ORACLE.toString();
		}else {
			throw new RuntimeException("不支持的数据库类型>>>>"+DataBaseContextHolder.DB_TYPE);
		}
	}
/*	
	private void addQueryTableWhere(SQL sql, String tableName) {
		sql.WHERE("table_schema = (select database())");
		if(StringUtils.isNotBlank(tableName) ){
			sql.WHERE("table_name = #{tableName}");
		}		
	}


	private void addQueryTableFrom(SQL sql) {
		sql.FROM("information_schema.tables");
	}


	private void addQueryTableSelect(SQL sql) {
		sql.SELECT("table_name tableName, engine, table_comment tableComment, create_time createTime");
	}
*/

	public String getQueryColumnsSql(String tableName){
		// 创建SQL对象
		/*SQL sql = new SQL();
		addQueryColumnsSelect(sql);
		addQueryColumnsFrom(sql); // 添加from语句
		addQueryColumnsWhere(sql, tableName); // 添加where语句
		addQueryColumnsOrderBy(sql);
		return sql.toString();*/
		if("mysql".equals(DataBaseContextHolder.DB_TYPE)) {
			return QUERY_COLUMNS_SQL_MYSQL.toString();
		}else if("oracle".equals(DataBaseContextHolder.DB_TYPE)) {
			return QUERY_COLUMNS_SQL_ORACLE.toString();
		}else {
			throw new RuntimeException("不支持的数据库类型>>>>"+DataBaseContextHolder.DB_TYPE);
		}
	}

/*
	private void addQueryColumnsWhere(SQL sql, String tableName) {
		sql.WHERE("table_schema = (select database())");
		if(StringUtils.isNotBlank(tableName) ){
			sql.WHERE("table_name = #{tableName}");
		}	
	}


	private void addQueryColumnsOrderBy(SQL sql) {
		sql.ORDER_BY("ordinal_position");
	}


	private void addQueryColumnsFrom(SQL sql) {
		sql.FROM("information_schema.columns");
		
	}


	private void addQueryColumnsSelect(SQL sql) {
		sql.SELECT("column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra");
		
	}
*/
	
}
