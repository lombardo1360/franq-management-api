package franq_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest (
        @NotBlank(message = "The franchise name is required")
        String name
){
}
