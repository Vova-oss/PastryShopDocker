package com.example.demo.Scheduled;

import com.example.demo.Entity.Basket;
import com.example.demo.Entity.Product;
import com.example.demo.Services.BasketService;
import com.example.demo.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DeleteProductsByTime {
    @Autowired
    BasketService basketService;
    @Autowired
    ProductService productService;


        @Scheduled(cron = "0 */30 * * * *")
//    @Scheduled(cron = "*/30 * * * * *")
    public void taskOf22Pr() throws IOException {
        System.out.println("Oket~");
        List<Basket> allBaskets = basketService.findAll();
        for(Basket basket:allBaskets){
                                                    // 1 секунда == 1000
                                // 24 часа = 1440 мин = 86400 сек = 86_400_000
            if(new Date().getTime() - basket.getDateCreate() > 86_400_000){
                long amount = basketService.deleteAllAmountByBasket(basket);
//                productService.addSomeAmountOfProductByName(basket.getNameOfProduct(), String.valueOf(amount));
            }
        }
    }
}
