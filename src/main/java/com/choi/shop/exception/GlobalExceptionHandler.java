package com.choi.shop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice // 모든 Controller에서 발생하는 예외 잡아줌
public class GlobalExceptionHandler {

    // 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    String handleException(Exception e, Model model) {
        log.error("예상치 못한 에러 발생: {}", e.getMessage()); // ← 에러 로그
        log.error("스택 트레이스: ", e);                        // ← 상세 스택 트레이스
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요");
        return "error.html";
    }

    // 정적 파일 못 찾을 때
    @ExceptionHandler(NoResourceFoundException.class)
    String handleNoResource(NoResourceFoundException e) {
        log.warn("정적 파일 못 찾음: {}", e.getMessage());
        return "redirect:/list"; // 또는 404 페이지
    }
}
