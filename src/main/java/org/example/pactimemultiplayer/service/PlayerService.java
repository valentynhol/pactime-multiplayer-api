package org.example.pactimemultiplayer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.PlayerDto;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.repository.PlayerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Transactional
    public Player findOrCreate(String id, String email, String username) {
        return playerRepository.findById(id)
                .orElseGet(() -> playerRepository.save(new Player(id, username, email)));
    }

    public Player getCurrentPlayer() {
        return (Player) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public void updatePlayer(PlayerDto playerDto, Player player) {
        player.setEmail(playerDto.getEmail());
        player.setUsername(playerDto.getUsername());

        playerRepository.save(player);
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }
}
