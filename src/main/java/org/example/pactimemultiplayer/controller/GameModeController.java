package org.example.pactimemultiplayer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.CreateGameModeDto;
import org.example.pactimemultiplayer.dto.DeleteGameModeDto;
import org.example.pactimemultiplayer.dto.GameModeDto;
import org.example.pactimemultiplayer.service.GameModeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Game modes", description = "Game mode endpoints")
@RestController
@RequestMapping("/game-modes")
@RequiredArgsConstructor
public class GameModeController {

    private final GameModeService gameModeService;

    @Operation(
            summary = "Get allowed game modes",
            description = "Returns list of game modes that are allowed for multiplayer."
    )
    @GetMapping
    public List<GameModeDto> getAllowedGameModes() {
        return gameModeService.getGameModes();
    }

    // @PostMapping
    public GameModeDto createGameMode(@Valid @RequestBody CreateGameModeDto dto) {
        return gameModeService.createGameMode(dto);
    }

    // @DeleteMapping
    public void deleteGameMode(@Valid @RequestBody DeleteGameModeDto dto) {
        gameModeService.deleteGameMode(dto);
    }
}
