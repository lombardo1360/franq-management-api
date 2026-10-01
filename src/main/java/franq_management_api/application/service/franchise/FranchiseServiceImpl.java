package franq_management_api.application.service.franchise;

import franq_management_api.domain.exception.FranchiseNotFoundException;
import franq_management_api.domain.models.Franchise;
import franq_management_api.domain.repository.FranchiseRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class FranchiseServiceImpl  implements FranchiseService  {
    private final FranchiseRepository franchiseRepository;

    public FranchiseServiceImpl(FranchiseRepository franchiseRepository){
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Franchise> create(String name){
        Franchise franchise = new Franchise(
                UUID.randomUUID(),
                name
        );

        return franchiseRepository.save(franchise);
    }

    @Override
    public Flux<Franchise> findAll() {
        return franchiseRepository.findAll();
    }

    @Override
    public Mono<Franchise> findById(UUID id) {
        return franchiseRepository.findById(id);
    }

    @Override
    public Mono<Franchise> updateName(
            UUID franchiseId,
            String name) {

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(
                        Mono.error(
                                new FranchiseNotFoundException(
                                        "Franchise not found"
                                )
                        )
                )
                .flatMap(franchise ->
                        franchiseRepository.updateName(
                                franchiseId,
                                name
                        )
                );
    }
}
