package com.citms.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.citms.annotation.SetDSPrimary;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.citms.dao.SysGeneratorDao;
import com.citms.entity.ColumnEntity;
import com.citms.entity.TableEntity;
import com.citms.utils.GenUtils;
import com.citms.wordbreak.WordBreak;

/**
 * 代码生成器
 * 
 * @author denghq
 * 
 * @date 2018/10/29
 */

@Service
public class SysGeneratorService {
	@Autowired
	private SysGeneratorDao sysGeneratorDao;
	
	@Autowired
	private WordBreak wordBreak;

	@SetDSPrimary
	public List<TableEntity> queryList(Map<String, Object> map) {
		return sysGeneratorDao.queryList(map);
	}

	/*public int queryTotal(Map<String, Object> map) {
		return sysGeneratorDao.queryTotal(map);
	}*/
	public TableEntity queryTable(String tableName) {
		return sysGeneratorDao.queryTable(tableName);
	}

	public List<ColumnEntity> queryColumns(String tableName) {
		return sysGeneratorDao.queryColumns(tableName);
	}

	public byte[] generatorCode(String[] tableNames) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		for(String tableName : tableNames){
			//查询表信息
			TableEntity table = queryTable(tableName);
			//查询列信息
			List<ColumnEntity> columns = queryColumns(tableName);
			//生成代码
			GenUtils.generatorCode(table, columns, zip,wordBreak);
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}


	public List<TableEntity> queryList(Page<TableEntity> page, Map<String, Object> map) {
		return sysGeneratorDao.queryList(page,map);
	}
}
