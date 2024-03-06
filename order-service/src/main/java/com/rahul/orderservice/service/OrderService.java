package com.rahul.orderservice.service;

import com.rahul.orderservice.dto.InventoryResponse;
import com.rahul.orderservice.dto.OrderLineItemsDto;
import com.rahul.orderservice.dto.OrderRequest;
import com.rahul.orderservice.exeption.OrderProcessingException;
import com.rahul.orderservice.model.Order;
import com.rahul.orderservice.model.OrderLineItems;
import com.rahul.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final WebClient.Builder webClientBuilder;
    private final OrderRepository orderRepository;
    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        try {
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            if( inventoryResponseArray == null){
                return "Product(s) not in stock";
            }
            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
            if (allProductsInStock) {
                orderRepository.save(order);
                return "Order placed Successfully";
            } else {
                return "Product(s) not in stock"; // More specific exception
            }
        } catch (WebClientException e) { // Catch specific webclient exception
            throw new OrderProcessingException("Failed to check inventory", e); // Encapsulate original exception
        } catch (Exception e) { // Catch other general exceptions
            throw new OrderProcessingException("Order processing failed", e); // Encapsulate original exception
        }
    }


    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
