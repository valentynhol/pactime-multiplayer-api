package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.pactimemultiplayer.entity.Lobby;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pactimemultiplayer.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Schema(description = "Lobby representation returned by the API")
public class LobbyDto {

    @Schema(description = "Lobby unique code", example = "ABCD12")
    private String code;

    @Schema(description = "Lobby name", example = "Friday Night Game")
    private String name;

    @Schema(description = "Host player ID", example = "117036227984588628599")
    private String host;

    @Schema(description = "Whether the lobby has started")
    private boolean started;

    @Schema(description = "Full game mode name", example = "Classic")
    private String mode;

    @Schema(description = "Game mode short name", example = "cl")
    private String modeShort;

    @Schema(
            description = "Map of playerId → username",
            example = """
                    {
                      "117036227984588628599": "John",
                      "217036227984588628500": "Alex"
                    }
                    """
    )
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
