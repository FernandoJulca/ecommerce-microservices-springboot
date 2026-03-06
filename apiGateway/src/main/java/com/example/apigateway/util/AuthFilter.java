package com.example.apigateway.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

	@Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
    		"/auth-service/auth/register",
    	    "/auth-service/auth/login"
        );

    @Override
    public int getOrder() {
        return 1; 
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    	ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        System.out.println("=================================================");
        System.out.println("GATEWAY RECIBIÓ PETICIÓN");
        System.out.println("Path: " + path);
        System.out.println("Método: " + method);
        System.out.println("Origin: " + request.getHeaders().getFirst("Origin"));
        System.out.println("=================================================");
        
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        boolean isPublic = isPublicPath(path);
        System.out.println(">>> isPublic resultado: " + isPublic);  // ← log clave

        if (isPublic) {
            System.out.println(">>> Dejando pasar ruta pública");
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "No autorizado: falta token", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);

        if (token.isBlank()) {
            System.out.println("Token vacío");
            return onError(exchange, "Token JWT no proporcionado", HttpStatus.UNAUTHORIZED);
        }

        try {
            if (!jwtUtil.validarToken(token)) {
                System.out.println("Token inválido");
                return onError(exchange, "Token JWT inválido o expirado", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return onError(exchange, "Token JWT inválido o mal formado", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.obtenerUsuarioAndToken(token);
        System.out.println("Token válido para usuario: " + username);
        
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header("X-Username", username)
            .build();

        System.out.println("Reenviando petición con header X-Username: " + username);

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPublicPath(String path) {
        System.out.println("Path bytes: " + Arrays.toString(path.getBytes()));
        System.out.println("Expected bytes: " + Arrays.toString("/auth-service/auth/register".getBytes()));
        System.out.println("¿Son iguales? " + path.equals("/auth-service/auth/register"));
        
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String errorJson = String.format("{\"error\": \"%s\"}", message);
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorJson.getBytes()))
        );
    }
}
