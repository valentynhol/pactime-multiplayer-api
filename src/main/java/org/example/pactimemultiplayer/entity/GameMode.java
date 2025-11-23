package org.example.pactimemultiplayer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class GameMode {
    @Id
    private String shortName;

    private String name;

    public GameMode(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }
}
