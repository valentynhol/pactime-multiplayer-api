package org.example.pactimemultiplayer.service;

import org.example.pactimemultiplayer.config.Constants;
import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import org.example.pactimemultiplayer.repository.PlayerRepository;
import org.example.pactimemultiplayer.repository.GameModeRepository;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.util.CodeGenerator;
import org.example.pactimemultiplayer.websocket.LobbyWebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final GameModeRepository gameModeRepository;
    private final PlayerService playerService;
    private final LobbyWebSocketHandler lobbyWebSocketService;

    public LobbyDto createLobby(CreateLobbyDto dto) {
        String code = generateUniqueCode();
        Player host = playerService.findOrCreate(dto.getHostUsername());

        Lobby lobby = new Lobby();
        lobby.setCode(code);
        lobby.setHost(host);
        lobby.setName(dto.getName());
        lobby.setStarted(false);

        GameMode mode = gameModeRepository.findByShortName(dto.getGmShortName())
                .orElseThrow(() -> new RuntimeException("Game mode not found"));

        lobby.setMode(mode);

        lobby.getPlayers().add(host);

        lobbyRepository.save(lobby);

        lobbyWebSocketService.broadcastLobbyUpdate(code);

        return LobbyDto.from(lobby);
    }

    public boolean deleteLobby(DeleteLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode())
                .orElseThrow(() -> new RuntimeException("Lobby not found"));
        lobbyRepository.delete(lobby);

        return true;
    }

    public LobbyDto joinLobby(JoinLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode())
                .orElseThrow(() -> new RuntimeException("Lobby not found"));

        if (lobby.isStarted()) {
            throw new RuntimeException("Lobby already started");
        }
        if (lobby.getPlayers().size() >= Constants.LOBBY_MAX_PLAYERS) {
            throw new RuntimeException("Lobby is full");
        }

        Player player =  playerService.findOrCreate(dto.getUsername());

        lobby.getPlayers().add(player);

        lobbyRepository.save(lobby);

        lobbyWebSocketService.broadcastLobbyUpdate(lobby.getCode());

        return LobbyDto.from(lobby);
    }

    public LobbyDto leaveLobby(LeaveLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode())
                .orElseThrow(() -> new RuntimeException("Lobby not found"));

        if (lobby.isStarted()) {
            throw new RuntimeException("Lobby already started");
        }
        if (lobby.getPlayers().size() >= Constants.LOBBY_MAX_PLAYERS) {
            throw new RuntimeException("Lobby is full");
        }

        Optional<Player> player = playerRepository.findByUsername(dto.getUsername());
        player.ifPresent(value -> lobby.getPlayers().remove(value));

        lobbyRepository.save(lobby);

        lobbyWebSocketService.broadcastLobbyUpdate(lobby.getCode());

        return LobbyDto.from(lobby);
    }

    public LobbyDto startLobby(StartLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getCode()).orElseThrow();
        lobby.setStarted(true);
        lobbyRepository.save(lobby);

        System.out.println("Debug: got map: " + dto.getGameMap());

        lobbyWebSocketService.runCountdown(dto);

        return LobbyDto.from(lobby);
    }

    public List<LobbyDto> getJoinableLobbies() {
        return lobbyRepository.findJoinableLobbies(Constants.LOBBY_MAX_PLAYERS)
                .stream()
                .map(LobbyDto::from)
                .toList();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = CodeGenerator.generate(Constants.LOBBY_CODE_LENGTH);
        } while (lobbyRepository.existsById(code));
        return code;
    }
}
