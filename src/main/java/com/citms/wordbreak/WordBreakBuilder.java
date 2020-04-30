package com.citms.wordbreak;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WordBreakBuilder {

	public WordBreak build(String fileName) {
		try {
			URL url = WordBreak.class.getClassLoader().getResource(fileName);
			List<String> lines = FileUtils.readLines(new File(url.getFile()), Charset.defaultCharset());
			return new SimpleWordBreak(lines);
		} catch (Exception e) {
			log.error("加载字典出错》》{}", e.getMessage());
			throw new RuntimeException("加载字典出错：" + e.getMessage());
		}

	}

	public static void main(String[] args) {
		WordBreakBuilder wbb = new WordBreakBuilder();
		WordBreak wordBreak = wbb.build("word-break-dict.txt");
		WordBreakResult res = null;
		/*
		res= wordBreak.wordBreak("ATTACHMENTID");
		System.out.println(res);
		res = wordBreak.wordBreak("CREATEDTIME");
		System.out.println(res);
		res = wordBreak.wordBreak("SOURCEID");
		System.out.println(res);
		res = wordBreak.wordBreak("FILESIZE");
		System.out.println(res);
		res = wordBreak.wordBreak("DEVICENAME");
		System.out.println(res);

		res = wordBreak.wordBreak("DEVICEID");
		System.out.println(res);

		res = wordBreak.wordBreak("DEVICENO");
		System.out.println(res);
		res = wordBreak.wordBreak("CORENO");
		System.out.println(res);
		res = wordBreak.wordBreak("DEPARTMENTID");
		System.out.println(res);
		res = wordBreak.wordBreak("SOURCEKIND");
		System.out.println(res);
		res = wordBreak.wordBreak("DEVICEMODEL");
		System.out.println(res);
		res = wordBreak.wordBreak("DEVICETYPE");
		System.out.println(res);
		res = wordBreak.wordBreak("FUNCTIONTYPE");
		System.out.println(res);
		res = wordBreak.wordBreak("SPOTTINGID");
		System.out.println(res);
		res = wordBreak.wordBreak("DIRECTIONID");
		System.out.println(res);
		res = wordBreak.wordBreak("LANDNOLIST");
		System.out.println(res);
		res = wordBreak.wordBreak("LONGITUDE");
		System.out.println(res);
		res = wordBreak.wordBreak("LATITUDE");
		System.out.println(res);
		res = wordBreak.wordBreak("VIDEOINFO");
		System.out.println(res);
		res = wordBreak.wordBreak("EXT");
		System.out.println(res);
		res = wordBreak.wordBreak("EXT1");
		System.out.println(res);*/
		res = wordBreak.wordBreak("YWSTATUS");
		System.out.println(res);
		res = wordBreak.wordBreak("MANUFACTURER");
		System.out.println(res);
		res = wordBreak.wordBreak("PROJECTID");
		System.out.println(res);
		res = wordBreak.wordBreak("APPROVESTATUS");
		System.out.println(res);
		res = wordBreak.wordBreak("APPROVETOR");
		System.out.println(res);
		res = wordBreak.wordBreak("APPROVETIME");
		System.out.println(res);
		res = wordBreak.wordBreak("APPROVEINFO");
		System.out.println(res);
		res = wordBreak.wordBreak("HEADSTOCK");
		System.out.println(res);
		res = wordBreak.wordBreak("BOPOMOFO");
		System.out.println(res);
		res = wordBreak.wordBreak("CREATEDTIME");
		System.out.println(res);
		res = wordBreak.wordBreak("MODIFIEDTIME");
		System.out.println(res);
		res = wordBreak.wordBreak("MODIFIEDTIMEINFO");
		System.out.println(res);


	}

}
