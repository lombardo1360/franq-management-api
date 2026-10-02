package franq_management_api.application.service.product;

import franq_management_api.application.dto.ProductTopStockResponse;
import franq_management_api.domain.exception.BranchNotFoundException;
import franq_management_api.domain.exception.FranchiseNotFoundException;
import franq_management_api.domain.models.Branch;
import franq_management_api.domain.models.Franchise;
import franq_management_api.domain.models.Product;
import franq_management_api.domain.repository.BranchRepository;
import franq_management_api.domain.repository.FranchiseRepository;
import franq_management_api.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private BranchRepository branchRepository;
    private FranchiseRepository franchiseRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {

        productRepository = mock(ProductRepository.class);
        branchRepository = mock(BranchRepository.class);
        franchiseRepository = mock(FranchiseRepository.class);

        productService = new ProductServiceImpl(
                productRepository,
                branchRepository,
                franchiseRepository
        );
    }

    @Test
    void shouldReturnProductWithHighestStockForEachBranch() {

        UUID franchiseId = UUID.randomUUID();

        UUID branch1 = UUID.randomUUID();
        UUID branch2 = UUID.randomUUID();

        Product product1 = new Product(
                UUID.randomUUID(),
                branch1,
                "Product A",
                10
        );

        Product product2 = new Product(
                UUID.randomUUID(),
                branch1,
                "Product B",
                50
        );

        Product product3 = new Product(
                UUID.randomUUID(),
                branch2,
                "Product C",
                20
        );

        Product product4 = new Product(
                UUID.randomUUID(),
                branch2,
                "Product D",
                80
        );

        when(productRepository.findTopStockByBranch(franchiseId))
                .thenReturn(
                        Flux.just(
                                product1,
                                product2,
                                product3,
                                product4
                        )
                );

        Flux<ProductTopStockResponse> result =
                productService.findTopStockByBranch(franchiseId);

        StepVerifier.create(result)
                .recordWith(java.util.ArrayList::new)
                .thenConsumeWhile(response -> true)
                .consumeRecordedWith(responses -> {

                    org.junit.jupiter.api.Assertions.assertEquals(
                            2,
                            responses.size()
                    );

                    List<Integer> stocks = responses.stream()
                            .map(ProductTopStockResponse::stock)
                            .sorted()
                            .toList();

                    org.junit.jupiter.api.Assertions.assertEquals(
                            List.of(50, 80),
                            stocks
                    );
                })
                .verifyComplete();

        verify(productRepository)
                .findTopStockByBranch(franchiseId);

        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldCreateProduct() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(
                        new Franchise(
                                franchiseId,
                                "Franchise"
                        )
                ));

        when(branchRepository.findById(franchiseId, branchId))
                .thenReturn(Mono.just(
                        new Branch(
                                branchId,
                                franchiseId,
                                "Branch"
                        )
                ));

        when(productRepository.save(any(Product.class), eq(franchiseId)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        productService.create(
                                franchiseId,
                                branchId,
                                "Product A",
                                100
                        )
                )
                .assertNext(product -> {

                    org.junit.jupiter.api.Assertions.assertNotNull(
                            product.getId()
                    );

                    org.junit.jupiter.api.Assertions.assertEquals(
                            branchId,
                            product.getBranchId()
                    );

                    org.junit.jupiter.api.Assertions.assertEquals(
                            "Product A",
                            product.getName()
                    );

                    org.junit.jupiter.api.Assertions.assertEquals(
                            100,
                            product.getStock()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .findById(franchiseId);

        verify(branchRepository)
                .findById(franchiseId, branchId);

        verify(productRepository)
                .save(any(Product.class), eq(franchiseId));

    }

    @Test
    void shouldFailWhenFranchiseDoesNotExist() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        productService.create(
                                franchiseId,
                                branchId,
                                "Product A",
                                100
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof FranchiseNotFoundException &&
                                error.getMessage().equals("Franchise not found")
                )
                .verify();

        verify(franchiseRepository)
                .findById(franchiseId);

        verifyNoInteractions(branchRepository);
        verifyNoInteractions(productRepository);

    }

    @Test
    void shouldFailWhenBranchDoesNotExist() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(
                        new Franchise(
                                franchiseId,
                                "Franchise"
                        )
                ));

        when(branchRepository.findById(franchiseId, branchId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        productService.create(
                                franchiseId,
                                branchId,
                                "Product A",
                                100
                        )
                )
                .expectErrorMatches(error ->
                        error instanceof BranchNotFoundException &&
                                error.getMessage().equals("Branch not found")
                )
                .verify();

        verify(franchiseRepository)
                .findById(franchiseId);

        verify(branchRepository)
                .findById(franchiseId, branchId);

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldUpdateProductStock() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product(
                productId,
                branchId,
                "Product A",
                10
        );

        Product updatedProduct = new Product(
                productId,
                branchId,
                "Product A",
                50
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(
                        new Franchise(
                                franchiseId,
                                "Franchise"
                        )
                ));

        when(branchRepository.findById(franchiseId, branchId))
                .thenReturn(Mono.just(
                        new Branch(
                                branchId,
                                franchiseId,
                                "Branch"
                        )
                ));

        when(productRepository.findById(
                franchiseId,
                branchId,
                productId
        )).thenReturn(Mono.just(product));

        when(productRepository.updateStock(
                franchiseId,
                branchId,
                productId,
                50
        )).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(
                        productService.updateStock(
                                franchiseId,
                                branchId,
                                productId,
                                50
                        )
                )
                .assertNext(result -> {

                    org.junit.jupiter.api.Assertions.assertEquals(
                            productId,
                            result.getId()
                    );

                    org.junit.jupiter.api.Assertions.assertEquals(
                            50,
                            result.getStock()
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product(
                productId,
                branchId,
                "Product A",
                10
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(
                        new Franchise(
                                franchiseId,
                                "Franchise"
                        )
                ));

        when(branchRepository.findById(franchiseId, branchId))
                .thenReturn(Mono.just(
                        new Branch(
                                branchId,
                                franchiseId,
                                "Branch"
                        )
                ));

        when(productRepository.findById(
                franchiseId,
                branchId,
                productId
        )).thenReturn(Mono.just(product));

        when(productRepository.delete(
                franchiseId,
                branchId,
                productId
        )).thenReturn(Mono.empty());

        StepVerifier.create(
                        productService.delete(
                                franchiseId,
                                branchId,
                                productId
                        )
                )
                .verifyComplete();

        verify(productRepository)
                .delete(
                        franchiseId,
                        branchId,
                        productId
                );
    }


}

