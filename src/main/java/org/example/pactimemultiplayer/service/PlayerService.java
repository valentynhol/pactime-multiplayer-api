package org.example.pactimemultiplayer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.ChangeUsernameDto;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.repository.PlayerRepository;
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

    public void updatePlayerUsername(ChangeUsernameDto playerDto, Player player) {
        player.setUsername(playerDto.getUsername());

        playerRepository.save(player);
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }
}
