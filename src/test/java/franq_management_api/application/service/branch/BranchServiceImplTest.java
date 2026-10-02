package franq_management_api.application.service.branch;

import franq_management_api.domain.models.Branch;
import franq_management_api.domain.models.Franchise;
import franq_management_api.domain.repository.BranchRepository;
import franq_management_api.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    private BranchService branchService;

    @BeforeEach
    void setUp() {

        branchService = new BranchServiceImpl(
                branchRepository,
                franchiseRepository
        );
    }

    @Test
    void shouldCreateBranch() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        Branch branch = new Branch(
                branchId,
                franchiseId,
                "Branch A"
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        when(branchRepository.save(any(Branch.class)))
                .thenReturn(Mono.just(branch));

        StepVerifier.create(
                        branchService.create(
                                franchiseId,
                                "Branch A"
                        )
                )
                .assertNext(result -> {

                    assertNotNull(result.getId());

                    assertEquals(
                            franchiseId,
                            result.getFranchiseId()
                    );

                    assertEquals(
                            "Branch A",
                            result.getName()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .findById(franchiseId);

        verify(branchRepository)
                .save(any(Branch.class));
    }

    @Test
    void shouldFailWhenFranchiseDoesNotExist() {

        UUID franchiseId = UUID.randomUUID();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        branchService.create(
                                franchiseId,
                                "Branch A"
                        )
                )
                .expectError()
                .verify();

        verify(franchiseRepository)
                .findById(franchiseId);

        verifyNoInteractions(branchRepository);
    }

    @Test
    void shouldUpdateBranchName() {

        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();

        Franchise franchise = new Franchise(
                franchiseId,
                "Franchise A"
        );

        Branch branch = new Branch(
                branchId,
                franchiseId,
                "Branch A"
        );

        Branch updatedBranch = new Branch(
                branchId,
                franchiseId,
                "New Branch Name"
        );

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(franchise));

        when(branchRepository.findById(
                franchiseId,
                branchId
        ))
                .thenReturn(Mono.just(branch));

        when(branchRepository.updateName(
                franchiseId,
                branchId,
                "New Branch Name"
        ))
                .thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(
                        branchService.updateName(
                                franchiseId,
                                branchId,
                                "New Branch Name"
                        )
                )
                .assertNext(result -> {

                    assertEquals(
                            branchId,
                            result.getId()
                    );

                    assertEquals(
                            franchiseId,
                            result.getFranchiseId()
                    );

                    assertEquals(
                            "New Branch Name",
                            result.getName()
                    );
                })
                .verifyComplete();

        verify(franchiseRepository)
                .findById(franchiseId);

        verify(branchRepository)
                .findById(
                        franchiseId,
                        branchId
                );

        verify(branchRepository)
                .updateName(
                        franchiseId,
                        branchId,
                        "New Branch Name"
                );
    }
}
