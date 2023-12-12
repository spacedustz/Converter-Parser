package com.rtsp.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtsp.dto.InstanceDto;
import com.rtsp.service.RestApiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckThread extends Thread {
    private final RestApiService restApiService;
    private final TaskExecutor executor;
    private final ObjectMapper mapper;
//    private String[] instanceName = {"1-260-01", "1-260-04", "1-294-01", "1-294-02", "1-414-02", "1-414-03", "1-438-02", "1-465-01", "1-465-04"};
    private String[] instanceName = {"1-260-01"};
    private String server = "http://localhost:8080/";
    private static int index = 0;

    @PostConstruct
    public void init() {
        this.InstanceConnection();
    }

    @Override
    public void run() {
        while (true) {

            Arrays.stream(instanceName).forEach(name -> {
                try {
                    String uri = server + "api/instance/get?instance_name=" + name;
                    String result = restApiService.getInstance(uri).block();
                    InstanceDto[] instanceDtoArray = mapper.readValue(result, InstanceDto[].class);

                    if (instanceDtoArray != null && instanceDtoArray.length > 0) {
                        Arrays.stream(instanceDtoArray).forEach(instance -> {

                            if (instance.getState() == 4) {
                                log.info("Instance 상태 : 실행 중");
                            }

                            log.info("Instance Status - Name : {}, Info : {}", name, instance.toString());

                            if (instance.getState() == 0 || instance.getState() == 1 || instance.getState() == 3 || instance.getState() == 5) {
                                index++;
                                log.debug("총 인스턴스 실패한 횟수 : {}", index);

                                String startUri = server + "api/instance/start";

                                InstanceDto reqBody = new InstanceDto();
                                reqBody.setInstanceName(instance.getInstanceName());
                                reqBody.setSolution(instance.getSolution());

                                try {
                                    String requestBody = mapper.writeValueAsString(reqBody);
                                    restApiService.postInstance(startUri, requestBody).block();

                                    log.info("Instance Start - Request Body : {}", requestBody);
                                } catch (Exception e) {
                                    log.info("Instance Start with An Exception : {}", e.getMessage());
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("Instance Monitoring with An Exception - {}", e.getMessage());
                }
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void InstanceConnection() {
        executor.execute(() -> {
            HealthCheckThread healthCheckThread = new HealthCheckThread(restApiService, executor, mapper);
            healthCheckThread.setDaemon(true);
            executor.execute(healthCheckThread);
        });
    }
}