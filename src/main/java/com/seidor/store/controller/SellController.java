package com.seidor.store.controller;

import com.seidor.store.dto.sell_dtos.SellRequestDTO;
import com.seidor.store.dto.sell_dtos.SellResponseDTO;
import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;
import com.seidor.store.mapper.SellDetailMapper;
import com.seidor.store.mapper.SellMapper;
import com.seidor.store.model.Sell;
import com.seidor.store.service.SellService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sell")
public class SellController {

    private final SellService sellService;


    public SellController(SellService sellService) {
        this.sellService = sellService;
    }


    @GetMapping
    public ResponseEntity<List<SellResponseDTO>> getAllSells() {
        return ResponseEntity.ok(SellMapper.toDtoList(sellService.getAllSells()));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SellResponseDTO>> getSellsByDate(@RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                @RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                @RequestParam(required = false) Integer userId,
                                                                @RequestParam(required = false) Double minTotalSale,
                                                                @RequestParam(required = false) Double maxTotalSale) {
        return ResponseEntity.ok(SellMapper.toDtoList(sellService.getSellsByFilters(startDate, endDate,userId,minTotalSale,maxTotalSale)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellResponseDTO> getSellById(@PathVariable("id") Integer id) {
        return  ResponseEntity.ok(SellMapper.toDto(sellService.getSellById(id)));
    }


    @GetMapping("/sell-details/{id}")
    public ResponseEntity<Set<SellDetailDTO>> getSellDetails(@PathVariable("id") Integer id) {
        return  ResponseEntity.ok(SellDetailMapper.toDtoList(sellService.getSellDetails(id)));
    }

    @PostMapping
    public ResponseEntity<SellResponseDTO> addSell(@Valid @RequestBody SellRequestDTO request) {
        Sell  sell = sellService.addSell(request);
        return ResponseEntity.ok(SellMapper.toDto(sell));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSell(@PathVariable("id") Integer id) {
        sellService.deleteSellbyId(id);
        return ResponseEntity.noContent().build();
    }


}
