package franq_management_api.application.service.branch;

import franq_management_api.domain.exception.BranchNotFoundException;
import franq_management_api.domain.exception.FranchiseNotFoundException;
import franq_management_api.domain.models.Branch;
import franq_management_api.domain.models.Product;
import franq_management_api.domain.repository.BranchRepository;
import franq_management_api.domain.repository.FranchiseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class BranchServiceImpl implements BranchService{

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public BranchServiceImpl(
            BranchRepository branchRepository,
            FranchiseRepository franchiseRepository){

        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Branch> create(UUID franchiseId, String name){
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.error(
                                new FranchiseNotFoundException(
                                        "Franchise not found"
                                )
                        )
                )
                .flatMap(franchise -> {

                    Branch branch = new Branch(
                            UUID.randomUUID(),
                            franchiseId,
                            name
                    );

                    return branchRepository.save(branch);
                });
    }

    @Override
    public Mono<Branch> updateName(
            UUID franchiseId,
            UUID branchId,
            String name) {

        return findBranch(
                franchiseId,
                branchId
        ).flatMap(branch ->
                branchRepository.updateName(
                        franchiseId,
                        branchId,
                        name
                )
        );
    }

    private Mono<Branch> findBranch(
            UUID franchiseId,
            UUID branchId) {

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
                );
    }
}
