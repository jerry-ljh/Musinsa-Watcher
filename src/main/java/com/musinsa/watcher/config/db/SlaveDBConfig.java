package com.musinsa.watcher.config.db;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "slaveEntityManager", transactionManagerRef = "slaveTransactionManager",
    basePackages = {"com.musinsa.watcher.domain.product.slave",
        "com.musinsa.watcher.domain.product.discount.slave",
        "com.musinsa.watcher.domain.price.slave"})
public class SlaveDBConfig {

  private final Environment env;

  @Bean
  @ConfigurationProperties(prefix = "spring.slave.datasource")
  public DataSource slaveDataSource() {
    return DataSourceBuilder
        .create()
        .build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean slaveEntityManager(
      EntityManagerFactoryBuilder builder, @Qualifier("slaveDataSource") DataSource dataSource) {
    Map<String, Object> props = new HashMap<>();
    props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
    props.put("hibernate.hbm2ddl.auto", env.getProperty("spring.slave.hibernate.hbm2ddl.auto"));
    return builder
        .dataSource(dataSource)
        .packages("com.musinsa.watcher.domain")
        .properties(props)
        .build();
  }

  @Bean
  public PlatformTransactionManager slaveTransactionManager(
      @Qualifier("slaveEntityManager") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

}

