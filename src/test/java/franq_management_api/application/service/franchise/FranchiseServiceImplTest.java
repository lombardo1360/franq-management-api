package franq_management_api.application.service.franchise;

import franq_management_api.domain.models.Franchise;
import franq_management_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceImplTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private FranchiseService franchiseService;

    @BeforeEach
    void setUp() {
        franchiseService = new FranchiseServiceImpl(
                franchiseRepository
        );
    }

    @Test
    void shouldCreateFranchise() {

        UUID franchiseId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(
                        franchiseService.create("Franchise A")
                )
                .assertNext(result -> {

                    assertNotNull(result.getId());

                    assertEquals(
                            "Franchise A",
                            result.getName()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .save(any(Franchise.class));
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

        when(franchiseRepository.findAll())
                .thenReturn(
                        Flux.just(
                                franchise1,
                                franchise2
                        )
                );

        StepVerifier.create(
                        franchiseService.findAll()
                )
                .expectNext(franchise1)
                .expectNext(franchise2)
                .verifyComplete();

        verify(franchiseRepository)
                .findAll();
    }

    @Test
    void shouldFindFranchiseById() {

        UUID franchiseId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(
                        franchiseService.findById(franchiseId)
                )
                .assertNext(result -> {

                    assertEquals(
                            franchiseId,
                            result.getId()
                    );

                    assertEquals(
                            "Franchise A",
                            result.getName()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .findById(franchiseId);
    }

    @Test
    void shouldUpdateFranchiseName() {
        UUID franchiseId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        Franchise updatedFranchise = new Franchise(
                franchiseId,
                "New Franchise Name"
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        when(franchiseRepository.updateName(
                franchiseId,
                "New Franchise Name"
        ))
                .thenReturn(
                        Mono.just(updatedFranchise)
                );

        StepVerifier.create(
                        franchiseService.updateName(
                                franchiseId,
                                "New Franchise Name"
                        )
                )
                .assertNext(result -> {

                    assertEquals(
                            franchiseId,
                            result.getId()
                    );

                    assertEquals(
                            "New Franchise Name",
                            result.getName()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .findById(franchiseId);

        verify(franchiseRepository)
                .updateName(
                        franchiseId,
                        "New Franchise Name"
                );

    }

}

