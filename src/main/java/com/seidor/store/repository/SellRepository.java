package com.seidor.store.repository;

import com.seidor.store.model.Sell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;


public interface SellRepository extends JpaRepository<Sell,Integer> , JpaSpecificationExecutor<Sell> {
    List<Sell> findByUserId(Integer userId);

}
