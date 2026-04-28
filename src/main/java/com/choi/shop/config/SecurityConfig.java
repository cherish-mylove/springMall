package com.choi.shop.config;

import com.choi.shop.config.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration      // @Bean은 반드시 @Configuration 클래스 안에서만 써야 해요.
@EnableWebSecurity
@EnableMethodSecurity  // ← @PreAuthorize 쓰려면 필수!
public class SecurityConfig {

    @Bean       // 외부라이브러리는 갔다 쓰려면 직접 빈 등록해야 함
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());

                // STATELESS 제거! 세션 쓸 거니까
        // http.sessionManagement((session) -> session
        //         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // );

        http.addFilterBefore(new JwtFilter(), ExceptionTranslationFilter.class);

        http.authorizeHttpRequests((authorize) ->
                authorize.requestMatchers("/add", "/delete").hasAuthority("관리자")
                         .requestMatchers("/**").permitAll()
        );

        http.formLogin((formLogin) -> formLogin.loginPage("/login")
                .defaultSuccessUrl("/")
//                .failureUrl("/fail")  실패 시 기본적으로 /login/error로 이동
        );

        http.logout( logout -> logout.logoutUrl("/logout") );

        return http.build();
    }
}
