package com.choi.shop.service;

import com.choi.shop.config.CustomUser;
import com.choi.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        // DB에서 username으로 회원 찾기
        var result = memberRepository.findByUsername(username);
        if (result.isEmpty()){
            throw new UsernameNotFoundException("그런 아이디 없음");
        }

        var user = result.get();

        // 권한 목록 생성
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> adminList = List.of("admin", "choi");

        if (adminList.contains(username)) {  // admin 계정만 관리자
            authorities.add(new SimpleGrantedAuthority("관리자"));
        } else {
            authorities.add(new SimpleGrantedAuthority("일반유저"));
        }

        // CustomUser 생성해서 반환
        var a = new CustomUser(user.getUsername(), user.getPassword(), authorities);
        a.displayName = user.getDisplayName();
        a.id = user.getId();
        return a;
        // → AuthenticationManager가 받아서 비밀번호 비교
    }
}

