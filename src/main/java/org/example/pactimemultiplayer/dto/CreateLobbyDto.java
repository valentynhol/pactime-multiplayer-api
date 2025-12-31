package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for creating a new lobby")
public class CreateLobbyDto {

    @Schema(
            description = "Lobby display name",
            example = "Friday Night Game"
    )
    private String name;

    @Schema(
            description = "Game mode unique identifier",
            example = "cl"
    )
    private String gmShortName;
}
