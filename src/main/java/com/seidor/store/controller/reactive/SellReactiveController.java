package com.seidor.store.controller.reactive;

import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.mapper.SellDetailMapper;
import com.seidor.store.service.reactive.SellServiceReactive;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/reactive/sell")
public class SellReactiveController {

    private final SellServiceReactive sellServiceReactive;

    public SellReactiveController(SellServiceReactive sellServiceReactive) {
        this.sellServiceReactive = sellServiceReactive;
    }
    @GetMapping("/details/{id}")
    public Flux<SellDetailDTO> getSell(@PathVariable Integer id) {

        return sellServiceReactive.getSellDetailsBySaleId(id)
                .map(SellDetailMapper::toDto);
    }
}
