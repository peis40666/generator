package com.citms.startup;

import java.util.HashMap;
import java.util.Map;

public class DataBaseContextHolder {
	
	public static String DB_TYPE = "mysql";

	public static Map<String,String> dbType = new HashMap<>();

	static {
		dbType.put("oracle.jdbc.driver.OracleDriver","oracle");
		dbType.put("com.mysql.jdbc.Driver","mysql");
		dbType.put("org.postgresql.Driver","librA");
	}
}
