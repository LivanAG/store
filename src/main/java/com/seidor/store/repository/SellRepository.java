package com.seidor.store.repository;

import com.seidor.store.model.Sell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface SellRepository extends JpaRepository<Sell,Integer> , JpaSpecificationExecutor<Sell> {
    List<Sell> findByUserId(Integer userId);

}
