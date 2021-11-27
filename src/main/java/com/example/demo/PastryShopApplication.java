 package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PastryShopApplication {

    @Value("${spring.redis.address}")
    private String address;

    public static void main(String[] args) {
        SpringApplication.run(PastryShopApplication.class, args);
    }

    @Bean
    public RedisConnectionFactory factory(){
        return new LettuceConnectionFactory(address, 6379);
    }

}
