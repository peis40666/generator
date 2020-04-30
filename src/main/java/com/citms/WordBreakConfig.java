package com.citms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.citms.wordbreak.WordBreak;
import com.citms.wordbreak.WordBreakBuilder;

@Configuration
public class WordBreakConfig {

	@Bean
	public WordBreak wordBreak(){
		WordBreakBuilder wbb = new WordBreakBuilder();
		return wbb.build("word-break-dict.txt");
	}
}
