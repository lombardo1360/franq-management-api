package franq_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBranchRequest(

        @NotBlank(message = "The branch name is required")
        String name
) {
}
