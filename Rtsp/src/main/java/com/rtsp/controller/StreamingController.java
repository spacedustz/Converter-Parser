package com.rtsp.controller;

import com.rtsp.dto.StreamingDto;
import com.rtsp.service.RestApiService;
import com.rtsp.service.StreamingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/hls")
@RequiredArgsConstructor
public class StreamingController {
    private final RestApiService restApiService;
    private final StreamingService streamingService;

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/request")
    public void startConvert(@RequestParam String ip, @RequestParam String command) {
        restApiService.requestStreaming(ip, command);
    }

    @PostMapping("/control")
    public String controlHls(@Valid @RequestBody StreamingDto request) {
        String ok = "{\"code\": 0, \"msg\": \"Success\"}";
        String nok = "{\"code\": -1, \"msg\": \"Failure\"}";

        try {
            if (!StringUtils.hasText(request.getApiKey()) || !apiKey.trim().equals(request.getApiKey().trim())) {
                log.error("API Key가 잘못 되었습니다. - {}", request);

                return nok;
            }

            // Command가 Start or Stop 일때 FFmpeg 프로세스 실행 / 중지
            if (request.getCommand().equalsIgnoreCase("start")) {
                streamingService.startConverter(request.getCameraId(), request.getInstanceName(), request.getIp(), request.getPort());
            } else if (request.getCommand().equalsIgnoreCase("stop")) {
                streamingService.stopHlsConverter(request.getInstanceName());
            }
        } catch (Exception e) {
            log.error("HLS Controller Exception - {}, {}", e.getMessage(), request.toString());
            e.printStackTrace();

            return nok;
        }

        return ok;
    }
}
