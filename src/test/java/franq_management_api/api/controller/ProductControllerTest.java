package franq_management_api.api.controller;

import franq_management_api.application.service.product.ProductService;
import franq_management_api.domain.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    private ProductService productService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        ProductController controller =
                new ProductController(productService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();
    }

    @Test
    void shouldCreateProduct() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = new Product(
                productId,
                branchId,
                "Product A",
                100
        );

        when(productService.create(
                franchiseId,
                branchId,
                "Product A",
                100
        ))
                .thenReturn(Mono.just(product));

        webTestClient
                .post()
                .uri(
                        "/franchises/{franchiseId}/branches/{branchId}/products",
                        franchiseId,
                        branchId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "name": "Product A",
                        "stock": 100
                    }
                    """)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(productId.toString())
                .jsonPath("$.branchId")
                .isEqualTo(branchId.toString())
                .jsonPath("$.name")
                .isEqualTo("Product A")
                .jsonPath("$.stock")
                .isEqualTo(100);
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
                200
        );

        when(productService.updateStock(
                franchiseId,
                branchId,
                productId,
                200
        ))
                .thenReturn(Mono.just(product));

        webTestClient
                .patch()
                .uri(
                        "/franchises/{franchiseId}/branches/{branchId}/product/{productId}/stock",
                        franchiseId,
                        branchId,
                        productId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "stock": 200
                    }
                    """)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(productId.toString())
                .jsonPath("$.stock")
                .isEqualTo(200);
    }

    @Test
    void shouldDeleteProduct() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(productService.delete(
                franchiseId,
                branchId,
                productId
        ))
                .thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(
                        "/franchises/{franchiseId}/branches/{branchId}/product/{productId}",
                        franchiseId,
                        branchId,
                        productId
                )
                .exchange()
                .expectStatus()
                .isNoContent();
    }

}
