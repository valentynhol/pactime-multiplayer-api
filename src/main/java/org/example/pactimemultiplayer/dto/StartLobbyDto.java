package org.example.pactimemultiplayer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartLobbyDto {
    private String lobbyCode;
    private String gameMap;
}
