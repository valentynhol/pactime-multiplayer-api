package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for starting a lobby")
public class StartLobbyDto {

    @Schema(
            description = "Lobby code",
            example = "ABCD12"
    )
    private String lobbyCode;

    @Schema(
            description = "Selected game map JSON (stringified)",
            type = "string",
            format = "json",
            example = """
            {
              "name": "Test1",
              "gameModes": ["cl", "tr", "oc"],
              "maxGameDuration": 30,
              "gameMap": [
                ["#", "#", "#", "#", "#"],
                [".", "p", "#", ".", "."],
                [".", ".", "#", ".", "."],
                [".", ".", "#", ".", "."],
                [".", ".", "#", ".", "."]
              ]
            }
            """
    )
    private String gameMap;
}
