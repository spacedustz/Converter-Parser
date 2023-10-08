package com.rtsp.streaming;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamingDto {
    private String url;

    public static StreamingDto mapping(String url) {
        return new StreamingDto(url);
    }
}
