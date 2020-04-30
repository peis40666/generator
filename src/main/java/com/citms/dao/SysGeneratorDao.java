package com.citms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.SelectProvider;

import com.baomidou.mybatisplus.plugins.Page;
import com.citms.dao.sqlprovider.SysGeneratorSQLProvider;
import com.citms.entity.ColumnEntity;
import com.citms.entity.TableEntity;

/**
 * 代码生成器
 * 
 * @author denghq
 * 
 * @date 2018/10/29
 */
public interface SysGeneratorDao {
	

	@SelectProvider(type= SysGeneratorSQLProvider.class, method="getQueryListSql")
	List<TableEntity> queryList(Map<String, Object> map);
	
	@SelectProvider(type= SysGeneratorSQLProvider.class, method="getQueryListSql")
	List<TableEntity> queryList(Page<TableEntity> page, Map<String, Object> map);
	
	/*@SelectProvider(type= SysGeneratorSQLProvider.class, method="getQueryTotalSql")
	int queryTotal(Map<String, Object> map);*/
	
	@SelectProvider(type= SysGeneratorSQLProvider.class, method="getQueryTableSql")
	TableEntity queryTable(String tableName);
	
	@SelectProvider(type= SysGeneratorSQLProvider.class, method="getQueryColumnsSql")
	List<ColumnEntity> queryColumns(String tableName);

}
