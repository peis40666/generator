package com.citms.utils;

import com.citms.bean.ColumnPojo;
import com.citms.bean.TablePojo;
import com.citms.entity.ColumnEntity;
import com.citms.entity.TableEntity;
import com.citms.startup.JsonConverterHolder;
import com.citms.startup.ServerModuleHolder;
import com.citms.wordbreak.WordBreak;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器 工具类
 *
 * @author denghq
 * @date 2018/10/29
 */
@Slf4j
public class GenUtils {

    private static final Integer DEFAULT_MOCK_DATA_COUNT = 20;

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<String>();
        templates.add("template/Entity.java.vm");
        templates.add("template/Dao.java.vm");
        templates.add("template/Dao.xml.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");
        templates.add("template/menu.sql.vm");

        //查询与响应vo
        templates.add("template/SearchReq.java.vm");
        templates.add("template/SearchResp.java.vm");
        templates.add("template/InfoResp.java.vm");
        templates.add("template/UpdateOrSaveReq.java.vm");


        // swagger
        //templates.add("template/index.yaml.vm");

        templates.add("template/index.vue.vm");
        templates.add("template/add-or-update.vue.vm");
        //主从表
        templates.add("template/add-or-update-withsub.vue.vm");

        // mock
        templates.add("template/Mock.js.vm");
        templates.add("template/mockConfig.js.vm");

        //api
        templates.add("template/api.js.vm");
        //router
        templates.add("template/router.js.vm");
        return templates;
    }

    /**
     * 生成TableEntity对象
     */

    public static TablePojo getTableEntity(TableEntity table, List<ColumnEntity> columns, Configuration config,
                                           WordBreak wordBreak) {

        boolean hasBigDecimal = false;
        // 表信息
        TablePojo tablePojo = new TablePojo();
        tablePojo.setTableName(table.getTableName());
        //去掉表注释中的“表”字
        if (StringUtils.isBlank(table.getTableComment())) {
            tablePojo.setComments(table.getTableName());
        } else {
            tablePojo.setComments(table.getTableComment().replaceAll("表", "信息"));
        }
        //tablePojo.setComments(table.getTableComment());
        // 表名转换成Java类名
        String className = tableToJava(tablePojo.getTableName(), config.getBoolean("useWordBroker"), config.getString("tablePrefix"), wordBreak);
        tablePojo.setClassName(className);
        tablePojo.setClassname(StringUtils.uncapitalize(className));

        // 列信息
        List<ColumnPojo> columsList = new ArrayList<>();
        for (ColumnEntity column : columns) {
            ColumnPojo columnPojo = new ColumnPojo();
            columnPojo.setColumnName(column.getColumnName());
            columnPojo.setDataType(column.getDataType());
            columnPojo.setComments(column.getColumnComment());
            columnPojo.setExtra(column.getExtra());

            // 列名转换成Java属性名
            String attrName = columnToJava(columnPojo.getColumnName(), config.getBoolean("useWordBroker"), wordBreak);
            columnPojo.setAttrName(attrName);
            columnPojo.setAttrname(StringUtils.uncapitalize(attrName));

            // 列的数据类型，转换成Java类型
            String attrType = config.getString(columnPojo.getDataType(), "unknowType");
            columnPojo.setAttrType(attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal")) {
                tablePojo.setHasBigDecimal(true);
                hasBigDecimal = true;
            }
            // 是否主键
            if ("PRI".equalsIgnoreCase(column.getColumnKey()) && tablePojo.getPk() == null) {
                tablePojo.setPk(columnPojo);
            }

            columsList.add(columnPojo);
        }

        tablePojo.setColumns(columsList);

        genMockData(tablePojo, config);

        // 没主键，则第一个字段为主键
        if (tablePojo.getPk() == null) {
            tablePojo.setPk(tablePojo.getColumns().get(0));
        }

        return tablePojo;
    }

    private static void genMockData(TablePojo tablePojo, Configuration config) {
        List<Map<String, Object>> mockData = tablePojo.getMockData();
        if (mockData == null) {
            mockData = Lists.newArrayList();
        }
        // 列信息
        List<ColumnPojo> columsList = tablePojo.getColumns();
        ColumnPojo pk = tablePojo.getPk();
        Random r = new Random();
        for (int i = 0, len = config.getInteger("mockDataCount", DEFAULT_MOCK_DATA_COUNT); i < len; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            for (ColumnPojo column : columsList) {

                String attrType = column.getAttrType();
                String attrName = column.getAttrname();
                String comments = column.getComments();
                if (StringUtils.isEmpty(comments)) {
                    comments = attrName;
                }
                if (pk != null && StringUtils.isNotBlank(pk.getAttrname()) && pk.getAttrname().equalsIgnoreCase(attrName)) {
                    data.put(attrName, "uuid" + System.currentTimeMillis() + i);
                } else if (attrType.equals("BigDecimal")) {
                    data.put(attrName, r.nextInt(2));
                } else if (attrType.equals("Integer")) {
                    data.put(attrName, r.nextInt());
                } else if (attrType.equals("Float") || attrType.equals("Double")) {
                    data.put(attrName, r.nextFloat());
                } else if (attrType.equals("Date")) {
                    data.put(attrName, new Date());
                } else {
                    data.put(attrName, (comments == null ? "" : comments) + ("mock-" + (i + 1)));
                }
            }
            mockData.add(data);
        }

    }

    public static TablePojo getTableEntity(TableEntity table, List<ColumnEntity> columns, WordBreak wordBreak) {

        Configuration config = getConfig();
        return getTableEntity(table, columns, config, wordBreak);
    }

    public static Map<String, Object> tableEntityToContext(TablePojo tablePojo, Configuration config) {

        String mainPath = config.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "com.citms" : mainPath;
        // 封装模板数据
        String cName = tablePojo.getClassName();
        String cname = toLowerCaseFirstOne(cName);
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tablePojo.getTableName());
        map.put("comments", tablePojo.getComments());
        map.put("pk", tablePojo.getPk());
        map.put("className", cName);
        map.put("classname", cname);
        String[] split = tablePojo.getTableName().toLowerCase().split("_");
        //String moduleName = config.getString("moduleName");
        String moduleName = ServerModuleHolder.moduleName;
        if (StringUtils.isBlank(moduleName)) {
            moduleName = split[0];
        }
        /*
         * StringBuffer pathName = new StringBuffer(); for(int i=1;i<split.length;i++){
         * pathName.append(split[i]); }
         */
        String pathName = cname;
        map.put("pathName", pathName);
        map.put("moduleName", moduleName);
        map.put("columns", tablePojo.getColumns());
        map.put("hasBigDecimal", tablePojo.isHasBigDecimal());
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package"));
        map.put("author", config.getString("author"));
        map.put("email", config.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));

        //mock
        try {
            map.put("mockData", JsonConverterHolder.jsonConverter.getObjectMapper().writeValueAsString(tablePojo.getMockData()));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            log.error("转换mock数据出错" + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(TableEntity table, List<ColumnEntity> columns, ZipOutputStream zip,
                                     WordBreak wordBreak) {
        // 配置信息
        Configuration config = getConfig();

        // 表信息
        TablePojo tablePojo = getTableEntity(table, columns, config, wordBreak);

        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);

        // 模板上下文
        VelocityContext context = new VelocityContext(tableEntityToContext(tablePojo, config));

        // 获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tablePojo.getClassName(),
                        //config.getString("package"), config.getString("moduleName"))));
                        config.getString("package"), ServerModuleHolder.moduleName)));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RRException("渲染模板失败，表名：" + tablePojo.getTableName(), e);
            }
        }

    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName, Boolean useWordBroker, WordBreak wordBreak) {
        // 如果单词里面没有“_”则调用分词器分词
        if (!columnName.contains("_") && useWordBroker != null && useWordBroker) {
            columnName = StringUtils.join(wordBreak.wordBreak(columnName).getWords(), "_");
        }
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, Boolean useWordBroker, String tablePrefix, WordBreak wordBreak) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replace(tablePrefix, "");
        }
        return columnToJava(tableName, useWordBroker, wordBreak);
    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "After-end" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }
		/*if (StringUtils.isNotBlank(moduleName)) {
			packagePath += moduleName + File.separator;
		}*/
        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("Dao.xml.vm")) {
            return "After-end" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper" + File.separator
                    + /* moduleName + File.separator + */ className + "Dao.xml";
        }

        if (template.contains("menu.sql.vm")) {
            return className.toLowerCase() + "_menu.sql";
        }

        //vo
        if (template.contains("SearchReq.java.vm")) {
            return packagePath + "pojo" + File.separator + "api" + File.separator + className + "SearchReq.java";
        }

        if (template.contains("SearchResp.java.vm")) {
            return packagePath + "pojo" + File.separator + "api" + File.separator + className + "SearchResp.java";
        }

        if (template.contains("InfoResp.java.vm")) {
            return packagePath + "pojo" + File.separator + "api" + File.separator + className + "InfoResp.java";
        }

        if (template.contains("UpdateOrSaveReq.java.vm")) {
            return packagePath + "pojo" + File.separator + "api" + File.separator + className + "UpdateOrSaveReq.java";
        }

        //前端
		/* Front-end
		 src
		 components*/
        if (template.contains("index.vue.vm")) {
            return "Front-end" + File.separator + "src" + File.separator + "components" + File.separator
                    + className + File.separator + "Index.vue";
        }

        if (template.contains("add-or-update.vue.vm")) {
            return "Front-end" + File.separator + "src" + File.separator + "components" + File.separator
                    + className + File.separator + "modal" + File.separator + "dataInfo.vue";
        }

        if (template.contains("add-or-update-withsub.vue.vm")) {
            return "Front-end" + File.separator + "src" + File.separator + "components" + File.separator
                    + className + File.separator + "modal_MasterSub" + File.separator + "dataInfo.vue";
        }
        //api
        if (template.contains("api.js.vm")) {
            return "Front-end" + File.separator + "src" + File.separator + "api" + File.separator + className + File.separator +
                    "api.js";
        }
				
		/*if (template.contains("index.yaml.vm")) {
			return "main" + File.separator + "resources" + File.separator + "static" + File.separator + "swagger"
					+ File.separator + "modules" + File.separator +  moduleName + File.separator +  className
					+ ".yaml";
		}*/
        //router
        if (template.contains("router.js.vm")) {
            return "Front-end" + File.separator + "src" + File.separator + "router" + File.separator + className + File.separator +
                    "index.js";
        }
        //Mock
        if (template.contains("Mock.js.vm")) {
            return "Front-end" + File.separator + "static" + File.separator + "mock" + File.separator
                    + className + "Mock.js";
        }
        if (template.contains("mockConfig.js.vm")) {
            return "Front-end" + File.separator + "static" + File.separator + "mock" + File.separator
                    + className + "_mockConfig.js";
        }

        return null;
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
