# SpringMall

Spring Boot 기반 개인 쇼핑몰 프로젝트입니다.

## 기술 스택

- Java, Spring Boot, Spring Security
- Thymeleaf, HTML/CSS
- PostgreSQL (Supabase), JPA
- AWS S3
- GitHub

## 주요 기능

- 회원가입 / 로그인 (Spring Security 세션 방식)
- 상품 목록 조회 / 검색 / 페이지네이션
- 상품 등록 / 수정 / 삭제 (관리자 전용)
- 상품 상세 조회
- 댓글(리뷰) 작성 / 수정 / 삭제
- 주문 생성 / 전체 주문 조회 (관리자 전용)
- 마이페이지 (주문 내역 조회)

## API 테스트 (Postman)

Postman을 활용하여 주요 API에 대한 테스트 케이스를 작성하고 실행하였습니다.

- 테스트 대상: Auth / Item / Order / Comment
- 테스트 스크립트: 상태코드, 응답시간, 응답 본문 검증
- 발견한 버그 3건을 버그 리포트로 기록

Collection 파일: `/postman/SpringMall API Test.postman_collection.json`

## 발견한 주요 버그

| 버그 ID | 내용 | 심각도 |
|---|---|---|
| BUG-001 | 존재하지 않는 상품 ID 조회 시 404 미반환 | Medium |
| BUG-002 | 검색 결과 없을 때 안내 메시지 미노출 | Low |
| BUG-003 | 검색 결과 없을 때 0페이지 링크 노출 | Medium |

## QA 포트폴리오

[노션 링크]
