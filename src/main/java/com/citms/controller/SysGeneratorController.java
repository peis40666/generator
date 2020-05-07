package com.citms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.citms.bean.DataSourcePojo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.citms.entity.TableEntity;
import com.citms.service.SysGeneratorService;
import com.citms.startup.ServerModuleHolder;
import com.citms.utils.PageUtils;
import com.citms.utils.Query;
import com.citms.utils.R;

/**
 * 代码生成器
 * 
 * @author denghq
 * 
 * @date 2018/10/29
 */
@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {
	@Autowired
	private SysGeneratorService sysGeneratorService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params);
		Page<TableEntity> page = query.getPagination();
		
		List<TableEntity> list = sysGeneratorService.queryList(page,params);
		//int total = sysGeneratorService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(list, (int) page.getTotal(), page.getSize(), page.getCurrent());
		return R.ok().put("page", pageUtil);
	}

	@Autowired
	private DynamicDataSourceProperties dynamicDataSourceProperties;
	@ResponseBody
	@RequestMapping("/ds")
	public R ds(){
		List<DataSourcePojo> list = new ArrayList<>();
		Map<String, DataSourceProperty> map = dynamicDataSourceProperties.getDatasource();
		for (Map.Entry<String, DataSourceProperty> entry : map.entrySet()) {
			DataSourcePojo ds = new DataSourcePojo();
			DataSourceProperty dataSourceProperty = entry.getValue();
			ds.setName(entry.getKey());
			ds.setUsername(dataSourceProperty.getUsername());
			ds.setPassword(dataSourceProperty.getPassword());
			ds.setDriverClassName(dataSourceProperty.getDriverClassName());
			ds.setUrl(dataSourceProperty.getUrl());
			list.add(ds);
		}
	//	PageUtils pageUtil = new PageUtils(list, (int) page.getTotal(), page.getSize(), page.getCurrent());
		return R.ok().put("dsList",list);
	}

	/**
	 * 生成代码
	 */
	@RequestMapping("/code")
	public void code(String tables, String moduleName,HttpServletResponse response) throws IOException{
		if(StringUtils.isNotBlank(moduleName)) {
			ServerModuleHolder.moduleName = moduleName;
		}else {
			ServerModuleHolder.moduleName = "";
		}
		byte[] data = sysGeneratorService.generatorCode(tables.split(","));

		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"platform.zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");

		IOUtils.write(data, response.getOutputStream());
	}



}
