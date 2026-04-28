package com.choi.shop.service;

import com.choi.shop.entity.Member;
import com.choi.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public void signup(String username, String password, String displayName) {

        // 빈 값 체크
        if (username.isBlank() || password.isBlank() || displayName.isBlank()) {
            throw new RuntimeException("모든 항목을 입력해주세요");
        }

//        // 비밀번호 길이 체크
//        if (password.length() < 6) {
//            throw new RuntimeException("비밀번호는 6자 이상이어야 합니다");
//        }

        // 비번 길이, 영문, 숫자, 특수문자 포함
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$")) {
            throw new RuntimeException("비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다");
        }

        // 중복 아이디 체크
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 사용중인 아이디입니다");
        }

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setDisplayName(displayName);
        memberRepository.save(member);
    }
}
