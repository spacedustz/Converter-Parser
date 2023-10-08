package com.rtsp.streaming;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rtsp")
@RequiredArgsConstructor
public class StreamingController {
    private final StreamingService streamingService;

    @GetMapping("/linux")
    public ResponseEntity<StreamingDto> rtspToHlsOnLinux() {
        return new ResponseEntity<>(StreamingDto.mapping(streamingService.convertToHlsOnLinux()), HttpStatus.OK);
    }

    @GetMapping("/windows")
    public ResponseEntity<StreamingDto> rtspToHlsOnWindows() {
        return new ResponseEntity<>(StreamingDto.mapping(streamingService.convertToHlsOnWindows()), HttpStatus.OK);
    }
}
