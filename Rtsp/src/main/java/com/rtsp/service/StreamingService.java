package com.rtsp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StreamingService {
    @Value("${ffmpeg.option.hls.time}")
    private int hlsTime;

    @Value("${ffmpeg.option.hls.list.size}")
    private int hlsListSize;

    @Value("${ffmpeg.option.hls.flags}")
    private String hlsFlags;

    @Value("${ffmpeg.option.start.number}")
    private String startNumber;

    @Value("${hls.file.path}")
    private String hlsFilePath;

    private Map<Integer, Process> processMap = new ConcurrentHashMap();

    private String scriptPath = "script/";

    /**
     * FFmpeg 프로세스 시작
     * @param cameraId
     * @param instanceName
     * @param ip
     * @param port
     * @throws IOException
     * @author 신건우
     */
    public void startConverter(final Integer cameraId, final String instanceName, final String ip, final Integer port) throws IOException {
        Process process = processMap.get(cameraId);

        // FFmpeg에 사용할 명령어를 String Builder를 이용해 작성 합니다.
        if (process == null || !process.isAlive()) {
            StringBuilder builder = new StringBuilder();
            builder.append("ffmpeg -i rtsp://");
            builder.append(ip);
            builder.append(":");
            builder.append(port);
            builder.append("/");
            builder.append(instanceName);
            builder.append(" -c:v copy -c:a copy ");
            builder.append(" -hls_time ").append(hlsTime);
            builder.append(" -hls_list_size ").append(hlsListSize);
            builder.append(" -hls_flags ").append(hlsFlags);
            builder.append(" -start_number ").append(startNumber);
            builder.append(" ").append(hlsFilePath).append(cameraId).append(File.separator).append("output.m3u8");

            // 프로세스를 시작하게 할 명령어 입니다.
            String cmd = builder.toString();

            File file = new File(hlsFilePath + cameraId);

            // 파일이 없으면 생성합니다.
            if (!file.exists()) file.mkdirs();

            // 명령을 전달하여 프로세스 실행합니다.
            process = Runtime.getRuntime().exec(cmd);

            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            int exitCode = 0;

            while ((line = errorStream.readLine()) != null) {
                log.debug(line);
            }

            try {
                exitCode = process.waitFor();
            } catch (Exception e) {
                log.error("Process Wait For Failed - {}", e.getMessage());
            }

            // 카메라의 식별자와 프로세스를 Map에 넣습니다.
            processMap.put(cameraId, process);

            log.info("FFmpeg 변환 프로세스가 시작됩니다. - {}, {}, {}, {}, {}, {}", cameraId, instanceName, ip, port, cmd, process.isAlive());

        } else {
            log.debug("FFmpeg 변환 프로세스가 이미 실행 중 입니다.");
        }
    }

    /**
     * FFmpeg 프로세스 종료
     * @param cameraId
     * @param instanceName
     * @throws IOException
     * @author 신건우
     */
    public void stopHlsConverter(final Integer cameraId, final String instanceName) throws IOException {
        Process process = processMap.get(cameraId);

        // 프로세스가 존재하면 종료합니다.
        if (process != null && process.isAlive()) {
            process.destroy();
            process = null;

            File file = new File(hlsFilePath + cameraId);

            // 파일이 존재하면 제거합니다.
            if (file.exists()) {
                file.delete();
            }

            // Map에서 프로세스를 제거합니다.
            processMap.remove(instanceName);

            log.info("FFmpeg 변환 프로세스가 종료 되었습니다.");
        } else {
            log.info("FFmpeg 변환 프로세스가 실행 중이 아닙니다.");
        }
    }

    /**
     * FFmpeg 프로세스 Health Check
     */
    @Scheduled(fixedDelayString = "${ffmpeg.check.interval.millis}")
    public void checkProcess() {
        processMap.forEach((cameraId, process) -> {
            if (process != null) {
                if (process.isAlive()) {
                    log.debug("Check FFmpeg - {}번 카메라 변환 프로세스가 실행 중 입니다.", cameraId);
                } else {
                    log.debug("Check FFmpeg - {}번 카메라 변환 프로세스가 실행 중이 아닙니다.", cameraId);
                }
            }
        });
    }

    /**
     * FFmpeg 변환을 수행하는 스크립트 실행
     */
    public void startConvert() {
        // ClassPathResource를 사용하여 스크립트 파일을 가져옵니다.
        ClassPathResource resource = new ClassPathResource(scriptPath + "start.sh");

        // InputStream으로 스크립트 파일 내용을 읽습니다.
        try {
            StringBuilder command = new StringBuilder();
            command.append("bash ");
            command.append(resource.getFile().getAbsolutePath());

            // 스크립트 실행 (Linux 및 macOS에서는 /bin/bash를 사용합니다)
            Process process = Runtime.getRuntime().exec(command.toString());

            // 프로세스가 종료될 때까지 대기
            int exitCode = process.waitFor();

            // 실행 결과 출력
            log.info("스크립트 실행 결과: " + exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("스크립트 실행 실패 : {}", e.getMessage());
        }
    }

    /**
     * FFmpeg 변환을 수행하는 스크립트 실행
     */
    public void stopConvert() {
        // ClassPathResource를 사용하여 스크립트 파일을 가져옵니다.
        ClassPathResource resource = new ClassPathResource(scriptPath + "stop.sh");

        // InputStream으로 스크립트 파일 내용을 읽습니다.
        try {
            StringBuilder command = new StringBuilder();
            command.append("bash ");
            command.append(resource.getFile().getAbsolutePath());

            // 스크립트 실행 (Linux 및 macOS에서는 /bin/bash를 사용합니다)
            Process process = Runtime.getRuntime().exec(command.toString());

            // 프로세스가 종료될 때까지 대기
            int exitCode = process.waitFor();

            // 실행 결과 출력
            log.info("스크립트 실행 결과: " + exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("스크립트 실행 실패 : {}", e.getMessage());
        }
    }
}
