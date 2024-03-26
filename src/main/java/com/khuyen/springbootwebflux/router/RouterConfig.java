package com.khuyen.springbootwebflux.router;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;


@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
                .GET("/", request -> indexHtml())
                .build();
    }

    @SuppressWarnings("null")
    private Mono<ServerResponse> indexHtml() {
        Resource resource = new ClassPathResource("static/index.html");
        try {
            String content = Files.lines(resource.getFile().toPath()).collect(Collectors.joining("\n"));
            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(BodyInserters.fromValue(content));
        } catch (IOException e) {
            return ServerResponse.notFound().build();
        }
    }
}