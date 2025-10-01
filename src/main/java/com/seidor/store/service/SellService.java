package com.seidor.store.service;

import com.seidor.store.specification.SellSpecification;
import com.seidor.store.config.AuthUtil;
import com.seidor.store.dto.sell_dtos.SellRequestDTO;
import com.seidor.store.dto.sell_detail_dtos.SellDetailDTO;
import com.seidor.store.exception.my_exceptions.InsufficientStockException;
import com.seidor.store.exception.my_exceptions.ResourceNotFoundException;
import com.seidor.store.model.*;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.SellRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    public Sell getSellById(Integer id) {
        return sellRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sell not found"));
    }

    public List<Sell> getSellsByFilters(
                LocalDateTime startDate,
                LocalDateTime endDate,
                Integer userId,
                Double minTotalSale,
                Double maxTotalSale
        ) {
            Specification<Sell> spec = null;

            // Filtro por fecha de creación
            if (startDate != null) {
                spec = SellSpecification.createdAfter(startDate);
            }
            if (endDate != null) {
                spec = (spec == null) ? SellSpecification.createdBefore(endDate) : spec.and(SellSpecification.createdBefore(endDate));
            }

            // Filtro por userId
            if (userId != null) {
                spec = (spec == null) ? SellSpecification.hasUserId(userId) : spec.and(SellSpecification.hasUserId(userId));
            }

            // Filtro por totalSale
            if (minTotalSale != null) {
                spec = (spec == null) ? SellSpecification.totalSaleGreaterThanOrEqual(minTotalSale) : spec.and(SellSpecification.totalSaleGreaterThanOrEqual(minTotalSale));
            }
            if (maxTotalSale != null) {
                spec = (spec == null) ? SellSpecification.totalSaleLessThanOrEqual(maxTotalSale) : spec.and(SellSpecification.totalSaleLessThanOrEqual(maxTotalSale));
            }


            return (spec == null) ? sellRepository.findAll() : sellRepository.findAll(spec);
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
        this.getSellById(id);
        sellRepository.deleteById(id);
    }





    public Set<SellDetail> getSellDetails(Integer id) {
        Sell sell = this.getSellById(id);
        return sell.getSellDetails();
    }
}
