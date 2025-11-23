package org.example.pactimemultiplayer.websocket;

import org.example.pactimemultiplayer.dto.LobbyDto;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LobbyWebSocketHandler extends TextWebSocketHandler {
    private final LobbyRepository lobbyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String code = path.substring(path.lastIndexOf('/') + 1);
        System.out.println("Debug: opening ws with code " + code);

        session.getAttributes().put("code", code);

        sessions.computeIfAbsent(code, k -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String code = getLobbyCode(session);
        System.out.println("Debug: closing ws with code " + code);
        sessions.getOrDefault(code, Set.of()).remove(session);
    }

    public void broadcastLobbyUpdate(String code) {
        Lobby lobby = lobbyRepository.findById(code).orElseThrow();
        LobbyDto dto = LobbyDto.from(lobby);

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "PLAYER_LIST_CHANGED");
        msg.put("lobby", dto);

        String json;
        try {
            json = objectMapper.writeValueAsString(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (WebSocketSession session : sessions.getOrDefault(code, Set.of())) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException ignored) {}
        }
    }

    private String getLobbyCode(WebSocketSession session) {
        return (String) session.getAttributes().get("code");
    }
}
