package com.musinsa.watcher.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfiguration {

  @PersistenceContext(unitName = "masterEntityManager")
  private EntityManager masterEntityManager;

  @PersistenceContext(unitName = "slaveEntityManager")
  private EntityManager slaveEntityManager;

  @Bean(value = "masterJPAQueryFactory")
  public JPAQueryFactory jpaMasterQueryFactory() {
    return new JPAQueryFactory(masterEntityManager);
  }

  @Bean(value = "slaveJPAQueryFactory")
  public JPAQueryFactory jpaSlaveQueryFactory() {
    return new JPAQueryFactory(slaveEntityManager);
  }
}
