package org.example.pactimemultiplayer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pactimemultiplayer.entity.Player;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    private String username;
    private String email;

    public static PlayerDto from(Player player) {
        return new PlayerDto(
                player.getUsername(),
                player.getEmail()
        );
    }
}
