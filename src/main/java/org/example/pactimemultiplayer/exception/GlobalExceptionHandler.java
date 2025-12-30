package org.example.pactimemultiplayer.exception;

import org.example.pactimemultiplayer.dto.ErrorResponse;
import org.example.pactimemultiplayer.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LobbyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLobbyNotFound(LobbyNotFoundException ignoredEx) {
        return new ErrorResponse(ErrorCode.LOBBY_NOT_FOUND);
    }

    @ExceptionHandler(LobbyStartedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleLobbyStarted(LobbyStartedException ignoredEx) {
        return new ErrorResponse(ErrorCode.LOBBY_ALREADY_STARTED);
    }

    @ExceptionHandler(LobbyFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleLobbyFull(LobbyFullException ignoredEx) {
        return new ErrorResponse(ErrorCode.LOBBY_FULL);
    }

    @ExceptionHandler(InvalidGameModeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidGameMode(InvalidGameModeException ignoredEx) {
        return new ErrorResponse(ErrorCode.INVALID_GAME_MODE);
    }

    @ExceptionHandler(PlayerNotInLobbyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePlayerNotInLobby(PlayerNotInLobbyException ignoredEx) {
        return new ErrorResponse(ErrorCode.PLAYER_NOT_IN_LOBBY);
    }

    @ExceptionHandler(PlayerIsAlreadyInLobbyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePlayerIsAlreadyInLobby(PlayerIsAlreadyInLobbyException ignoredEx) {
        return new ErrorResponse(ErrorCode.PLAYER_IS_ALREADY_IN_LOBBY);
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlePlayerNotFound(PlayerNotFoundException ignoredEx) {
        return new ErrorResponse(ErrorCode.PLAYER_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ignoredEx) {
        return new ErrorResponse(ErrorCode.VALIDATION_FAILED);
    }

    @ExceptionHandler(PlayerIsNotTheHostException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlePlayerIsNotTheHost(PlayerIsNotTheHostException ignoredEx) {
        return new ErrorResponse(ErrorCode.PLAYER_IS_NOT_THE_HOST);
    }
}
