package franq_management_api.application.service.product;

import franq_management_api.application.dto.ProductTopStockResponse;
import franq_management_api.domain.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {

    Mono<Product> create(
            UUID franchiseId,
            UUID branchId,
            String name,
            Integer stock
    );

    Mono<Void> delete(
            UUID franchiseId,
            UUID branchId,
            UUID productId
    );

    Mono<Product> updateStock(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            Integer stock
    );

    Mono<Product> updateName(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            String name
    );

    Flux<ProductTopStockResponse> findTopStockByBranch(UUID franchiseId);
}
