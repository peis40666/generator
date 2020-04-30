package com.citms.wordbreak;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;

public class DictProcessor {

	public static void main(String[] args) throws Exception {
		
		URL url = DictProcessor.class.getClassLoader().getResource("source_dict.txt");
		List<String> lines = FileUtils.readLines(new File(url.getFile()),Charset.defaultCharset());
		
		
		
		Set<String> dict = Sets.newHashSet();
		
		String regex  = " \\w+ ";
		Pattern  pattern=Pattern.compile(regex);
		for(String str:lines) {
			Matcher  ma=pattern.matcher(str);
			while(ma.find()){  
				dict.add(ma.group().trim());  
	        }  
		}
		
		url = DictProcessor.class.getClassLoader().getResource("word-break-dict.txt");
		FileUtils.writeLines(new File(url.getFile()), dict);
        
	}
}
