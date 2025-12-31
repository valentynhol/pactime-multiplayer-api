package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for deleting a lobby")
public class DeleteLobbyDto {

    @Schema(
            description = "Unique lobby code",
            example = "ABCD12"
    )
    private String lobbyCode;
}
