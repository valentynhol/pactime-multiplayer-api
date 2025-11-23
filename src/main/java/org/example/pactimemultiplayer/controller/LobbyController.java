package org.example.pactimemultiplayer.controller;

import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.service.LobbyService;
import lombok.RequiredArgsConstructor;
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
    public LobbyDto createLobby(@RequestBody CreateLobbyDto dto) {
        return lobbyService.createLobby(dto);
    }

    @DeleteMapping
    public boolean createLobby(@RequestBody DeleteLobbyDto dto) { return lobbyService.deleteLobby(dto); }

    @PostMapping("/join")
    public LobbyDto joinLobby(@RequestBody JoinLobbyDto dto) {
        return lobbyService.joinLobby(dto);
    }

    @PostMapping("/leave")
    public LobbyDto leaveLobby(@RequestBody LeaveLobbyDto dto) {
        return lobbyService.leaveLobby(dto);
    }

    @PostMapping("/{code}/start")
    public LobbyDto startLobby(@PathVariable String code) {
        return lobbyService.startLobby(code);
    }
}
