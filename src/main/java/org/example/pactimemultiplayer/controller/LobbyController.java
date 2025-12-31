package org.example.pactimemultiplayer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.security.Authenticated;
import org.example.pactimemultiplayer.service.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Lobbies", description = "Lobby management endpoints")
@RestController
@RequestMapping("/lobbies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LobbyController {
    private final LobbyService lobbyService;

    @Operation(
            summary = "Get joinable lobbies",
            description = "Returns list of lobbies that are not started and not full."
    )
    @GetMapping
    public List<LobbyDto> getJoinableLobbies() { return lobbyService.getJoinableLobbies(); }

    @Operation(
            summary = "Create lobby",
            description = "Creates a new lobby. The authenticated player becomes the host."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lobby created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public LobbyDto createLobby(
            @Valid @RequestBody CreateLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.createLobby(dto, player);
    }

    @Operation(
            summary = "Delete lobby",
            description = "Deletes a lobby. Only the host may delete it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lobby deleted"),
            @ApiResponse(responseCode = "403", description = "Not lobby host"),
            @ApiResponse(responseCode = "404", description = "Lobby not found")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLobby(
            @Valid @RequestBody DeleteLobbyDto dto,
            @Authenticated Player player
    ) {
        lobbyService.deleteLobby(dto, player);
    }

    @Operation(
            summary = "Join lobby",
            description = "Adds authenticated player to a lobby if possible."
    )
    @PostMapping("/join")
    public LobbyDto joinLobby(
            @Valid @RequestBody JoinLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.joinLobby(dto, player);
    }

    @Operation(
            summary = "Leave lobby",
            description = "Removes authenticated player from lobby."
    )
    @PostMapping("/leave")
    public LobbyDto leaveLobby(
            @Valid @RequestBody LeaveLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.leaveLobby(dto, player);
    }

    @Operation(
            summary = "Start lobby",
            description = "Starts the lobby and begins game countdown. Only host allowed."
    )
    @PostMapping("/start")
    public LobbyDto startLobby(
            @Valid @RequestBody StartLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.startLobby(dto, player);
    }
}
