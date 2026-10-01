package franq_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFranchiseNameRequest (

        @NotBlank(message = "Franchise name is required")
        String name
) {
}
