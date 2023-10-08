package com.rtsp.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class StreamingService {
    static String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
    static String rtspUrl = "rtsp://210.99.70.120:1935/live/cctv001.stream";
    String outputFolderOnLinux = "/root/rtsp"; // hls 폴더로 변경
    String outputFileNameOnLinux = "stream_" + timestamp + ".m3u8"; // 파일 이름에 타임스탬프 포함
    String outputFilePathOnLinux = outputFolderOnLinux + outputFileNameOnLinux;
    String outputFolderOnWindows = "D:/Data/FFmpeg"; // hls 폴더로 변경
    String outputFileNameOnWindows = "stream_" + timestamp + ".m3u8"; // 파일 이름에 타임스탬프 포함
    String outputFilePathOnWindows = outputFolderOnWindows + outputFileNameOnWindows;

    public String convertToHlsOnLinux() {
        try {
            /**
             * -i 옵션 : 입력 스트림 지정
             * -c:v 옵션 : 비디오 코덱 지정
             * -f 옵션 : 출력 형식 지정
             * hls_segment_duration : HLS Segment 길이 지정
             */
            String[] commands = {
                    "ffmpeg", // FFmpeg 환경변수를 등록 안했다면 실행항 FFmpeg 경로 작성
                    "-i", rtspUrl,
                    "-c:v", "copy",
                    "-f", "hls",
                    "-hls_segment_duration", "10",
                    outputFilePathOnLinux
            };

            Process process = Runtime.getRuntime().exec(commands);

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("FFmpeg 실행 실패 : {}", e.getMessage());
            }
        } catch (IOException e) {
            log.error("HLS Convert 실패 : {}", e.getMessage());
        }

        log.info("RTSP 변환 성공");
        return outputFilePathOnLinux;
    }

    public String convertToHlsOnWindows() {
        try {
            /**
             * -i 옵션 : 입력 스트림 지정
             * -c:v 옵션 : 비디오 코덱 지정
             * -f 옵션 : 출력 형식 지정
             * hls_segment_duration : HLS Segment 길이 지정
             */
            String[] commands = {
                    "D:/Tool/ffmpeg/bin/ffmpeg", // FFmpeg 환경변수를 등록 안했다면 실행항 FFmpeg 경로 작성
                    "-i", rtspUrl,
                    "-c:v", "copy",
                    "-f", "hls",
                    "-hls_segment_duration", "10",
                    outputFilePathOnWindows
            };

            Process process = Runtime.getRuntime().exec(commands);

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("FFmpeg 실행 실패 : {}", e.getMessage());
            }
        } catch (IOException e) {
            log.error("HLS Convert 실패 : {}", e.getMessage());
        }

        log.info("RTSP 변환 성공");
        return outputFilePathOnWindows;
    }
}