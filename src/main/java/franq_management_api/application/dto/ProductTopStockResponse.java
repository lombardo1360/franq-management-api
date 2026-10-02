package franq_management_api.application.dto;

import java.util.UUID;

public record ProductTopStockResponse(
        UUID productId,
        String productName,
        Integer stock,
        UUID branchId
) {
}
