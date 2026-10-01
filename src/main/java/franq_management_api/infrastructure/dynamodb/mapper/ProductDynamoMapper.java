package franq_management_api.infrastructure.dynamodb.mapper;

import franq_management_api.domain.models.Product;
import franq_management_api.infrastructure.dynamodb.entity.ProductDynamoEntity;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

public class ProductDynamoMapper {

    public static ProductDynamoEntity toEntity(
            Product product,
            UUID franchiseId) {

        ProductDynamoEntity entity = new ProductDynamoEntity();

        entity.setPk("FRANCHISE#" + franchiseId);
        entity.setSk(
                "BRANCH#" + product.getBranchId()
                        + "#PRODUCT#" + product.getId()
        );

        entity.setId(product.getId().toString());
        entity.setFranchiseId(franchiseId.toString());
        entity.setBranchId(product.getBranchId().toString());
        entity.setName(product.getName());
        entity.setStock(product.getStock());

        return entity;
    }

    public static Product toDomain(ProductDynamoEntity entity) {

        return new Product(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getBranchId()),
                entity.getName(),
                entity.getStock()
        );
    }

    public static Product toDomain(
            Map<String, AttributeValue> attributes) {

        UUID id = UUID.fromString(
                attributes.get("id").s()
        );

        UUID branchId = UUID.fromString(
                attributes.get("branchId").s()
        );

        String name = attributes.get("name").s();

        Integer stock = Integer.valueOf(
                attributes.get("stock").n()
        );

        return new Product(
                id,
                branchId,
                name,
                stock
        );
    }
}
