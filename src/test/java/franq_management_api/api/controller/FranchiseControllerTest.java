package franq_management_api.api.controller;

import franq_management_api.application.service.franchise.FranchiseService;
import franq_management_api.domain.models.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseControllerTest {
    @Mock
    private FranchiseService franchiseService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        FranchiseController controller =
                new FranchiseController(franchiseService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();
    }

    @Test
    void shouldCreateFranchise() {

        UUID franchiseId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        when(franchiseService.create("Franchise A"))
                .thenReturn(Mono.just(franchise));

        webTestClient
                .post()
                .uri("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "name": "Franchise A"
                    }
                    """)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(franchiseId.toString())
                .jsonPath("$.name")
                .isEqualTo("Franchise A");
    }

    @Test
    void shouldFindAllFranchises() {

        Franchise franchise1 = new Franchise(
                UUID.randomUUID(),
                "Franchise A"
        );

        Franchise franchise2 = new Franchise(
                UUID.randomUUID(),
                "Franchise B"
        );

        when(franchiseService.findAll())
                .thenReturn(
                        Flux.just(
                                franchise1,
                                franchise2
                        )
                );

        webTestClient
                .get()
                .uri("/franchises")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].name")
                .isEqualTo("Franchise A")
                .jsonPath("$[1].name")
                .isEqualTo("Franchise B");
    }

    @Test
    void shouldFindFranchiseById() {

        UUID franchiseId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        when(franchiseService.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        webTestClient
                .get()
                .uri("/franchises/{id}", franchiseId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(franchiseId.toString())
                .jsonPath("$.name")
                .isEqualTo("Franchise A");
    }
}
