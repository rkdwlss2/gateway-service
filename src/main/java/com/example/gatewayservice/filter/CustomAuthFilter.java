package com.example.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {
    public CustomAuthFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config){
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("token")){
                return handleUnAuthorized(exchange);
            }

            List<String> token = request.getHeaders().get("token");
            String tokenString = Objects.requireNonNull(token).get(0);

            if (!tokenString.equals("A.B.C")){
                return handleUnAuthorized(exchange);
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleUnAuthorized(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
    public static class Config{

    }
}
