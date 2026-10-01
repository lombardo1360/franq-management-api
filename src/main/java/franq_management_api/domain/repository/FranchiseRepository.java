package franq_management_api.domain.repository;

import franq_management_api.domain.models.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FranchiseRepository {
    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(UUID id);

    Flux<Franchise> findAll();

    Mono<Franchise> updateName(
            UUID franchiseId,
            String name
    );
}
