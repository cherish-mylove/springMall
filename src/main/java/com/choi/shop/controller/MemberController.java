package com.choi.shop.controller;

import com.choi.shop.config.CustomUser;
import com.choi.shop.config.JwtUtil;
import com.choi.shop.dto.MemberDto;
import com.choi.shop.entity.Sales;
import com.choi.shop.repository.MemberRepository;
import com.choi.shop.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/signup")
    String signup() {

        return "signup.html";
    }

    @PostMapping("/signup")
    String signup(String username, String password, String displayName, Model model) throws Exception {
        try {
            memberService.signup(username, password, displayName);
            return "redirect:/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("prevUsername", username);
            model.addAttribute("prevDisplayName", displayName);
            return "signup.html";
        }
    }

    @GetMapping("/login")
    String login() {
        return "login.html";
    }

    @GetMapping("/mypage")
    String myPage(Authentication auth, Model model) {
        CustomUser user = (CustomUser) auth.getPrincipal(); // getPrincipal() 반환 타입이 Object라서 CustomUser 필드에 바로 접근 못함
        System.out.println(auth.getPrincipal());
        System.out.println(auth);
        System.out.println(user.displayName);

        // 주문 내역 조회
        var member = memberRepository.findById(user.id);
        if (member.isPresent()) {
            List<Sales> salesList = member.get().getSales();
            model.addAttribute("salesList", salesList);
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("displayName", user.displayName);
//        System.out.println(auth.getName());
//        System.out.println(auth.isAuthenticated());
//        System.out.println(auth.getAuthorities().contains(new SimpleGrantedAuthority("일반유저")));
        return "mypage.html";
    }

    @GetMapping("/user/1")
    @ResponseBody
    public MemberDto getUser() {
        var a = memberRepository.findById(3L);
        var result = a.get();
        var data = new MemberDto(result.getUsername(), result.getDisplayName());
        return data;
    }

    @PostMapping("/login/jwt")
    @ResponseBody
    public String loginJWT(@RequestBody Map<String, String> data, HttpServletResponse response) {

        // 1. username, password로 인증 토큰 생성
        var authToken = new UsernamePasswordAuthenticationToken(
                data.get("username"), data.get("password")
        );

        // 2. AuthenticationManager한테 인증 요청
        //    → 내부적으로 MyUserDetailsService.loadUserByUsername() 호출
        //    → DB에서 username으로 회원 찾아서 비밀번호 비교
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);

        // 3. 인증 성공하면 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 4. JWT 토큰 생성
        var auth2 = SecurityContextHolder.getContext().getAuthentication();
        var jwt = JwtUtil.createToken(auth2);

        // 5. JWT를 쿠키에 담아서 브라우저에 전달
        var cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(10);       // 10초 유효
        cookie.setHttpOnly(true);   // JS에서 접근 못하게 (보안)
        cookie.setPath("/");        // 모든 경로에서 쿠키 사용
        response.addCookie(cookie);

        return "jwt발급완료";
    }

    @GetMapping("/my-page/jwt")
    @ResponseBody
    public String myPageJWT(Authentication auth) {
        var user = (CustomUser) auth.getPrincipal();
        System.out.println(user);
        System.out.println(user.displayName);
        System.out.println(user.getAuthorities());
        return "마이데이터페이지";
    }
}