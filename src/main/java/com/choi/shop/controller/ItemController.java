package com.choi.shop.controller;

import com.choi.shop.entity.Comment;
import com.choi.shop.entity.Item;
import com.choi.shop.repository.CommentRepository;
import com.choi.shop.service.ItemService;
import com.choi.shop.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;

    @GetMapping("/list")
    String list(Model model, @RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<Item> result = itemService.getList(page);
        model.addAttribute("items", result);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "list.html";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('관리자')")
    String add(Model model) {
        model.addAttribute("item", new Item());
        return "add.html";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('관리자')")
    String addPost(@Valid @ModelAttribute Item item, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors()
                    .get(0).getDefaultMessage();
            model.addAttribute("error", errorMsg);
            return "add.html";
        }

        try {
            itemService.addItem(item.getTitle(), item.getPrice(), item.getCount());
            return "redirect:/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "add.html";
        }
    }

    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename) {
        var result = s3Service.createPresignedUrl("test/" + filename);
        System.out.println(result);
        return result;
    }

    @PostMapping("/search")
    String search(@RequestParam String search,
                  @RequestParam(name = "page", defaultValue = "1") Integer page, Model model) {
        Page<Item> result = itemService.search(search, page);

        model.addAttribute("items", result);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("keyword", search    );
        model.addAttribute("currentPage", page);
        return "list.html";
    }

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, Model model) {
        List<Comment> comments = commentRepository.findAllByItemId(id);
        return itemService.getDetail(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    model.addAttribute("comments", comments);
                    return "detail.html";
                })
                .orElse("redirect:../list");
    }

    @GetMapping("/update/{id}")
    String update(@PathVariable Long id, Model model) {
        return itemService.getDetail(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    return "update.html";
                })
                .orElse("redirect:/list");
    }

    @PostMapping("/update")
    String update(Long id, String title, Integer price) {
        itemService.update(id, title, price);
        return "redirect:/list";
    }

    @DeleteMapping("/delete")
    ResponseEntity<String> delete(@RequestParam Long id) {
        try {
            itemService.delete(id);
            return ResponseEntity.status(200).body("삭제완료");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}