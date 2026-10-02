package franq_management_api.api.controller;

import franq_management_api.application.service.branch.BranchService;
import franq_management_api.domain.models.Branch;
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
class BranchControllerTest {
    @Mock
    private BranchService branchService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {

        BranchController controller =
                new BranchController(branchService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();
    }

    @Test
    void shouldCreateBranch() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Branch branch = new Branch(
                branchId,
                franchiseId,
                "Branch A"
        );

        when(branchService.create(
                franchiseId,
                "Branch A"
        ))
                .thenReturn(Mono.just(branch));

        webTestClient
                .post()
                .uri(
                        "/franchises/{franchiseId}/branches",
                        franchiseId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "name": "Branch A"
                    }
                    """)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(branchId.toString())
                .jsonPath("$.franchiseId")
                .isEqualTo(franchiseId.toString())
                .jsonPath("$.name")
                .isEqualTo("Branch A");
    }

    @Test
    void shouldUpdateBranchName() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Branch updatedBranch = new Branch(
                branchId,
                franchiseId,
                "New Branch Name"
        );

        when(branchService.updateName(
                franchiseId,
                branchId,
                "New Branch Name"
        ))
                .thenReturn(Mono.just(updatedBranch));

        webTestClient
                .patch()
                .uri(
                        "/franchises/{franchiseId}/branches/{branchId}/name",
                        franchiseId,
                        branchId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "name": "New Branch Name"
                    }
                    """)
                .exchange()
                .expectStatus()
                .isAccepted()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(branchId.toString())
                .jsonPath("$.franchiseId")
                .isEqualTo(franchiseId.toString())
                .jsonPath("$.name")
                .isEqualTo("New Branch Name");
    }

}
