package com.bolsadeideas.springboot.webflux.spring_boot_webflux_client.models.services;

import com.bolsadeideas.springboot.webflux.spring_boot_webflux_client.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductoServiceImpl implements ProductoService {
    
    @Autowired
    private WebClient.Builder client;
    
    @Override
    public Flux<Producto> findAll() {
        return client.build().get().accept(MediaType.APPLICATION_JSON)
            .exchange()
            .flatMapMany(response -> response.bodyToFlux(Producto.class));
    }
    
    @Override
    public Mono<Producto> findById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return client.build().get().uri("/{id}", params)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Producto.class);
//                .exchange()
//                .flatMap(response -> response.bodyToMono(Producto.class));
    }
    
    @Override
    public Mono<Producto> save(Producto producto) {
        return client.build().post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .syncBody(producto)
            .retrieve()
            .bodyToMono(Producto.class);
    }
    
    @Override
    public Mono<Producto> update(Producto producto, String id) {
        return client.build().put()
            .uri("/{id}", Collections.singletonMap("id", id))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .syncBody(producto)
            .retrieve()
            .bodyToMono(Producto.class);
    }
    
    @Override
    public Mono<Void> delete(String id) {
        return client.build().delete()
            .uri("/{id}", Collections.singletonMap("id", id))
            .exchange()
            .then();
    }
    
    @Override
    public Mono<Producto> upload(FilePart file, String id) {
        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.asyncPart("file", file.content(), DataBuffer.class).headers(h -> {
                h.setContentDispositionFormData("file", file.filename());
            });
        
        return client.build().post()
            .uri("/upload/{id}", Collections.singletonMap("id", id))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .syncBody(parts.build())
            .retrieve()
            .bodyToMono(Producto.class);
    }
}
