package com.rahul.invetoryservice.service;

import com.rahul.invetoryservice.dto.InventoryResponse;
import com.rahul.invetoryservice.model.Inventory;
import com.rahul.invetoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode){
//        log.info("Wait Started");
//        Thread.sleep(10000);
//        log.info("Wait Ended");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity()>0)
                        .build()
                ).toList();
    }

//    public List<InventoryResponse> isInStock(List<String> skuCodes) {
//        return skuCodes.stream()
//                .map(skuCode -> {
//
//                    Inventory inventory = inventoryRepository.findBySkuCodeIn(skuCodes).orElse(null);
//                    boolean isInStock = !Objects.isNull(inventory) && inventory.getQuantity() > 0;
//                    return InventoryResponse.builder()
//                            .skuCode(skuCode)
//                            .isInStock(isInStock)
//                            .build();
//                }).toList();
//    }


}
