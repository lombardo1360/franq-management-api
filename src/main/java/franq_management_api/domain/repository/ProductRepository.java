package franq_management_api.domain.repository;

import franq_management_api.domain.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository {

    Mono<Product> save(Product product, UUID franchiseId);

    Mono<Product> findById(
            UUID franchiseId,
            UUID branchId,
            UUID productId
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

    Flux<Product> findTopStockByBranch(UUID franchiseId);
}
