package com.citms.entity;

import java.util.Date;

import lombok.Data;

@Data
public class TableEntity {
	private String tableName;
	private String engine;
	private String tableComment;
	private Date createTime;
}
