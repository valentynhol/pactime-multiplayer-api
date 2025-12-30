package org.example.pactimemultiplayer.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLobbyDto {
    private String name;
    private String gmShortName;
}
