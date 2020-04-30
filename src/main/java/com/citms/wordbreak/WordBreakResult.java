package com.citms.wordbreak;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordBreakResult {

	//解析出的单词
	private List<String> words;
	
	//是否全部匹配了字典
	private Boolean allMatch;
}
