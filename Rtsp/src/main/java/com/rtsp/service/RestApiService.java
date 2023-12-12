package com.rtsp.service;

import com.rtsp.dto.StreamingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestApiService {
    private final WebClient webClient;

    private final StreamingService streamingService;

    @Value("${api.key}")
    private String apiKey;

    private String uri = "http://localhost:5002/api/hls/control";


    public Mono<String> getInstance(final String uri) throws Exception{

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }

    /**
     * POST 요청(URI 는 / 부터 시작해야 함)
     *
     * @param uri
     * @param data
     * @return
     */
    public Mono<String> postInstance(final String uri, final Object data) throws Exception{
        return webClient.post()
                .uri(uri)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class);
    }

    public void requestStreaming(String ip, String command) {
        String[] cameras = {
                "1-260-01 8554",
                "1-260-04 8555",
                "1-294-01 8556",
                "1-294-02 8557",
                "1-414-02 8558",
                "1-414-03 8559",
                "1-438-02 8560",
                "1-465-01 8561",
                "1-465-04 8562"
        };

        Mono<List<Void>> request = Flux.range(0, cameras.length)
                .flatMap(i -> {
                    String[] info = cameras[i].split(" ");
                    String instance = info[0];
                    int port = Integer.parseInt(info[1]);

                    StreamingDto dto = new StreamingDto();
                    dto.setCameraId(i + 1);
                    dto.setPort(port);
                    dto.setIp(ip);
                    dto.setApiKey(apiKey);
                    dto.setCommand(command);
                    dto.setInstanceName(instance);

                    return webClient
                            .post()
                            .uri(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(dto))
                            .retrieve()
                            .bodyToMono(Void.class);
                }).collect(Collectors.toList());

        Flux.mergeSequential(request)
                .then()
                .doOnTerminate(() -> {
                    if (command.equals("start")) {
                        log.warn("Request API - FFmpeg 실행 요청 완료");
                    } else if (command.equals("stop")) {
                        log.warn("Request API - FFmpeg 중지 요청 완료");
                    }
                })
                .subscribe();
    }
}
