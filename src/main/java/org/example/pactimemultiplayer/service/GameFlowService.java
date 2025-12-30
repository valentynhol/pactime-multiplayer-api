package org.example.pactimemultiplayer.service;

import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.StartLobbyDto;
import org.example.pactimemultiplayer.events.GameStartedEvent;
import org.example.pactimemultiplayer.events.LobbyCountdownEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameFlowService {

    private final ApplicationEventPublisher publisher;

    @Async("lobbyExecutor")
    public void startCountdown(StartLobbyDto dto) {
        try {
            for (int i = 3; i > 0; i--) {
                publisher.publishEvent(new LobbyCountdownEvent(dto.getLobbyCode(), i));
                Thread.sleep(1000);
            }
            publisher.publishEvent(new GameStartedEvent(dto.getLobbyCode(), dto.getGameMap()));
        } catch (InterruptedException ignored) {}
    }
}
