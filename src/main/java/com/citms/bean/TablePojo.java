package com.citms.bean;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * 表数据
 * 
 * @author denghq
 * 
 * @date 2018/10/29
 */
public class TablePojo {
	
	//表的名称
	private String tableName;
	//表的备注
	private String comments;
	//表的主键
	private ColumnPojo pk;
	//表的列名(不包含主键)
	private List<ColumnPojo> columns;
	
	//类名(第一个字母大写)，如：sys_user => SysUser
	private String className;
	//类名(第一个字母小写)，如：sys_user => sysUser
	private String classname;
	
	private boolean hasBigDecimal;
	
	private List<Map<String,Object>> mockData = Lists.newArrayList();
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public ColumnPojo getPk() {
		return pk;
	}
	public void setPk(ColumnPojo pk) {
		this.pk = pk;
	}
	public List<ColumnPojo> getColumns() {
		return columns;
	}
	public void setColumns(List<ColumnPojo> columns) {
		this.columns = columns;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public boolean isHasBigDecimal() {
		return hasBigDecimal;
	}
	public void setHasBigDecimal(boolean hasBigDecimal) {
		this.hasBigDecimal = hasBigDecimal;
	}
	public List<Map<String, Object>> getMockData() {
		return mockData;
	}
	public void setMockData(List<Map<String, Object>> mockData) {
		this.mockData = mockData;
	}
	
}
