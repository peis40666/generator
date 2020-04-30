package com.citms.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class Starter implements CommandLineRunner {
	
	@Value("${spring.datasource.dynamic.primary}")
	private String dbType;
	
	@Autowired
	MappingJackson2HttpMessageConverter jsonConverter;
	
	@Override
	public void run(String... args) throws Exception {
		DataBaseContextHolder.DB_TYPE = dbType;
		JsonConverterHolder.jsonConverter = jsonConverter;
	}
	
}
