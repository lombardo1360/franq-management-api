package franq_management_api.api.controller;

import franq_management_api.application.dto.CreateFranchiseRequest;
import franq_management_api.application.dto.UpdateFranchiseNameRequest;
import franq_management_api.application.service.franchise.FranchiseService;
import franq_management_api.domain.models.Franchise;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/franchises")
public class FranchiseController {
    private final FranchiseService franchiseService;

    public FranchiseController(FranchiseService franchiseService){
        this.franchiseService = franchiseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> create(@Valid @RequestBody CreateFranchiseRequest request) {
        return franchiseService.create(request.name());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Franchise> findAll() {
        return franchiseService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Franchise> findById(
            @PathVariable UUID id) {

        return franchiseService.findById(id);
    }

    @PatchMapping("/{franchiseId}/name")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Franchise> updateName(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody UpdateFranchiseNameRequest request) {

        return franchiseService.updateName(
                franchiseId,
                request.name()
        );
    }
}
