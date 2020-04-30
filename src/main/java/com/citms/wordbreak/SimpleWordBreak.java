package com.citms.wordbreak;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class SimpleWordBreak implements WordBreak {

	
	//字典
	private List<String> dict;
	
	public SimpleWordBreak(List<String> dict) {
		if(!CollectionUtils.isEmpty(dict)) {
			//对字典由单词长度由长到短排序
			 this.dict = sortDict(dict);
			//Collections.sort(dict,new SortByLengthComparator());
		}else {
			throw new RuntimeException("字典内容为空");
		}
	}

	@Override
	public WordBreakResult wordBreak(String string) {
		if(StringUtils.isBlank(string)) {
			return new WordBreakResult(Lists.newArrayList(), false);
		}
		List<String> words =  Lists.newArrayList();
		boolean allMatch = true;
		//int lastMathIndex = -1;
		int lastMathIndex = 0;
		for(int i = 0,len = string.length();i<len;) {
			boolean match = false;
			for(String d:dict) {
				if(d.length()<=1) {
					//过滤掉单个字符
					continue;
				}
				if(i + d.length() <= len && string.substring(i, i+d.length()).equalsIgnoreCase(d)) {
					//if(lastMathIndex<i&&i>0) {
					if(lastMathIndex<i) {
						//words.add(string.substring(lastMathIndex==-1?0:lastMathIndex, i));
						words.add(string.substring(lastMathIndex, i));
					}
					words.add(string.substring(i, i+d.length()));
					i = i + d.length();
					lastMathIndex = i;
					match = true;
					break;
				}
			}
			if(!match) {
				i ++;
				allMatch = false;
				if(i>=len) {
					//words.add(string.substring(lastMathIndex==-1?0:lastMathIndex));
					words.add(string.substring(lastMathIndex));
					break;
				}
			}
		}
		
		return new WordBreakResult(words,allMatch);
		
	}
	
	//去空并排序
	public static List<String> sortDict(List<String> dictList) {
	
		 //插入排序
		if(!CollectionUtils.isEmpty(dictList)) {
			
			List<String> res = Lists.newArrayList();
			String tempStr = null;
			for(int i = 0,len = dictList.size();i<len;i++) {
				tempStr = dictList.get(i);
				if(StringUtils.isNotBlank(tempStr)) {
					insertToListWithOrder(res,tempStr);
				}
			} 
			
			return res;
		} else {
			return Lists.newArrayList();
		}
		
    }

	private static void insertToListWithOrder(List<String> list, String str) {
		
		//找到插入的位置
		int index = -1;
		
		for(int i = 0,len = list.size();i<len;i++) {
			if(str.length()>list.get(i).length()) {
				index = i;
				break;
			}
		}
		
		list.add(str);
		
		if(index != -1) {//需移动位置
			for(int len = list.size(),i=len-2;i>=index;i--) {
				list.set(i+1,list.get(i));
			}
			list.set(index, str);
		}
		
	}
	
}
