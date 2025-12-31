package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pactimemultiplayer.entity.Player;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authenticated player profile")
public class PlayerDto {

    @Schema(description = "Player unique ID", example = "117036227984588628599")
    private String id;

    @Schema(description = "Player username", example = "player123")
    private String username;

    @Schema(description = "Player email address", example = "john@gmail.com")
    private String email;

    public static PlayerDto from(Player player) {
        return new PlayerDto(
                player.getId(),
                player.getUsername(),
                player.getEmail()
        );
    }
}
