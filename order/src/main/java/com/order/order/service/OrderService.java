package com.order.order.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.ProductDTO;
import com.order.order.common.ErrorOrderResponse;
import com.order.order.common.OrderResponse;
import com.order.order.common.SuccessOrderResponse;
import com.order.order.dto.OrderDTO;
import com.order.order.model.Orders;
import com.order.order.repo.OrderRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final WebClient webClient;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ModelMapper modelMapper;

    public OrderService(WebClient.Builder webClientBuilder, OrderRepo orderRepo, ModelMapper modelMapper) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v1").build();
        this.orderRepo=orderRepo;
        this.modelMapper=modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        List<Orders>orderList = orderRepo.findAll();
        return modelMapper.map(orderList, new TypeToken<List<OrderDTO>>(){}.getType());
    }

    public OrderResponse saveOrder(OrderDTO OrderDTO) {

        int itemId = OrderDTO.getItemId();

        try {
            InventoryDTO inventoryResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/item/{itemId}").build(itemId))
                    .retrieve()
                    .bodyToMono(InventoryDTO.class)
                    .block();

            assert inventoryResponse != null;

            int productId = inventoryResponse.getProductId();

            ProductDTO productResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/product/{productId}").build(productId))
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .block();


            if (inventoryResponse.getQuantity() > 0 ){
                orderRepo.save(modelMapper.map(OrderDTO, Orders.class));
                return new SuccessOrderResponse(OrderDTO);
            }else {
                return new ErrorOrderResponse("Item not available Please try later..");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public OrderDTO updateOrder(OrderDTO OrderDTO) {
        orderRepo.save(modelMapper.map(OrderDTO, Orders.class));
        return OrderDTO;
    }

    public String deleteOrder(Integer orderId) {
        orderRepo.deleteById(orderId);
        return "Order deleted";
    }

    public OrderDTO getOrderById(Integer orderId) {
        Orders order = orderRepo.getOrderById(orderId);
        return modelMapper.map(order, OrderDTO.class);
    }

}
