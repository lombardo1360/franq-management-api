package franq_management_api.api.exception;

public record ErrorResponse(
        int status,
        String message
) {
}