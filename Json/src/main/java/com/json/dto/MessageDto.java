package com.json.dto;

import com.json.entity.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MessageDto {
    private Long id;
    private Double frameId;
    private Double bboxHeight;
    private Double bboxWidth;
    private Double bboxX;
    private Double bboxY;
    private List<String> vertices;

    // Response DTO
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private Long id;
        private Double frameId;
        private Double bboxHeight;
        private Double bboxWidth;
        private Double bboxX;
        private Double bboxY;
        private List<String> vertices;

        public static MessageDto.Response fromEntity(Message entity) {
            return new MessageDto.Response(
                    entity.getId(),
                    entity.getFrameId(),
                    entity.getBboxHeight(),
                    entity.getBboxWidth(),
                    entity.getBboxX(),
                    entity.getBboxY(),
                    entity.getVertices().stream()
                            .map(it -> "x: " + it.getX() + "y: " + it.getY())
                            .collect(Collectors.toList())
            );
        }
    }
}
