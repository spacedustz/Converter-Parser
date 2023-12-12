package com.rtsp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    private Map<String, Process> processMap = new ConcurrentHashMap();

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

        // FFmpeg에 사용할 명령어를 String Builder를 이용해 작성 합니다.
        if (!this.isFfmpegProcesRunning(ip.trim(), port, instanceName.trim())) {
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
            builder.append(" -fflags nobuffer ");
            builder.append(" -strict experimental ");
            builder.append(" -avioflags direct");
            builder.append(" -fflags discardcorrupt ");
            builder.append(" -flags low_delay ");
            builder.append(" -segment_time 0.5 ");
            builder.append(" -segment_wrap 24 ");
            builder.append(" -start_number ").append(startNumber);
            builder.append(" ").append(hlsFilePath).append(cameraId).append(File.separator).append("output.m3u8");

            // 프로세스를 시작하게 할 명령어 입니다.
            String cmd = builder.toString();

            File file = new File(hlsFilePath + cameraId);

            // 파일이 없으면 생성합니다.
            if (!file.exists()) {
                file.mkdirs();
            } else  {
                file.delete();

                try {
                    Thread.sleep(500L);
                } catch (Exception e) {
                    log.warn("FFmpeg Process 실행 Thread Error - {}", e.getMessage());
                }
            }

            // 명령을 전달하여 프로세스 실행합니다.
            Process process = Runtime.getRuntime().exec(cmd);

            // 프로세스 출력 에러 스트림 리다이렉션
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            AtomicInteger exitCode = new AtomicInteger();
            String line;
            AtomicBoolean result = new AtomicBoolean(false);

            while ((line = errorStream.readLine()) != null) {
                log.info(line);
            }

            CompletableFuture.runAsync(() -> {
                try {
                    result.set(process.waitFor(3000L, TimeUnit.MILLISECONDS));
                    log.warn("FFmpeg process exited with exit code: {}", exitCode);
                } catch (InterruptedException e) {
                    log.error("Process Wait For Failed - {}", e.getMessage());
                }
            });

            // 카메라의 식별자와 프로세스를 Map에 넣습니다.
            processMap.put(instanceName, process);

            log.info("FFmpeg 변환 프로세스가 시작됩니다. - {}, {}, {}, {}, {}, {}", cameraId, instanceName, ip, port, cmd, process.isAlive());
//            log.error("Process Map 테스트 : {}", processMap.entrySet().stream().toList().toString());

        } else {
            log.debug("FFmpeg 변환 프로세스가 이미 실행 중 입니다.");
        }
    }

    /**
     * FFmpeg 프로세스 종료
     * @param instanceName
     * @throws IOException
     * @author 신건우
     */
    public void stopHlsConverter(final String instanceName) throws IOException {
        Process process = processMap.get(instanceName);

        if (process != null && process.isAlive()) {
            process.destroy();
            processMap.remove(instanceName);
            log.warn("FFmpeg 변환 프로세스가 종료 되었습니다.");
        } else {
            log.warn("FFmpeg 변환 프로세스가 실행 중이 아닙니다.");
        }
    }

    /**
     * FFmpeg Process 실행 체크
     */
    private boolean isFfmpegProcesRunning(final String ip, final Integer port, final String instanceName) {
        boolean isRunning = false;
        String command = "powershell.exe $currentId = (Get-Process -Id $PID).Id; Get-WmiObject Win32_Process | Where-Object { $_.CommandLine -like '*ffmpeg -i rtsp://" + ip + ":" + port + "/" + instanceName + "*' -and $_.ProcessId -ne $currentId } | Select-Object ProcessId";

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                log.info("FFmpeg Process 실행 중인지 체크 : {}", line);

                if (!line.trim().isEmpty() && line.trim().matches("\\d+")) {
                    log.info("FFmpeg 실행 중 : {}", line);
                    isRunning = true;
                    break;
                }
            }

            boolean result = false;

            try {
                result = process.waitFor(3000L, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("FFmpeg 실행 체크 중 에러 : {}", e.getMessage());
            }

        } catch (Exception e) {
            log.warn("FFmpeg 실행 체크 중 에러 : {}", e.getMessage());
            e.printStackTrace();
        }

        return isRunning;
    }

    /**
     * FFmpeg 프로세스 Health Check
     */
    @Scheduled(fixedDelayString = "${ffmpeg.check.interval.millis}")
    public void checkProcess() {
        if (processMap.isEmpty()) {
            log.warn("Check FFmpeg - 카메라 변환 프로세스가 실행 중이 아닙니다.");
        }

        processMap.forEach((cameraId, process) -> {
            if (process != null) {
                if (process.isAlive()) {
                    log.info("Check FFmpeg - {}번 카메라 변환 프로세스가 실행 중 입니다. - {}", cameraId, process.info().toString());
                } else {
                    log.info("Check FFmpeg - {}번 카메라 변환 프로세스가 실행 중이 아닙니다. - {}", cameraId, process.info().toString());
                }
            }
        });
    }
}
