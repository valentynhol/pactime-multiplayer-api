package org.example.pactimemultiplayer.controller;

import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.PlayerDto;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.security.Authenticated;
import org.example.pactimemultiplayer.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    PlayerDto getPlayer(@Authenticated Player player) {
        return PlayerDto.from(player);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updatePlayer(@RequestBody PlayerDto dto, @Authenticated Player player) {
        playerService.updatePlayer(dto, player);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePlayer(@Authenticated Player player) {
        playerService.deletePlayer(player);
    }
}
