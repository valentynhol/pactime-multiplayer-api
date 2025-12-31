package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for leaving a lobby")
public class LeaveLobbyDto {

    @Schema(
            description = "Lobby code to leave",
            example = "ABCD12"
    )
    String lobbyCode;
}