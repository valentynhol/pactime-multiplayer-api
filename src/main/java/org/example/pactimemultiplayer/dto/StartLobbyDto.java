package org.example.pactimemultiplayer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartLobbyDto {
    private String code;
    private String gameMap;
}
