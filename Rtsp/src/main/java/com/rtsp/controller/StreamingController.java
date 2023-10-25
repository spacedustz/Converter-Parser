package com.rtsp.controller;

import com.rtsp.dto.StreamingDto;
import com.rtsp.service.StreamingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/api/hls")
@RequiredArgsConstructor
public class StreamingController {
    private final StreamingService streamingService;

    @Value("${api.key}")
    private String apiKey;

    // org.springframework.core.io.Resource
    @GetMapping("/stream")
    public ResponseEntity<Resource> streamHls() {
        File file = new File("output.m3u8");
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok(resource);
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
                streamingService.stopHlsConverter(request.getCameraId(), request.getInstanceName());
            }
        } catch (Exception e) {
            log.error("HLS Controller Exception - {}, {}", e.getMessage(), request.toString());
            e.printStackTrace();

            return nok;
        }

        return ok;
    }
}
