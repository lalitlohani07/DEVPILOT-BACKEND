/*
  AppConfig is the central Spring configuration class for the application.
 
  Its responsibility is to create and configure infrastructure beans that are
  shared across the application. Instead of creating objects manually using
  "new" inside different classes, Spring creates them once and manages their
  lifecycle through the IoC container.
 
  This class creates the application's DataSource bean using HikariCP.
  The DataSource is responsible for providing database connections, while
  HikariCP manages a pool of reusable connections to improve performance.
 
  @Configuration
  Marks this class as a Spring configuration class. When the Spring IoC
  container starts, it scans this class and processes all methods annotated
  with @Bean.
 
  @PropertySource("classpath:application.properties")
  Tells Spring to load configuration values from the
  application.properties file located under src/main/resources.
  The term "classpath" refers to the location where Java looks for compiled
  classes and application resources at runtime. In a Maven project, everything
  inside src/main/resources is automatically placed on the classpath.
 
  @Value
  Injects individual values from the loaded properties file into Java fields.
  For example, @Value("${db.url}") tells Spring to read the value of the
  "db.url" property and assign it to the corresponding field.
 
  @Bean
  Marks a method whose return value should be managed by the Spring IoC
  container. By default, Spring creates the bean once (Singleton scope) and
  injects the same instance wherever it is required.
 
  Database credentials are intentionally stored outside the source code in
  application.properties instead of being hardcoded. This separates
  configuration from application logic and makes it easier to use different
  configurations for different environments. The real application.properties
  file should not be committed to Git. Instead, an
  application.properties.example file containing placeholder values is
  committed so that other developers know which configuration values are
  required.
 */

package com.lalit.devpilot.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
@ComponentScan("com.lalit.devpilot")
@Configuration
@PropertySource("classpath:application.properties")
public class appConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return new HikariDataSource(config);
    }
}

