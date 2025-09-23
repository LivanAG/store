package com.seidor.store.service;

import com.seidor.store.config.AuthUtil;
import com.seidor.store.dto.ProductRequestDTO;
import com.seidor.store.dto.SellRequestDTO;
import com.seidor.store.dto.sellDetailDTO.SellDetailDTO;
import com.seidor.store.mapper.ProductMapper;
import com.seidor.store.model.*;
import com.seidor.store.repository.ProductRepository;
import com.seidor.store.repository.SellRepository;
import com.seidor.store.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SellService {

    private final SellRepository sellRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    public SellService(SellRepository sellRepository, ProductRepository productRepository,
                        UserRepository userRepository,
                        StorageService storageService) {
        this.sellRepository = sellRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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

        Sell sell = new Sell();
        sell.setUser(user);

        Set<SellDetail> details = new HashSet<>();

        for (SellDetailDTO detailDto : request.getSellDetails()) {

            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            //Comprobamos que el stock sea suficiente antes de ejecutar la venta
            if (!storageService.isEnoughStock(product, detailDto.getAmount())) {
                throw new RuntimeException("No quedan suficientes unidades del producto"+detailDto.getProductId()+" para satisfacer la orden");
            }

            SellDetail sd = new SellDetail();
            sd.setSell(sell);
            sd.setProduct(product);
            sd.setAmount(detailDto.getAmount());

            //Reducimos stock
            storageService.reduceStock(product, detailDto.getAmount());
            details.add(sd);

        }
        sell.setSellDetails(details);


        double total = details.stream()
                .mapToDouble(d -> d.getProduct().getStorage().getPrice() * d.getAmount())
                .sum();

        sell.setTotalSale(total);


        return sellRepository.save(sell);
    }
}
