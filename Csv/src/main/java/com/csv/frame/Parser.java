package com.csv.frame;

import com.csv.error.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class Parser {
    private final FrameRepository frameRepository;

    /**
     * 변환, 리스트 저장 실패 시 트랜잭션 롤백
     */
    @Transactional
    public void parseCsv() {
        // 임시로 로컬에서 CSV를 읽어옴
        Resource resource = new ClassPathResource("sample/test.csv");

        try {
            List<String> lines = Files.readAllLines(Paths.get(resource.getFile().getPath()), StandardCharsets.UTF_8);
            List<Frame> list = new ArrayList<>();

            // CSV의 첫 행은 헤더이기 때문에 0번쨰 인덱스 스킵
            for (int i=1; i<lines.size(); i++) {
                String[] split = lines.get(i).split(",");

                // CSV 파일의 값중 String이 아닌 값들의 타입 변환 준비
                int count;
                float frameTime;
                long systemTimestamp;
                LocalDateTime systemDate;
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                String dateString = split[4];

                try {
                    // Count 필드 변환
                    count = Integer.parseInt(split[0]);

                    // Frame Time 필드 변환
                    Float frameValue = Float.parseFloat(split[2]);
                    frameTime = Float.parseFloat((String.format("%.4f", frameValue))); // 소수점 4자리 까지만

                    // System TimeStamp 필드 변환
                    systemTimestamp = Long.parseLong(split[5]);

                    // System Date 날짜 필드 변환
                    systemDate = LocalDateTime.parse(dateString, dateFormat);
                } catch (Exception e) {
                    log.error("CSV 데이터 변환 실패");
                    throw new CommonException("DATA-003", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Entity 생성하면서 변환된 데이터를 넣어줍니다.
                Frame frame = Frame.createOf(
                        count,
                        frameTime,
                        split[3],
                        systemDate,
                        systemTimestamp
                );

                list.add(frame);
            }

            // Repository에 Entity 전부 추가
            try {
                frameRepository.saveAll(list);
                log.info("========== 데이터 저장 성공 ==========");
            } catch (Exception e) {
                log.error("Entity List 저장 실패");
                throw new CommonException("DATA-002", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            log.error("===== 데이터 파싱 실패 =====");
            throw new CommonException("DATA-001", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
