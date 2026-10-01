package franq_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBranchNameRequest(

        @NotBlank(message = "Product name is required")
        String name
) {
}
