package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for deleting a game mode")
public class DeleteGameModeDto {

    @Schema(description = "Game mode short name", example = "cl")
    private String shortName;
}