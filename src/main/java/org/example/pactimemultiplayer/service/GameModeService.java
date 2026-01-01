package org.example.pactimemultiplayer.service;

import lombok.RequiredArgsConstructor;
import org.example.pactimemultiplayer.dto.CreateGameModeDto;
import org.example.pactimemultiplayer.dto.DeleteGameModeDto;
import org.example.pactimemultiplayer.dto.GameModeDto;
import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.repository.GameModeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameModeService {
    private final GameModeRepository gameModeRepository;

    public List<GameModeDto> getGameModes() {
        return gameModeRepository.findAll()
                .stream()
                .map(GameModeDto::from)
                .toList();
    }

    public GameModeDto createGameMode(CreateGameModeDto dto) {
        GameMode gameMode = new GameMode();
        gameMode.setShortName(dto.getShortName());
        gameMode.setName(dto.getName());
        gameModeRepository.save(gameMode);

        return GameModeDto.from(gameMode);
    }

    public void deleteGameMode(DeleteGameModeDto dto) {
        GameMode gm = gameModeRepository.findByShortName(dto.getShortName()).orElseThrow();
        gameModeRepository.delete(gm);
    }
}
