package org.example.pactimemultiplayer.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.service.GoogleOAuthService;
import org.example.pactimemultiplayer.service.JwtService;
import org.example.pactimemultiplayer.service.PlayerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PlayerService playerService;
    private final JwtService jwtService;
    private final GoogleOAuthService authService;

    @Value("${security.google.client-id}")
    private String googleClientId;

    @Value("${config.api-url}")
    private String apiUrl;

//    @PostMapping("/google")
//    public AuthResponse authenticate(@RequestBody GoogleTokenDto dto) {
//        try {
//            GoogleIdToken idToken = googleVerifier.verify(dto.idToken());
//
//            if (idToken == null) {
//                throw new ResponseStatusException(
//                        HttpStatus.UNAUTHORIZED, "Invalid Google token"
//                );
//            }
//
//            Player player = authenticateFromPayload(idToken.getPayload());
//            String jwt = jwtService.createToken(player);
//
//            return new AuthResponse(jwt);
//
//        } catch (Exception e) {
//            throw new ResponseStatusException(
//                    HttpStatus.UNAUTHORIZED, "Google authentication failed", e
//            );
//        }
//    }

    @GetMapping("/google/login")
    public void redirectToGoogle(
            @RequestParam("port") int port,
            @RequestParam("state_nonce") String stateNonce,
            HttpServletResponse response
    ) throws IOException {
        String statePayload = port + ":" + stateNonce;

        String googleAuthUrl =
                "https://accounts.google.com/o/oauth2/v2/auth" +
                        "?response_type=code" +
                        "&client_id=" + URLEncoder.encode(googleClientId, UTF_8) +
                        "&redirect_uri=" + URLEncoder.encode(apiUrl + "/auth/google/callback", UTF_8) +
                        "&scope=openid%20email%20profile" +
                        "&state=" + URLEncoder.encode(statePayload, UTF_8) +
                        "&prompt=select_account";

        response.sendRedirect(googleAuthUrl);
    }

    @GetMapping("/google/callback")
    public void handleGoogleCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletResponse response
    ) throws IOException {
        String[] parts = state.split(":");
        int port = Integer.parseInt(parts[0]);
        String stateNonce = parts[1];

        GoogleIdToken idToken = authService.getGoogleIdToken(code);
        if (idToken == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Google token");
            return;
        }

        Player player = authenticateFromPayload(idToken.getPayload());
        String jwt = jwtService.createToken(player);

        response.sendRedirect(
                "http://localhost:" + port +
                        "?token=" + URLEncoder.encode(jwt, UTF_8) +
                        "&state_nonce=" + URLEncoder.encode(stateNonce, UTF_8)
        );
    }

    private Player authenticateFromPayload(GoogleIdToken.Payload payload) {
        String googleUserId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        return playerService.findOrCreate(googleUserId, email, name);
    }
}
