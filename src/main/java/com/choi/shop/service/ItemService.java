package com.choi.shop.service;

import com.choi.shop.entity.Item;
import com.choi.shop.repository.CommentRepository;
import com.choi.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public void addItem(String title, Integer price, Integer count) {

        if (title == null || title.isBlank()) {
            throw new RuntimeException("상품명을 입력해주세요");
        }
        if (price == null || price < 0) {
            throw new RuntimeException("가격은 0원 이상이어야 합니다");
        }
        if (count == null || count < 0) {
            throw new RuntimeException("수량은 0개 이상이어야 합니다");
        }
        Item item = new Item();
        item.setTitle(title);   // private으로 선언 된 필드값이지만 setter 함수를 이용해서 간접적으로 바꿀 수 있음.
        item.setPrice(price);
        item.setCount(count);
        itemRepository.save(item);
    }

    public void update(Long id, String title, Integer price) {

        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다"));
        item.setTitle(title);
        item.setPrice(price);
        itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다"));
        itemRepository.deleteById(id);
    }

    public Page<Item> getList(int page) {
        return itemRepository.findPageBy(PageRequest.of(page - 1, 5));
    }

    public Page<Item> search(String keyword, int page) {
        String 처리된검색어 = keyword.trim().replaceAll("[^가-힣a-zA-Z0-9\\s]", "")
                .replace(" ", " & ");

        if (처리된검색어.isBlank()) {
            return itemRepository.findPageBy(PageRequest.of(page - 1, 5));
        }
        return itemRepository.fullTextSearchPage(처리된검색어, PageRequest.of(page - 1, 5));
    }

    // [추가] 상세 조회 서비스로 이동
    public Optional<Item> getDetail(Long id) {
        return itemRepository.findById(id);
    }
}
