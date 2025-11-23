package org.example.pactimemultiplayer.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.pactimemultiplayer.dto.LobbyDto;
import org.example.pactimemultiplayer.dto.StartLobbyDto;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        String username = URLDecoder.decode(session.getUri().getQuery()
                .replace("user=", ""), StandardCharsets.UTF_8);

        System.out.println("Debug: opening ws with code " + code + " username " + username);

        session.getAttributes().put("code", code);
        session.getAttributes().put("username", username);

        sessions.computeIfAbsent(code, k -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String code = getLobbyCode(session);
        System.out.println("Debug: closing ws with code " + code);
        sessions.getOrDefault(code, Set.of()).remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String code = getLobbyCode(session);

        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.get("type").asText();

        switch (type) {
            case "STATE_UPDATE" -> handleStateUpdate(code, root, session);
            // might be more
        }
    }

    @Async("lobbyExecutor")
    public void runCountdown(StartLobbyDto dto) {
        try {
            for (int i = 3; i > 0; i--) {
                broadcastCountdown(dto.getCode(), i);
                Thread.sleep(1000);
            }
            broadcastGameStart(dto.getCode(), dto.getGameMap());
        } catch (InterruptedException ignored) {}
    }

    public List<String> getConnectedUsernames(String code) {
        return sessions.getOrDefault(code, Set.of())
                .stream()
                .map(s -> (String) s.getAttributes().get("username"))
                .filter(Objects::nonNull)
                .toList();
    }

    private void handleStateUpdate(String code, JsonNode root, WebSocketSession sender) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "STATE_UPDATE");
        msg.put("username", root.get("username").asText());
        msg.put("state", root.get("state"));

        broadcastMsg(code, msg, sender);
    }

    public void broadcastLobbyUpdate(String code) {
        Lobby lobby = lobbyRepository.findById(code).orElseThrow();
        LobbyDto dto = LobbyDto.from(lobby);

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "PLAYER_LIST_CHANGED");
        msg.put("lobby", dto);

        broadcastMsg(code, msg);
    }

    public void broadcastGameStart(String code, String map) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "GAME_START");
        msg.put("gameMap", map);
        msg.put("connectedPlayers", getConnectedUsernames(code));

        broadcastMsg(code, msg);
    }

    public void broadcastCountdown(String code, int number) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "COUNTDOWN");
        msg.put("number", number);
        broadcastMsg(code, msg);
    }

    private void broadcastMsg(String code, Map<String, Object> msg) { broadcastMsg(code, msg, null); }
    private void broadcastMsg(String code, Map<String, Object> msg, WebSocketSession except) {
        String json;
        try {
            json = objectMapper.writeValueAsString(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (WebSocketSession session : sessions.getOrDefault(code, Set.of())) {
            if (except != null && session == except) continue;

            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException ignored) {}
        }
    }

    private String getLobbyCode(WebSocketSession session) {
        return (String) session.getAttributes().get("code");
    }
}
