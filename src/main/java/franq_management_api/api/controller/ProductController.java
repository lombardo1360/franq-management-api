package franq_management_api.api.controller;

import franq_management_api.application.dto.CreateProductRequest;
import franq_management_api.application.dto.ProductTopStockResponse;
import franq_management_api.application.dto.UpdateProductNameRequest;
import franq_management_api.application.dto.UpdateStockRequest;
import franq_management_api.application.service.product.ProductService;
import franq_management_api.domain.models.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/franchises")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @Valid @RequestBody CreateProductRequest request){

        return productService.create(
                franchiseId,
                branchId,
                request.name(),
                request.stock()
        );

    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId){

        return productService.delete(
                franchiseId,
                branchId,
                productId
        );
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/product/{productId}/stock")
    public Mono<Product> updateStock(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateStockRequest request) {

        return productService.updateStock(
                franchiseId,
                branchId,
                productId,
                request.stock()
        );
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/product/{productId}/name")
    public Mono<Product> updateName(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductNameRequest request) {

        return productService.updateName(
                franchiseId,
                branchId,
                productId,
                request.name()
        );
    }

    @GetMapping("/{franchiseId}/products/top-stock")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProductTopStockResponse> findTopStockByBranch(
            @PathVariable UUID franchiseId) {

        return productService.findTopStockByBranch(franchiseId);
    }

}
