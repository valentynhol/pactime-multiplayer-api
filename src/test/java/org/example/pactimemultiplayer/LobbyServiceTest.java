package org.example.pactimemultiplayer;

import org.example.pactimemultiplayer.dto.*;
import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.exception.InvalidGameModeException;
import org.example.pactimemultiplayer.exception.PlayerIsAlreadyInLobbyException;
import org.example.pactimemultiplayer.exception.PlayerIsNotTheHostException;
import org.example.pactimemultiplayer.repository.GameModeRepository;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import org.example.pactimemultiplayer.service.GameFlowService;
import org.example.pactimemultiplayer.service.LobbyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    @Mock
    LobbyRepository lobbyRepository;

    @Mock
    GameModeRepository gameModeRepository;

    @Mock
    GameFlowService gameFlowService;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    LobbyService lobbyService;

    Player host;
    GameMode mode;

    @BeforeEach
    void setup() {
        host = new Player("p1", "user1", "u1@mail.com");
        mode = new GameMode("Classic", "cl");
    }

    @Test
    void createLobby_success() {
        CreateLobbyDto dto = new CreateLobbyDto("Lobby", "cl");

        when(gameModeRepository.findByShortName("cl"))
                .thenReturn(Optional.of(mode));
        when(lobbyRepository.existsById(any())).thenReturn(false);

        LobbyDto result = lobbyService.createLobby(dto, host);

        assertThat(result.getHost()).isEqualTo("p1");
        verify(lobbyRepository).save(any(Lobby.class));
    }

    @Test
    void createLobby_invalidGameMode_throws() {
        when(gameModeRepository.findByShortName("bad"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                lobbyService.createLobby(
                        new CreateLobbyDto("Lobby", "bad"), host
                )
        ).isInstanceOf(InvalidGameModeException.class);
    }

    @Test
    void joinLobby_whenAlreadyJoined_throws() {
        Lobby lobby = new Lobby();
        lobby.getPlayers().add(host);

        when(lobbyRepository.findById("ABC"))
                .thenReturn(Optional.of(lobby));

        assertThatThrownBy(() ->
                lobbyService.joinLobby(new JoinLobbyDto("ABC"), host)
        ).isInstanceOf(PlayerIsAlreadyInLobbyException.class);
    }

    @Test
    void deleteLobby_notHost_throws() {
        Player other = new Player("p2", "user2", "x@mail.com");
        Lobby lobby = new Lobby("ABC", host, "Lobby", false, Set.of(host), null);

        when(lobbyRepository.findById("ABC"))
                .thenReturn(Optional.of(lobby));

        assertThatThrownBy(() ->
                lobbyService.deleteLobby(new DeleteLobbyDto("ABC"), other)
        ).isInstanceOf(PlayerIsNotTheHostException.class);
    }

    @Test
    void startLobby_success_triggersCountdown() {
        Lobby lobby = new Lobby("ABC", host, "Lobby", false, Set.of(host), null);

        when(lobbyRepository.findById("ABC"))
                .thenReturn(Optional.of(lobby));

        lobbyService.startLobby(new StartLobbyDto("ABC", "{}"), host);

        verify(gameFlowService).startCountdown(any());
        verify(lobbyRepository).save(lobby);
    }
}
