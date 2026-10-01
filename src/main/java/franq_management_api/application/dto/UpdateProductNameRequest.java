package franq_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductNameRequest(

        @NotBlank(message = "Product name is required")
        String name
) {
}
