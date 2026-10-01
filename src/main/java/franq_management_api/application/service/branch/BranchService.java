package franq_management_api.application.service.branch;

import franq_management_api.domain.models.Branch;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BranchService {

    Mono<Branch> create(UUID franchiseId, String name);

    Mono<Branch> updateName(
            UUID franchiseId,
            UUID branchId,
            String name
    );
}
