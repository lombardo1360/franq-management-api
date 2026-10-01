package franq_management_api.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(

        @NotBlank(message = "The product name is required")
        String name,

        @NotNull(message = "The stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock
) {
}
