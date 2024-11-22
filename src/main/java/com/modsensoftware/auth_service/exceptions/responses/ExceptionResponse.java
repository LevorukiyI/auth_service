package com.modsensoftware.auth_service.exceptions.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response with error report. You take it as response when error appears.")
public record ExceptionResponse(
        @Schema(description = "Description message of exception that appears in api.")
        String errorMessage
) {
}
