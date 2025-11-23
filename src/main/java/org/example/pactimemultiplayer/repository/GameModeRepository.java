package org.example.pactimemultiplayer.repository;

import org.example.pactimemultiplayer.entity.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Long> {
    Optional<GameMode> findByShortName(String shortName);
}