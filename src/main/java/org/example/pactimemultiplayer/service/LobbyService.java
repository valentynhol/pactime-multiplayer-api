package org.example.pactimemultiplayer.service;

import jakarta.transaction.Transactional;
import org.example.pactimemultiplayer.config.Constants;
import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.events.LobbyUpdatedEvent;
import org.example.pactimemultiplayer.exception.*;
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
    private final GameFlowService gameFlowService;
    private final ApplicationEventPublisher eventPublisher;

    public List<LobbyDto> getJoinableLobbies() {
        return lobbyRepository.findJoinableLobbies(Constants.LOBBY_MAX_PLAYERS)
                .stream()
                .map(LobbyDto::from)
                .toList();
    }

    public LobbyDto createLobby(CreateLobbyDto dto, Player player) {
        String code = generateUniqueCode();

        Lobby lobby = new Lobby();
        lobby.setCode(code);
        lobby.setHost(player);
        lobby.setName(dto.getName());
        lobby.setStarted(false);

        GameMode mode = gameModeRepository.findByShortName(dto.getGmShortName())
                .orElseThrow(InvalidGameModeException::new);

        lobby.setMode(mode);
        lobby.getPlayers().add(player);
        lobbyRepository.save(lobby);

        return LobbyDto.from(lobby);
    }

    public LobbyDto joinLobby(JoinLobbyDto dto, Player player) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode()).orElseThrow(LobbyNotFoundException::new);

        if (lobby.getPlayers().contains(player)) throw new PlayerIsAlreadyInLobbyException();
        if (lobby.isStarted()) throw new LobbyStartedException();
        if (lobby.getPlayers().size() >= Constants.LOBBY_MAX_PLAYERS) throw new LobbyFullException();

        lobby.getPlayers().add(player);
        lobbyRepository.save(lobby);

        return LobbyDto.from(lobby);
    }

    @Transactional
    public LobbyDto leaveLobby(LeaveLobbyDto dto, Player player) {
        Lobby lobby = removePlayer(dto.getLobbyCode(), player.getId());
        return LobbyDto.from(lobby);
    }

    public void deleteLobby(DeleteLobbyDto dto, Player player) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode()).orElseThrow(LobbyNotFoundException::new);
        if (!player.getId().equals(lobby.getHost().getId())) throw new PlayerIsNotTheHostException();

        lobbyRepository.delete(lobby);
    }

    public LobbyDto startLobby(StartLobbyDto dto, Player player) {
        Lobby lobby = lobbyRepository.findById(dto.getLobbyCode()).orElseThrow(LobbyNotFoundException::new);
        if (!player.getId().equals(lobby.getHost().getId())) throw new PlayerIsNotTheHostException();

        lobby.setStarted(true);
        lobbyRepository.save(lobby);

        gameFlowService.startCountdown(dto);
        return LobbyDto.from(lobby);
    }

    @Transactional
    public Lobby removePlayer(String code, String id) {
        Lobby lobby = lobbyRepository.findWithPlayers(code).orElseThrow(LobbyNotFoundException::new);

        boolean removed = lobby.getPlayers().removeIf(p -> p.getId().equals(id));
        if (!removed) throw new PlayerNotInLobbyException();

        eventPublisher.publishEvent(new LobbyUpdatedEvent(code));
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
