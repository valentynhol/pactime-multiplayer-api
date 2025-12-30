package org.example.pactimemultiplayer.controller;

import jakarta.validation.Valid;
import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.security.Authenticated;
import org.example.pactimemultiplayer.service.LobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lobbies")
@RequiredArgsConstructor
public class LobbyController {
    private final LobbyService lobbyService;

    @GetMapping
    public List<LobbyDto> getJoinableLobbies() { return lobbyService.getJoinableLobbies(); }

    @PostMapping
    public LobbyDto createLobby(
            @Valid @RequestBody CreateLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.createLobby(dto, player);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLobby(
            @Valid @RequestBody DeleteLobbyDto dto,
            @Authenticated Player player
    ) {
        lobbyService.deleteLobby(dto, player);
    }

    @PostMapping("/join")
    public LobbyDto joinLobby(
            @Valid @RequestBody JoinLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.joinLobby(dto, player);
    }

    @PostMapping("/leave")
    public LobbyDto leaveLobby(
            @Valid @RequestBody LeaveLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.leaveLobby(dto, player);
    }

    @PostMapping("/start")
    public LobbyDto startLobby(
            @Valid @RequestBody StartLobbyDto dto,
            @Authenticated Player player
    ) {
        return lobbyService.startLobby(dto, player);
    }
}
