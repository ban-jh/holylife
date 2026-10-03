package org.shaloman.pj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 파일 업로드 설정.
 */
@Configuration
@ConfigurationProperties(prefix = "holylife.upload")
@Data
public class FileUploadConfig {

    /** 업로드 파일 저장 경로 */
    private String path;

    /** URL 접두사 */
    private String urlPrefix;
}