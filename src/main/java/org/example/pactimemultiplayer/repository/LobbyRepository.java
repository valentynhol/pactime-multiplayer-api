package org.example.pactimemultiplayer.repository;

import org.example.pactimemultiplayer.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, String> {
    @Query("SELECT l FROM Lobby l WHERE size(l.players) < :maxPlayers AND l.started = false")
    List<Lobby> findJoinableLobbies(int maxPlayers);

    @Query("SELECT l FROM Lobby l LEFT JOIN FETCH l.players WHERE l.code = :code")
    Optional<Lobby> findWithPlayers(@Param("code") String code);
}
