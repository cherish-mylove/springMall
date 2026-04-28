package com.choi.shop.controller;

import com.choi.shop.dto.SalesDto;
import com.choi.shop.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/order")
    String order(String title, Integer price, Integer count, Authentication auth) {
        try {
            salesService.order(title, price, count, auth);
            return "redirect:/list";
        } catch (RuntimeException e) {
            return "redirect:/list?error=" + e.getMessage();
        }
    }

    @GetMapping("/order/all")
    String orderAll(Model model) {
        List<SalesDto> orders = salesService.orderAll();
        model.addAttribute("orders", orders);
        return "order-all.html";
    }
}