package org.shaloman.pj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 설정.
 * BCrypt 방식을 사용하며, salt는 BCrypt 내부에서 랜덤 생성하여 hash에 포함된다.
 * strength 값은 application.yml의 holylife.security.password.bcrypt-strength에서 지정한다.
 */
@Configuration
public class PasswordConfig {

    @Value("${holylife.security.password.bcrypt-strength:10}")
    private int bcryptStrength;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }
}