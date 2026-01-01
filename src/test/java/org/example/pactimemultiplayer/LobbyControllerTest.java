package org.example.pactimemultiplayer;

import org.example.pactimemultiplayer.controller.LobbyController;
import org.example.pactimemultiplayer.exception.GlobalExceptionHandler;
import org.example.pactimemultiplayer.exception.LobbyNotFoundException;
import org.example.pactimemultiplayer.security.AuthenticatedPlayerArgumentResolver;
import org.example.pactimemultiplayer.security.JwtAuthenticationFilter;
import org.example.pactimemultiplayer.service.LobbyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ GlobalExceptionHandler.class })
class LobbyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LobbyService lobbyService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    AuthenticatedPlayerArgumentResolver authenticatedPlayerArgumentResolver;

    @Test
    void getJoinableLobbies_returns200() throws Exception {
        when(lobbyService.getJoinableLobbies())
                .thenReturn(List.of());

        mockMvc.perform(get("/lobbies"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLobby_notFound() throws Exception {
        doThrow(new LobbyNotFoundException())
                .when(lobbyService).deleteLobby(any(), any());

        mockMvc.perform(delete("/lobbies")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"lobbyCode":"NOPE"}
                        """))
                .andExpect(status().isNotFound());
    }
}
