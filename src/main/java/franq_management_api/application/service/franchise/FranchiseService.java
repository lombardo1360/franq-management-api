package franq_management_api.application.service.franchise;

import franq_management_api.domain.models.Franchise;
import franq_management_api.infrastructure.dynamodb.entity.FranchiseDynamoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FranchiseService {

    Mono<Franchise> create(String name);

    Flux<Franchise> findAll();

    Mono<Franchise> findById(UUID id);

    Mono<Franchise> updateName(
            UUID franchiseId,
            String name
    );
}
