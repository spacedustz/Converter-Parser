package com.rtsp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StreamingDto {
    @NotNull
    private Integer cameraId; // DB에 저장된 CameraID

    @NotNull
    private String instanceName; // RTSP Topic

    @NotNull
    private String ip;

    @NotNull
    private Integer port;

    @NotNull
    private String command; // start & stop

    @NotNull
    private String apiKey; // API 호출을 위한 키
}
