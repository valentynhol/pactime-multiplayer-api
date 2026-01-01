package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Payload for creating a new game mode")
public class CreateGameModeDto {

    @Schema(description = "Game mode short name", example = "cl")
    private String shortName;

    @Schema(description = "Full game mode name", example = "Classic")
    private String name;
}
