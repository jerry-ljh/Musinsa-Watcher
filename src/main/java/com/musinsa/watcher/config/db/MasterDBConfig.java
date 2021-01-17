package com.musinsa.watcher.config.db;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "masterEntityManager", transactionManagerRef = "masterTransactionManager", basePackages = {
    "com.musinsa.watcher.domain.product.master", "com.musinsa.watcher.domain.log.master",
    "com.musinsa.watcher.domain.price.master"})
public class MasterDBConfig {

  private final Environment env;

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.master.datasource")
  public DataSource masterDataSource() {
    return DataSourceBuilder
        .create()
        .build();
  }

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean masterEntityManager(
      EntityManagerFactoryBuilder builder, @Qualifier("masterDataSource") DataSource dataSource) {
    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
    props.put("hibernate.hbm2ddl.auto", env.getProperty("spring.master.hibernate.hbm2ddl.auto"));

    return builder
        .dataSource(dataSource)
        .packages("com.musinsa.watcher.domain")
        .properties(props)
        .build();
  }

  @Primary
  @Bean
  public PlatformTransactionManager masterTransactionManager(
      @Qualifier("masterEntityManager") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}
