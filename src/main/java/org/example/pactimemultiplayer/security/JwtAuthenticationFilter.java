package org.example.pactimemultiplayer.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.error.ErrorCode;
import org.example.pactimemultiplayer.exception.PlayerNotFoundException;
import org.example.pactimemultiplayer.repository.PlayerRepository;
import org.example.pactimemultiplayer.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final PlayerRepository playerRepository;

    public JwtAuthenticationFilter(JwtService jwtService, PlayerRepository playerRepository) {
        this.jwtService = jwtService;
        this.playerRepository = playerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException, PlayerNotFoundException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Claims claims = jwtService.parseToken(token);
                String playerId = claims.getSubject();
                Player player = playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(player, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (PlayerNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"error\":\"" + ErrorCode.PLAYER_NOT_FOUND.name() + "\"}"
                );
                return;
            } catch (JwtException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
