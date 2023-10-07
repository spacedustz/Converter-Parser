package com.json.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vertice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vertice_id")
    private Long id;
    private Double x;
    private Double y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    private Vertice(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public static Vertice createOf(Double x, Double y) {
        return new Vertice(x, y);
    }

    public void setMessage(Message message) {
        this.message = message;
        if (message != null) {
            message.getVertices().add(this);
        }
    }
}
