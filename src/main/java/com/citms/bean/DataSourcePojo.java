package com.citms.bean;

import lombok.Data;

/**
 * @author pei.wang
 */

@Data
public class DataSourcePojo {
    private String name;

    /**
     * JDBC driver
     */
    private String driverClassName;

    /**
     * JDBC url 地址
     */
    private String url;

    /**
     * JDBC 用户名
     */
    private String username;

    /**
     * JDBC 密码
     */
    private String password;

}
