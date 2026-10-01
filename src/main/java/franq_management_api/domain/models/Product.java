package franq_management_api.domain.models;

import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

import java.util.UUID;

public class Product {
    private UUID id;
    private UUID  branchId;
    private String name;
    private Integer stock;

    public Product(UUID id, UUID branchId, String name, Integer stock) {
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
