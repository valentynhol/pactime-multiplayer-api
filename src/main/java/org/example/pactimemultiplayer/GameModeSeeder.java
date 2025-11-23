package org.example.pactimemultiplayer;

import org.example.pactimemultiplayer.entity.GameMode;
import org.example.pactimemultiplayer.repository.GameModeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GameModeSeeder implements CommandLineRunner {

    private final GameModeRepository repo;

    public GameModeSeeder(GameModeRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        seed("cl", "Classic");
        seed("tr", "Time race");
        seed("oc", "Obstacle course");
    }

    private void seed(String shortName, String name) {
        repo.findByShortName(name)
                .orElseGet(() -> repo.save(new GameMode(name, shortName)));
    }
}
