package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pactimemultiplayer.entity.GameMode;

@Data
@AllArgsConstructor
@Schema(description = "Game mode representation returned by the API")
public class GameModeDto {

    @Schema(description = "Game mode short name", example = "cl")
    private String shortName;

    @Schema(description = "Full game mode name", example = "Classic")
    private String name;

    public static GameModeDto from(GameMode gameMode) {
        return new GameModeDto(gameMode.getShortName(), gameMode.getName());
    }
}
