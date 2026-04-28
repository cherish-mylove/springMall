package com.choi.shop.controller;

import com.choi.shop.entity.Comment;
import com.choi.shop.exception.CommentException;
import com.choi.shop.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    String add(@RequestParam String content,
                       @RequestParam Long itemId,
                       Authentication auth,
                       RedirectAttributes redirectAttributes) {
        try {
            commentService.add(content, itemId, auth);
            return "redirect:/detail/" + itemId;
        } catch (CommentException e) {
            redirectAttributes.addFlashAttribute("commentError", e.getMessage());
            return "redirect:" + e.getRedirectUrl();
        }
    }

    @GetMapping("/comment/edit/{id}")
    String edit(@PathVariable Long id,
                       @RequestParam Long itemId,
                       Authentication auth,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            Comment comment = commentService.getEdit(id, itemId, auth);
            model.addAttribute("comment", comment);
            model.addAttribute("itemId", itemId);
            return "comment-edit.html";
        } catch (CommentException e) {
            redirectAttributes.addFlashAttribute("commentError", e.getMessage());
            return "redirect:" + e.getRedirectUrl();
        }
    }

    @PostMapping("/comment/edit")
    String edit(@RequestParam Long id,
                           @RequestParam String content,
                           @RequestParam Long itemId,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        try {
            commentService.postEdit(id, content, itemId, auth);
            return "redirect:/detail/" + itemId;
        } catch (CommentException e) {
            redirectAttributes.addFlashAttribute("commentError", e.getMessage());
            return "redirect:" + e.getRedirectUrl();
        }
    }

    @DeleteMapping("/deleteComment")
    @ResponseBody
    ResponseEntity<String> delete(@RequestParam Long id,
                                  @RequestParam Long itemId,
                                  Authentication auth) {
        try {
            commentService.delete(id, itemId, auth);
            return ResponseEntity.status(200).body("삭제완료");
        } catch (CommentException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}