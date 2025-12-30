package org.example.pactimemultiplayer.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.pactimemultiplayer.dto.LobbyDto;
import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.service.LobbyService;
import org.springframework.lang.NonNull;
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
    private final LobbyService lobbyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, JsonNode>> gameEndData = new ConcurrentHashMap<>();
    private final Map<String, Integer> expectedPlayers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String code = path.substring(path.lastIndexOf('/') + 1);

        String raw_uname = "";
        for (String param : session.getUri().getQuery().split("&")) {
            if (param.startsWith("user=")) {
                raw_uname = param.substring(5);
            }
        }
        String username = URLDecoder.decode(raw_uname, StandardCharsets.UTF_8);

        session.getAttributes().put("code", code);
        session.getAttributes().put("username", username);

        sessions.computeIfAbsent(code, k -> ConcurrentHashMap.newKeySet())
                .add(session);

        broadcastLobbyUpdate(code);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String code = getLobbyCode(session);
        String username = session.getAttributes().get("username").toString();
        if (code == null) return;
        System.out.println(username + ": session closed");

        sessions.getOrDefault(code, Set.of()).remove(session);

        Lobby lobby = lobbyService.removePlayer(code, username);
        if (lobby == null) return;

        if (lobby.isStarted()) {
            expectedPlayers.computeIfPresent(code, (k, v) -> Math.max(0, v - 1));

            int received = gameEndData.getOrDefault(code, Map.of()).size();
            int expected = expectedPlayers.getOrDefault(code, 0);

            if (expected > 0 && received >= expected) {
                broadcastFinalScoreboard(code);
                cleanupGameEndState(code);
            }
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        String code = getLobbyCode(session);

        JsonNode root = objectMapper.readTree(message.getPayload());
        String type = root.get("type").asText();

        switch (type) {
            case "STATE_UPDATE" -> handleStateUpdate(code, root, session);
            case "GAME_END" -> handleGameEnd(code, root, session);
        }
    }

    public void broadcastLobbyUpdate(String code) {
        Lobby lobby = lobbyRepository.findWithPlayers(code).orElseThrow();
        LobbyDto dto = LobbyDto.from(lobby);

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "PLAYER_LIST_CHANGED");
        msg.put("lobby", dto);

        broadcastMsg(code, msg);
    }

    public void broadcastCountdown(String code, int number) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "COUNTDOWN");
        msg.put("number", number);
        broadcastMsg(code, msg);
    }

    public void broadcastGameStart(String code, String map) {
        List<String> players = getConnectedUsernames(code);
        expectedPlayers.put(code, players.size());

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "GAME_START");
        msg.put("gameMap", map);
        msg.put("connectedPlayers", players);
        broadcastMsg(code, msg);
    }

    public void broadcastFinalScoreboard(String code) {
        Lobby lobby = lobbyRepository.findById(code).orElseThrow();
        lobby.setStarted(false);
        lobbyRepository.save(lobby);

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "GAME_END");
        msg.put("results", gameEndData.get(code));
        broadcastMsg(code, msg);

        broadcastLobbyUpdate(code);
    }

    private void handleStateUpdate(String code, JsonNode root, WebSocketSession sender) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "STATE_UPDATE");
        msg.put("username", root.get("username").asText());
        msg.put("state", root.get("state"));
        broadcastMsg(code, msg, sender);
    }

    private void handleGameEnd(String code, JsonNode root, WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");

        gameEndData.computeIfAbsent(code, k -> new ConcurrentHashMap<>())
                .put(username, root.get("stats"));

        int received = gameEndData.get(code).size();
        int expected = expectedPlayers.getOrDefault(code, 0);

        if (received >= expected && expected > 0) {
            broadcastFinalScoreboard(code);
            cleanupGameEndState(code);
        }
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

    private List<String> getConnectedUsernames(String code) {
        return sessions.getOrDefault(code, Set.of())
                .stream()
                .map(s -> (String) s.getAttributes().get("username"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void cleanupGameEndState(String code) {
        gameEndData.remove(code);
        expectedPlayers.remove(code);
    }
}
