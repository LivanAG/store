package com.seidor.store.service.reactive;

import com.seidor.store.exception.my_exceptions.ResourceNotFoundException;
import com.seidor.store.model.Sell;
import com.seidor.store.model.SellDetail;
import com.seidor.store.repository.SellRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class SellServiceReactive {

    private final SellRepository sellRepository;

    public SellServiceReactive(SellRepository sellRepository) {
        this.sellRepository = sellRepository;
    }



    public Flux<SellDetail> getSellDetailsBySaleId(Integer saleId) {

        Sell sell = sellRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sell not found"));
        return Flux.fromIterable(sell.getSellDetails());

    }

}
