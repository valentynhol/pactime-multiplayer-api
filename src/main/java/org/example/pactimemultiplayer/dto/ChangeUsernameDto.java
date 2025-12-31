package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payload for changing the player's username")
public class ChangeUsernameDto {

    @Schema(
            description = "New username for the player",
            example = "player123"
    )
    private String username;
}
