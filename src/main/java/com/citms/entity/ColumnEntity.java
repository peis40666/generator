package com.citms.entity;

import lombok.Data;

@Data
public class ColumnEntity {

	private String columnName;
	private String dataType;
	private String columnComment;
	private String columnKey;
	private String extra;
	
}
