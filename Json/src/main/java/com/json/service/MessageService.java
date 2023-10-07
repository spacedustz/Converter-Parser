package com.json.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.dto.MessageDto;
import com.json.entity.Message;
import com.json.entity.Vertice;
import com.json.error.CommonException;
import com.json.repository.MessageRepository;
import com.json.repository.VerticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final VerticeRepository verticeRepository;

    public List<MessageDto.Response> parseJson() throws Exception {
        List<JsonNode> jsonList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

//        String jsonContent = new String(Files.readAllBytes(Paths.get("C:\\Users\\root\\Desktop\\Tools\\Cvedia\\files\\output\\eventsExport.json")));

        // 프로젝트 내부 Sample 디렉터리의 샘플 Json 파일을 가져옵니다.
        ClassPathResource resource = new ClassPathResource("sample/test.json");

        // 파일에서 Json 데이터를 읽어 들여 String 값으로 변환합니다.
        String jsonContent = Files.readString(Paths.get(resource.getURI()));

        // 샘플 파일의 Json 객체들은 개행으로 구분되어 있어서 개행을 기준으로 객체들을 String 배열에 담습니다.
        String[] splitJson = jsonContent.split("\n");

        // 객체 분리가 되어 String 배열에 담긴 데이터들을 JsonNode 타입의 리스트에 담습니다.
        for (String json : splitJson) {
            JsonNode jsonObject = objectMapper.readTree(json);
            jsonList.add(jsonObject);
        }

        /* Json 배열을 돌기 전, 필요한 변수들을 선언해 줍니다. */
        Double frameId = null, bboxHeight = null, bboxWidth = null, bboxX = null, bboxY = null;
        List<Message> messageList = new ArrayList<>();

        // JsonNode List를 돌며 Json 객체들 1개씩 돌며, 필요한 필드를 추출해 Entity의 필드에 넣고 저장합니다.
        for (JsonNode node : jsonList) {

            // Json의 구조 중 최상단에 위치한 frame_id를 변수에 넣어줍니다.
            frameId = node.get("frame_id").asDouble();

            // 최상단의 바로 밑은 event 배열이 있습니다. event 배열을 순회합니다.
            JsonNode eventArray = node.get("events");

            if (eventArray != null && eventArray.isArray()) {
                for (JsonNode event : eventArray) {

                    // event -> extra -> bbox의 4개 값들을 변수에 넣어줍니다.
                    JsonNode bbox = event.get("extra").get("bbox");
                    if (bbox != null) {
                        bboxHeight = bbox.get("height").asDouble();
                        bboxWidth = bbox.get("width").asDouble();
                        bboxX = bbox.get("x").asDouble();
                        bboxY = bbox.get("y").asDouble();
                    }
                }
            }

            // 위에서 얻은 값들로 엔티티 생성
            Message message = Message.createOf(
                    frameId,
                    bboxHeight,
                    bboxWidth,
                    bboxX,
                    bboxY
            );

            // Vertice리스트를 verticeList 변수에 넣습니다.
            List<Vertice> vertices = getVertices(eventArray, message);

            messageList.add(message);

            // 메시지 저장
            try {
                messageRepository.save(message);
            } catch (Exception e) {
                log.error("Message 객체 저장 실패 - {}", e.getMessage());
                throw new CommonException("Message-001", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Vertice 저장
            try {
                verticeRepository.saveAll(vertices);
            } catch (Exception e) {
                log.error("Message 객체 저장 실패 - {}", e.getMessage());
                throw new CommonException("Message-001", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 변수 초기화
            frameId = null;
            bboxHeight = null;
            bboxWidth = null;
            bboxX = null;
            bboxY = null;
        }

        log.info("Json 객체 데이터 파싱 완료");
        return messageList.stream().map(MessageDto.Response::fromEntity).collect(Collectors.toList());
    }

    // Json 데이터 값 중 events 배열을 받아 event를 돌면서 Vertice 객체들을 빼냅니다.
    public List<Vertice> getVertices(JsonNode eventArray, Message message) {
        List<Vertice> verticeList = new ArrayList<>();

        if (eventArray != null && eventArray.isArray()) {
            for (JsonNode event : eventArray) {
                // Vertices 를 구합니다.
                JsonNode vertices = event.get("extra").get("tripwire").get("vertices");

                if (vertices != null && vertices.isArray() && vertices.size() > 0) {
                    for (JsonNode vertice : vertices) {
                        // Vertice 엔티티를 생성해 x,y 값을 넣어줍니다.
                        Vertice verticeEntity = Vertice.createOf(
                                vertice.get("x").asDouble(),
                                vertice.get("y").asDouble()
                        );
                        verticeEntity.setMessage(message);

                        // Vertice 리스트에 객체를 추가합니다.
                        verticeList.add(verticeEntity);
                    }
                }
            }
        }

        return verticeList;
    }
}

