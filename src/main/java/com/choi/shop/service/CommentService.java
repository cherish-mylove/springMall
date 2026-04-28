package com.choi.shop.service;

import com.choi.shop.entity.Comment;
import com.choi.shop.exception.CommentException;
import com.choi.shop.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 공통 검증 메서드
    private void checkLogin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("비로그인 사용자 댓글 시도");
            throw new CommentException("로그인이 필요합니다", "/login");
        }
    }

    private void validateComment(String content, Long itemId) {
        if (content == null || content.isBlank()) {
            log.warn("빈 댓글 전송 시도 itemId: {}", itemId);
            throw new CommentException("댓글 내용을 입력해주세요", "/detail/" + itemId);
        }
    }

    private Comment validateCommentOwner(Long id, Long itemId, Authentication auth) {
        Comment content = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 댓글 접근 Id: {}", id);
                    return new CommentException("존재하지 않는 댓글입니다", "/detail/" + itemId);
                });

        if (!content.getUsername().equals(auth.getName())) {
            log.warn("댓글 작성자 불일치 Id: {}, 요청자: {}", id, auth.getName());
            throw new CommentException("본인 댓글만 수정할 수 있습니다", "/detail/" + itemId);
        }
        return content;
    }

    // 댓글 등록
    public Long add(String content, Long itemId, Authentication auth) {
        checkLogin(auth);
        validateComment(content, itemId);

        Comment commentEntity = new Comment();
        commentEntity.setContent(content);
        commentEntity.setItemId(itemId);
        commentEntity.setUsername(auth.getName());
        commentRepository.save(commentEntity);

        log.info("댓글 등록 완료 itemId: {}, 작성자: {}", itemId, auth.getName());
        return itemId;
    }

    // 댓글 수정 페이지용 조회
    public Comment getEdit(Long id, Long itemId, Authentication auth) {
        checkLogin(auth);
        return validateCommentOwner(id, itemId, auth);
    }

    //   댓글 수정
    public void postEdit(Long id, String content, Long itemId, Authentication auth) {
        checkLogin(auth);
        validateComment(content, itemId);
        Comment commentEntity = validateCommentOwner(id, itemId, auth);
        commentEntity.setContent(content);
        commentRepository.save(commentEntity);
        log.info("댓글 수정 완료 Id: {}, 수정자: {}", id, auth.getName());
    }

    // 댓글 삭제
    public void delete(Long id, Long itemId, Authentication auth) {
        checkLogin(auth);
        Comment content = validateCommentOwner(id, itemId, auth);
        commentRepository.delete(content);
        log.info("댓글 삭제 완료 Id: {}, 삭제자: {}", id, auth.getName());
    }
}