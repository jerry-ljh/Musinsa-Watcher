package com.musinsa.watcher.domain.log.master;

import com.musinsa.watcher.domain.log.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

}
