package org.example.pactimemultiplayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lobby {
    @Id
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Player host;

    private String name;
    private boolean started;

    @ManyToMany
    @JoinTable(
            name = "lobby_players",
            joinColumns = @JoinColumn(name = "lobby_code"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @ManyToOne
    private GameMode mode;
}
