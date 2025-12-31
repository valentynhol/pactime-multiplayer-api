package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for joining a lobby")
public class JoinLobbyDto {

    @Schema(
            description = "Lobby code to join",
            example = "ABCD12"
    )
    String lobbyCode;
}
