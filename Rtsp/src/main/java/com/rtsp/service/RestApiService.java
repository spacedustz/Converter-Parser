package com.rtsp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestApiService {
    private final WebClient webClient;

    public Mono<String> getRequest(final String uri) throws Exception{

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * POST 요청(URI 는 / 부터 시작해야 함)
     *
     * @param serverId
     * @param uri
     * @param data
     * @return
     */
    public Mono<String> postRequest(final String uri, final Object data) throws Exception{
        return webClient.post()
                .uri(uri)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class);
    }
}
