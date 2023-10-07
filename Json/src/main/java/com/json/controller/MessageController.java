package com.json.controller;

import com.json.dto.MessageDto;
import com.json.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parse")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/json")
    public ResponseEntity<List<MessageDto.Response>> getJson() throws Exception {
        return new ResponseEntity<>(messageService.parseJson(), HttpStatus.OK);
    }
}
