package org.example.pactimemultiplayer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Transactional
    public Player findOrCreate(String username) {
        return playerRepository.findByUsername(username)
                .orElseGet(() -> playerRepository.save(new Player(username)));
    }
}
