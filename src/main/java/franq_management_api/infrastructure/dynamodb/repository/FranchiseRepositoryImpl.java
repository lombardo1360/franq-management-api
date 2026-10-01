package franq_management_api.infrastructure.dynamodb.repository;

import franq_management_api.domain.models.Franchise;
import franq_management_api.domain.repository.FranchiseRepository;
import franq_management_api.infrastructure.dynamodb.entity.FranchiseDynamoEntity;
import franq_management_api.infrastructure.dynamodb.mapper.FranchiseDynamoMapper;
import franq_management_api.infrastructure.dynamodb.mapper.ProductDynamoMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;
import java.util.UUID;

@Repository
public class FranchiseRepositoryImpl implements FranchiseRepository {

    private final DynamoDbAsyncTable<FranchiseDynamoEntity> table;
    private final DynamoDbAsyncClient dynamoDbClient;

    public FranchiseRepositoryImpl(
            DynamoDbEnhancedAsyncClient enhancedClient,
            DynamoDbAsyncClient dynamoDbClient) {

        this.dynamoDbClient = dynamoDbClient;

        this.table = enhancedClient.table(
                "franchise-management",
                TableSchema.fromBean(FranchiseDynamoEntity.class)
        );
    }

    @Override
    public Mono<Franchise> save(
            Franchise franchise) {

        FranchiseDynamoEntity franchiseDynamo = FranchiseDynamoMapper.toEntity(franchise);

        return Mono.fromFuture(
                table.putItem(franchiseDynamo)
        ).thenReturn(franchise);
    }

    @Override
    public Flux<Franchise> findAll() {

        return Flux.from(
                        table.scan()
                )
                .flatMapIterable(page -> page.items())
                .filter(entity ->
                        "METADATA".equals(entity.getSk())
                )
                .map(FranchiseDynamoMapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(UUID id) {

        FranchiseDynamoEntity key = new FranchiseDynamoEntity();

        key.setPk("FRANCHISE#" + id);
        key.setSk("METADATA");

        return Mono.fromFuture(
                table.getItem(key)
        ).map(FranchiseDynamoMapper::toDomain);
    }

    @Override
    public Mono<Franchise> updateName(
            UUID franchiseId,
            String name) {

        return updateAttribute(
                franchiseId,
                "name",
                AttributeValue.builder()
                        .s(name)
                        .build()
        );
    }

    private Mono<Franchise> updateAttribute(
            UUID franchiseId,
            String attributeName,
            AttributeValue attributeValue) {

        Map<String, AttributeValue> key = Map.of(
                "PK",
                AttributeValue.builder()
                        .s("FRANCHISE#" + franchiseId)
                        .build(),

                "SK",
                AttributeValue.builder()
                        .s("METADATA")
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
                FranchiseDynamoMapper.toDomain(
                        response.attributes()
                )
        );
    }
}
