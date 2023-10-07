package com.csv.frame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class FrameDTO {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private Long frameId;
        private int count;
        private Float frameTime;
        private String instanceId;
        private LocalDateTime systemDate;
        private Long systemTimestamp;

        public static Response createOf(
                Long id,
                int count,
                Float frameTime,
                String instanceId,
                LocalDateTime systemDate,
                Long systemTimestamp) {
            return new Response(id, count, frameTime, instanceId, systemDate, systemTimestamp);
        }

        public static Response fromEntity(Frame entity) {
            return Response.createOf(
                    entity.getId(),
                    entity.getCount(),
                    entity.getFrameTime(),
                    entity.getInstanceId(),
                    entity.getSystemDate(),
                    entity.getSystemTimestamp()
            );
        }
    }
}
