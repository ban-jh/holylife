package org.shaloman.pj.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 도메인 (users 테이블 매핑)
 */
@Data
public class User {

    private Long userId;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String roleGroup;
    private String accountStatus;
    private String memo;
    private Boolean emailNotification;
    private Boolean twoFactorAuth;
    private Boolean sessionAutoExpiry;
    private Boolean activityLog;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}