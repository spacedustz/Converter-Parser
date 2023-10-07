package com.json.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    private Double frameId;
    private Double bboxHeight;
    private Double bboxWidth;
    private Double bboxX;
    private Double bboxY;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vertice> vertices = new ArrayList<>();

    private Message(Double frameId, Double bboxHeight, Double bboxWidth, Double bboxX, Double bboxY) {
        this.frameId = frameId;
        this.bboxHeight = bboxHeight;
        this.bboxWidth = bboxWidth;
        this.bboxX = bboxX;
        this.bboxY = bboxY;
    }

    public static Message createOf(Double frameId, Double bboxHeight, Double bboxWidth, Double bboxX, Double bboxY) {
        return new Message(frameId, bboxHeight, bboxWidth, bboxX, bboxY);
    }
}
