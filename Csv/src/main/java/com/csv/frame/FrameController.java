package com.csv.frame;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/csv")
public class FrameController {
    private final Parser parser;

    @GetMapping
    public ResponseEntity<List<FrameDTO.Response>> getFrames() {
        return ResponseEntity.ok().body(parser.parseCsv());
    }
}
