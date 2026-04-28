package com.choi.shop.exception;

public class CommentException extends RuntimeException {

    private final String redirectUrl;   //  final로 데이터 못 건들이게 만들고, 메서드 통해서 데이터값 변경 가능

    public CommentException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
