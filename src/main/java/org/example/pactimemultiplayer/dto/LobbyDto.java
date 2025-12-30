package org.example.pactimemultiplayer.dto;

import org.example.pactimemultiplayer.entity.Lobby;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pactimemultiplayer.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class LobbyDto {
    private String code;
    private String name;
    private String host;
    private boolean started;
    private String mode;
    private String modeShort;
    private Map<String, String> players;

    public static LobbyDto from(Lobby lobby) {
        return new LobbyDto(
                lobby.getCode(),
                lobby.getName(),
                lobby.getHost().getId(),
                lobby.isStarted(),
                lobby.getMode() != null ? lobby.getMode().getName() : null,
                lobby.getMode() != null ? lobby.getMode().getShortName() : null,
                lobby.getPlayers().stream().collect(Collectors.toMap(
                        Player::getId,
                        Player::getUsername
                ))
        );
    }
}
