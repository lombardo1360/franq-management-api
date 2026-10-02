package franq_management_api.application.service.product;

import franq_management_api.application.dto.ProductTopStockResponse;
import franq_management_api.domain.exception.BranchNotFoundException;
import franq_management_api.domain.exception.FranchiseNotFoundException;
import franq_management_api.domain.exception.ProductNotFoundException;
import franq_management_api.domain.models.Product;
import franq_management_api.domain.repository.BranchRepository;
import franq_management_api.domain.repository.FranchiseRepository;
import franq_management_api.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            BranchRepository branchRepository,
            FranchiseRepository franchiseRepository) {

        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Product> create(
            UUID franchiseId,
            UUID branchId,
            String name,
            Integer stock) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.error(
                                new FranchiseNotFoundException(
                                        "Franchise not found"
                                )
                        )
                )
                .flatMap(franchise ->
                        branchRepository.findById(
                                franchiseId,
                                branchId
                        )
                )
                .switchIfEmpty(
                        Mono.error(
                                new BranchNotFoundException(
                                        "Branch not found"
                                )
                        )
                )
                .flatMap(branch -> {

                    Product product = new Product(
                            UUID.randomUUID(),
                            branchId,
                            name,
                            stock
                    );

                    return productRepository.save(
                            product,
                            franchiseId
                    );
                });
    }

    @Override
    public Mono<Void> delete(
            UUID franchiseId,
            UUID branchId,
            UUID productId) {

        return findProduct(
                franchiseId,
                branchId,
                productId
        ).flatMap(product ->
                productRepository.delete(
                        franchiseId,
                        branchId,
                        productId
                )
        );
    }

    @Override
    public Mono<Product> updateStock(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            Integer stock) {

        return findProduct(
                franchiseId,
                branchId,
                productId
        ).flatMap(product ->
                productRepository.updateStock(
                        franchiseId,
                        branchId,
                        productId,
                        stock
                )
        );
    }

    @Override
    public Mono<Product> updateName(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            String name) {

        return findProduct(
                franchiseId,
                branchId,
                productId
        ).flatMap(product ->
                productRepository.updateName(
                        franchiseId,
                        branchId,
                        productId,
                        name
                )
        );
    }

    @Override
    public Flux<ProductTopStockResponse> findTopStockByBranch(UUID franchiseId) {

        return productRepository.findTopStockByBranch(franchiseId)
                .groupBy(Product::getBranchId)
                .flatMap(group ->
                        group.reduce((product1, product2) ->
                                product1.getStock() >= product2.getStock()
                                        ? product1
                                        : product2
                        )
                )
                .map(product -> new ProductTopStockResponse(
                        product.getId(),
                        product.getName(),
                        product.getStock(),
                        product.getBranchId()
                ));
    }

    private Mono<Product> findProduct(
            UUID franchiseId,
            UUID branchId,
            UUID productId) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.error(
                                new FranchiseNotFoundException(
                                        "Franchise not found"
                                )
                        )
                )
                .flatMap(franchise ->
                        branchRepository.findById(
                                franchiseId,
                                branchId
                        )
                )
                .switchIfEmpty(
                        Mono.error(
                                new BranchNotFoundException(
                                        "Branch not found"
                                )
                        )
                )
                .flatMap(branch ->
                        productRepository.findById(
                                franchiseId,
                                branchId,
                                productId
                        )
                )
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found"
                                )
                        )
                );
    }
}
