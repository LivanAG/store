package com.seidor.store.service;

import com.seidor.store.config.AuthUtil;
import com.seidor.store.dto.sellDTOS.SellRequestDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.exception.myExceptions.InsufficientStockException;
import com.seidor.store.exception.myExceptions.ResourceNotFoundException;
import com.seidor.store.model.*;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.SellRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SellService {

    private final SellRepository sellRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final StorageService storageService;

    public SellService(SellRepository sellRepository, ProductRepository productRepository,
                       ProductService productService,
                       StorageService storageService) {
        this.sellRepository = sellRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.storageService = storageService;
    }



    public List<Sell> getAllSells(){

        //Recupero el usuario logueado que quiere realizar la peticion
        User user = AuthUtil.getCurrentUser();

        Role role = user.getRole();
        if(role == Role.ADMIN){
            return sellRepository.findAll();
        }else{
            return sellRepository.findByUserId(user.getId());
        }

    }


    public Sell addSell(SellRequestDTO request) {


        //Recupero el usuario logueado que quiere realizar la peticion
        User user = AuthUtil.getCurrentUser();

        //Creo la venta
        Sell sell = new Sell();

        //le asigno el usuario
        sell.setUser(user);

        //Creamos nuestro set de sell details
        Set<SellDetail> details = new HashSet<>();

        //Rellenamos nuestro set
        for (SellDetailDTO detailDto : request.getSellDetails()) {

            //Antes de mappear se SellDetailDTO a SellDetail
            //Comprobamos que el producto existe
            Product product = productService.getProductById(detailDto.getProductId());

            //Comprobamos que el stock sea suficiente antes de ejecutar la venta
            if (!storageService.isEnoughStock(product, detailDto.getAmount())) {
                throw new InsufficientStockException(
                        detailDto.getProductId(),
                        detailDto.getAmount(),
                        storageService.getAvailableStock(product)
                );
            }

            //Despues de todas las comprobaciones, creamos el sellDetail
            SellDetail sd = new SellDetail();
            sd.setSell(sell);
            sd.setProduct(product);
            sd.setAmount(detailDto.getAmount());

            //Reducimos stock
            storageService.reduceStock(product, detailDto.getAmount());

            //Agregamos el sellDetail al set de detalles de venta
            details.add(sd);

        }

        //Agregamos nuestro set antes creado a nuestra venta
        sell.setSellDetails(details);


        //Calculamos el total de la venta
        double total = details.stream()
                .mapToDouble(d -> d.getProduct().getStorage().getPrice() * d.getAmount())
                .sum();

        sell.setTotalSale(total);


        return sellRepository.save(sell);
    }



    public void deleteSellbyId(Integer id) {
        Sell sell = sellRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sell not found"));
        sellRepository.deleteById(id);
    }


    public Sell getSellById(Integer id) {
        return sellRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sell not found"));
    }


    public Set<SellDetail> getSellDetails(Integer id) {
        Sell sell = sellRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sell not found"));
        return sell.getSellDetails();
    }
}
