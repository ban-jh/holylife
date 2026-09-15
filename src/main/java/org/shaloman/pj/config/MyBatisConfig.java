package org.shaloman.pj.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정.
 * mapper 패키지를 스캔하여 Mapper 인터페이스를 Bean으로 등록한다.
 * XML 매퍼 파일은 application.yml의 mybatis.mapper-locations에서 지정한다.
 */
@Configuration
@MapperScan(basePackages = "org.shaloman.pj.mapper")
public class MyBatisConfig {
}