package com.rtsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RtspApplication {

    public static void main(String[] args) {
        SpringApplication.run(RtspApplication.class, args);
    }

}
