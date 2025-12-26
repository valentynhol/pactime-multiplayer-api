package org.example.pactimemultiplayer.events;

import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.websocket.LobbyWebSocketHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LobbyEventListener {
    private final LobbyWebSocketHandler ws;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLobbyUpdated(LobbyUpdatedEvent event) {
        ws.broadcastLobbyUpdate(event.code());
    }

    @EventListener
    public void onGameStarted(GameStartedEvent event) {
        ws.broadcastGameStart(event.code(), event.gameMap());
    }

    @EventListener
    public void onCountdown(LobbyCountdownEvent event) {
        ws.broadcastCountdown(event.code(), event.num());
    }
}
