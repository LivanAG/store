package com.seidor.store.controller;

import com.seidor.store.dto.sellDTOS.SellRequestDTO;
import com.seidor.store.dto.sellDTOS.SellResponseDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.mapper.SellDetailMapper;
import com.seidor.store.mapper.SellMapper;
import com.seidor.store.model.Sell;
import com.seidor.store.service.SellService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
