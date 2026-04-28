package com.choi.shop.service;

import com.choi.shop.dto.SalesDto;
import com.choi.shop.entity.Sales;
import com.choi.shop.repository.ItemRepository;
import com.choi.shop.config.CustomUser;
import com.choi.shop.entity.Member;
import com.choi.shop.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final ItemRepository itemRepository;

    @Transactional // 주문 저장 + 수량 차감 둘 다 성공해야 commit, 하나라도 실패하면 rollback
    public void order(String title, Integer price, Integer count, Authentication auth) {

        // 1. 재고 확인 및 차감
        if (count == null || count <= 0) {
            throw new RuntimeException("주문 수량은 1개 이상이어야 합니다");
        }

        var items = itemRepository.findByTitle(title);
        if (items.isEmpty()) {
            throw new RuntimeException("상품이 없습니다");
        }

        var item = items.get(0);
        if (item.getCount() < count) {
            throw new RuntimeException("재고가 부족합니다. 현재 재고: " + item.getCount() + "개");
        }

        item.setCount(item.getCount() - count); // 수량 차감
        itemRepository.save(item);

        // 2. 주문 저장
        CustomUser user = (CustomUser) auth.getPrincipal();
        var member = new Member();
        member.setId(user.id);

        Sales sales = new Sales();
        sales.setItemName(title);
        sales.setPrice(price);
        sales.setCount(count);
        sales.setMemberId(member);
        salesRepository.save(sales);
    }

    public List<SalesDto> orderAll() {
        List<Sales> result = salesRepository.customfindAll();
        return result.stream()
                .map(s -> {
                    SalesDto dto = new SalesDto();
                    dto.itemName = s.getItemName();
                    dto.price = s.getPrice();
                    dto.username = s.getMemberId().getUsername();
                    return dto;
                }).toList();
    }
}