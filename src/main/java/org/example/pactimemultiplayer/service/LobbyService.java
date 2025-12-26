package org.example.pactimemultiplayer.service;

import jakarta.transaction.Transactional;
import org.example.pactimemultiplayer.config.Constants;
import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.events.LobbyUpdatedEvent;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import org.example.pactimemultiplayer.repository.GameModeRepository;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.util.CodeGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final GameModeRepository gameModeRepository;
    private final PlayerService playerService;
    private final GameFlowService gameFlowService;
    private final ApplicationEventPublisher eventPublisher;

    public List<LobbyDto> getJoinableLobbies() {
        return lobbyRepository.findJoinableLobbies(Constants.LOBBY_MAX_PLAYERS)
                .stream()
                .map(LobbyDto::from)
                .toList();
    }

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

        return LobbyDto.from(lobby);
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

        Player player = playerService.findOrCreate(dto.getUsername());
        lobby.getPlayers().add(player);
        lobbyRepository.save(lobby);

        return LobbyDto.from(lobby);
    }

    @Transactional
    public LobbyDto leaveLobby(LeaveLobbyDto dto) {
        Lobby lobby = removePlayer(dto.getLobbyCode(), dto.getUsername());
        return LobbyDto.from(lobby);
    }

    public boolean deleteLobby(DeleteLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode())
                .orElseThrow(() -> new RuntimeException("Lobby not found"));
        lobbyRepository.delete(lobby);

        return true;
    }

    public LobbyDto startLobby(StartLobbyDto dto) {
        Lobby lobby = lobbyRepository.findById(dto.getCode()).orElseThrow();
        lobby.setStarted(true);
        lobbyRepository.save(lobby);

        gameFlowService.startCountdown(dto);
        return LobbyDto.from(lobby);
    }

    @Transactional
    public Lobby removePlayer(String code, String username) {
        Lobby lobby = lobbyRepository.findWithPlayers(code)
                .orElseThrow(() -> new RuntimeException("Lobby not found"));

        boolean removed = lobby.getPlayers()
                .removeIf(p -> p.getUsername().equals(username));

        if (removed) {
            eventPublisher.publishEvent(new LobbyUpdatedEvent(code));
        }

        return lobby;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = CodeGenerator.generate(Constants.LOBBY_CODE_LENGTH);
        } while (lobbyRepository.existsById(code));
        return code;
    }
}
