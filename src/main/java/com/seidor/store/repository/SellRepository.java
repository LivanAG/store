package com.seidor.store.repository;

import com.seidor.store.model.Sell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellRepository extends JpaRepository<Sell,Integer> {
    List<Sell> findByUserId(Integer userId);
}
