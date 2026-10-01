package franq_management_api.infrastructure.dynamodb.repository;

import franq_management_api.domain.models.Product;
import franq_management_api.domain.repository.ProductRepository;
import franq_management_api.infrastructure.dynamodb.entity.ProductDynamoEntity;
import franq_management_api.infrastructure.dynamodb.mapper.ProductDynamoMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DynamoDbAsyncTable<ProductDynamoEntity> table;
    private final DynamoDbAsyncClient dynamoDbClient;

    public ProductRepositoryImpl(
            DynamoDbEnhancedAsyncClient enhancedClient,
            DynamoDbAsyncClient dynamoDbClient) {

        this.dynamoDbClient = dynamoDbClient;

        this.table = enhancedClient.table(
                "franchise-management",
                TableSchema.fromBean(ProductDynamoEntity.class)
        );
    }

    @Override
    public Mono<Product> save(
            Product product,
            UUID franchiseId) {

        ProductDynamoEntity entity =
                ProductDynamoMapper.toEntity(
                        product,
                        franchiseId
                );

        return Mono.fromFuture(
                table.putItem(entity)
        ).thenReturn(product);
    }

    @Override
    public Mono<Product> findById(
            UUID franchiseId,
            UUID branchId,
            UUID productId) {

        ProductDynamoEntity key = new ProductDynamoEntity();

        key.setPk("FRANCHISE#" + franchiseId);
        key.setSk(
                "BRANCH#" + branchId
                        + "#PRODUCT#" + productId
        );

        return Mono.fromFuture(
                table.getItem(key)
        ).map(ProductDynamoMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(
            UUID franchiseId,
            UUID branchId,
            UUID productId) {

        ProductDynamoEntity key = new ProductDynamoEntity();

        key.setPk("FRANCHISE#" + franchiseId);
        key.setSk(
                "BRANCH#" + branchId
                        + "#PRODUCT#" + productId
        );

        return Mono.fromFuture(
                table.deleteItem(key)
        ).then();
    }

    @Override
    public Mono<Product> updateStock(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            Integer stock) {

        return updateAttribute(
                franchiseId,
                branchId,
                productId,
                "stock",
                AttributeValue.builder()
                        .n(stock.toString())
                        .build()
        );
    }

    @Override
    public Mono<Product> updateName(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            String name) {

        return updateAttribute(
                franchiseId,
                branchId,
                productId,
                "name",
                AttributeValue.builder()
                        .s(name)
                        .build()
        );
    }

    private Mono<Product> updateAttribute(
            UUID franchiseId,
            UUID branchId,
            UUID productId,
            String attributeName,
            AttributeValue attributeValue) {

        Map<String, AttributeValue> key = Map.of(
                "PK",
                AttributeValue.builder()
                        .s("FRANCHISE#" + franchiseId)
                        .build(),

                "SK",
                AttributeValue.builder()
                        .s(
                                "BRANCH#" + branchId
                                        + "#PRODUCT#" + productId
                        )
                        .build()
        );

        Map<String, String> attributeNames =
                Map.of("#attribute", attributeName);

        Map<String, AttributeValue> values =
                Map.of(":value", attributeValue);

        UpdateItemRequest request =
                UpdateItemRequest.builder()
                        .tableName("franchise-management")
                        .key(key)
                        .updateExpression(
                                "SET #attribute = :value"
                        )
                        .expressionAttributeNames(
                                attributeNames
                        )
                        .expressionAttributeValues(values)
                        .returnValues(
                                ReturnValue.ALL_NEW
                        )
                        .build();

        return Mono.fromFuture(
                dynamoDbClient.updateItem(request)
        ).map(response ->
                ProductDynamoMapper.toDomain(
                        response.attributes()
                )
        );
    }
}
