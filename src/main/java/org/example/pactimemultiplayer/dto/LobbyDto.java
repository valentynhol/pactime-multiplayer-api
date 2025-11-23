package org.example.pactimemultiplayer.dto;

import org.example.pactimemultiplayer.entity.Lobby;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pactimemultiplayer.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class LobbyDto {
    private String code;
    private String name;
    private boolean started;
    private String mode;
    private Set<String> players;

    public static LobbyDto from(Lobby lobby) {
        return new LobbyDto(
                lobby.getCode(),
                lobby.getName(),
                lobby.isStarted(),
                lobby.getMode() != null ? lobby.getMode().getName() : null,
                lobby.getPlayers().stream().map(Player::getUsername).collect(Collectors.toSet())
        );
    }
}
