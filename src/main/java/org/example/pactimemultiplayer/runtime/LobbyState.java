package org.example.pactimemultiplayer.runtime;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LobbyState {

    private Long lobbyId;
    private String mapJson;
    private Map<Long, PlayerState> players = new HashMap<>();
    private boolean started;
    private int maxPlayers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlayerState {
        private Long playerId;
        private String nickname;
        private int score;
        private int posX;
        private int posY;
    }
}
