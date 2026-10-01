package franq_management_api.api.controller;

import franq_management_api.application.dto.CreateBranchRequest;
import franq_management_api.application.dto.UpdateBranchNameRequest;
import franq_management_api.application.service.branch.BranchService;
import franq_management_api.domain.models.Branch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/franchises")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService){
        this.branchService = branchService;
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Branch> createBranch(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody CreateBranchRequest request) {

        return branchService.create(
                franchiseId,
                request.name()
        );
    }

    @PatchMapping("{franchiseId}/branches/{branchId}/name")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Branch> updateName(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateBranchNameRequest request) {

        return branchService.updateName(
                franchiseId,
                branchId,
                request.name()
        );
    }
}
