package org.example.pactimemultiplayer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.ChangeUsernameDto;
import org.example.pactimemultiplayer.dto.PlayerDto;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.security.Authenticated;
import org.example.pactimemultiplayer.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Players", description = "Authenticated player operations")
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {
    private final PlayerService playerService;

    @Operation(
            summary = "Get current player",
            description = "Returns the authenticated player's profile."
    )
    @GetMapping
    PlayerDto getPlayer(@Authenticated Player player) {
        return PlayerDto.from(player);
    }

    @Operation(
            summary = "Update username",
            description = "Updates the authenticated player's username."
    )
    @ApiResponse(responseCode = "204", description = "Username updated")
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updatePlayer(@RequestBody ChangeUsernameDto dto, @Authenticated Player player) {
        playerService.updatePlayerUsername(dto, player);
    }

    @Operation(
            summary = "Delete player account",
            description = "Deletes the authenticated player's account."
    )
    @ApiResponse(responseCode = "204", description = "Player deleted")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePlayer(@Authenticated Player player) {
        playerService.deletePlayer(player);
    }
}
