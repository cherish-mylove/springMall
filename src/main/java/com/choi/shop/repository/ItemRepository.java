package com.choi.shop.repository;

import com.choi.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findPageBy(Pageable pageable);
//    List<Item> findByTitleContains(String title);

    // id 기준 오름차순 정렬
    List<Item> findAllByOrderByIdAsc();

//    @Query(value = "SELECT * FROM item WHERE to_tsvector('simple', title) @@ to_tsquery('simple', :text)", nativeQuery = true)
//    List<Item> fullTextSearch(@Param("text") String text);

    @Query(value = "SELECT * FROM item WHERE to_tsvector('simple', title) @@ to_tsquery('simple', :text)",
            nativeQuery = true,
            countQuery = "SELECT count(*) FROM item WHERE to_tsvector('simple', title) @@ to_tsquery('simple', :text)")
    Page<Item> fullTextSearchPage(@Param("text") String text, Pageable pageable);

    List<Item> findByTitle(String title);
}

