package com.care.medi.config;

//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//@Configuration
public class DatabaseConfig {
//
//    @Value("${secrets.file.path}")
//    private String filePath;
//
//    @Bean
//    public DataSource dataSource() throws IOException {
//        Properties props = new Properties();
//
//        // This will look in the root folder (working directory)
//        try (InputStream input = new FileInputStream(filePath)) {
//            props.load(input);
//        }
//
//        System.out.println("DatabaseConfig connected successfully..😍😍");
//
//        return DataSourceBuilder.create()
//                .url(props.getProperty("url"))
//                .username(props.getProperty("username"))
//                .password(props.getProperty("password"))
//                .driverClassName("org.postgresql.Driver")
//                .build();
//    }
}