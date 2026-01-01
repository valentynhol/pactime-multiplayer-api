package org.example.pactimemultiplayer;

import org.example.pactimemultiplayer.entity.Lobby;
import org.example.pactimemultiplayer.entity.Player;
import org.example.pactimemultiplayer.repository.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class LobbyRepositoryTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private TestEntityManager em;

    private Player player;

    @BeforeEach
    void setup() {
        player = new Player("p1", "user1", "u1@mail.com");
        em.persist(player);
    }

    @Test
    void findJoinableLobbies_returnsOnlyNotStartedAndNotFull() {
        Lobby lobby = new Lobby();
        lobby.setCode("ABC");
        lobby.setHost(player);
        lobby.setStarted(false);
        lobby.getPlayers().add(player);

        em.persist(lobby);
        em.flush();

        List<Lobby> result = lobbyRepository.findJoinableLobbies(4);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("ABC");
    }

    @Test
    void findWithPlayers_fetchesPlayers() {
        Lobby lobby = new Lobby("XYZ", player, "Test", false, Set.of(player), null);
        em.persist(lobby);
        em.flush();

        Lobby fetched = lobbyRepository.findWithPlayers("XYZ").orElseThrow();

        assertThat(fetched.getPlayers()).hasSize(1);
    }

    @Test
    void findWithPlayers_returnsEmpty_whenNotFound() {
        assertThat(lobbyRepository.findWithPlayers("NOPE")).isEmpty();
    }
}
