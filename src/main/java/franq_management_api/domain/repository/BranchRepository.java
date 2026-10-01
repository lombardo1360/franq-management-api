package franq_management_api.domain.repository;

import franq_management_api.domain.models.Branch;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BranchRepository {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(UUID franchiseId, UUID branchId);

    Mono<Branch> updateName(
            UUID franchiseId,
            UUID branchId,
            String name
    );
}
