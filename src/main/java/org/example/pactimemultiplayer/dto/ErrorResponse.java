package org.example.pactimemultiplayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.pactimemultiplayer.error.ErrorCode;

@Schema(description = "Standard error response")
public record ErrorResponse(
        @Schema(
                description = "Application-specific error code",
                example = "LOBBY_NOT_FOUND"
        )
        ErrorCode code
) {}
