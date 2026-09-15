package org.shaloman.pj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 트랜잭션 설정.
 * spring-boot-starter-jdbc가 제공하는 DataSourceTransactionManager를
 * @Transactional 어노테이션으로 사용할 수 있도록 활성화한다.
 *
 * Service 계층에서 @Transactional 선언 시 자동으로 트랜잭션이 적용된다.
 * - @Transactional: 기본값 (readOnly=false, propagation=REQUIRED)
 * - @Transactional(readOnly=true): 읽기 전용 트랜잭션
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
}