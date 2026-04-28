package com.choi.shop.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {
    public String displayName;
    public Long id;
                                                        // 인터페이스는 메서드 이름이랑 파라미터만 정의
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities); // 부모(User) 생성자 호출, User 클래스가 파라미터 저장하게 함
    }                                           // 부모 클래스에 기본 생성자(파라미터 없는)가 없어서 super 필수임
}
