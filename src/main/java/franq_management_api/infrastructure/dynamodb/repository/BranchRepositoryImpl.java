package franq_management_api.infrastructure.dynamodb.repository;

import franq_management_api.domain.models.Branch;
import franq_management_api.domain.repository.BranchRepository;
import franq_management_api.infrastructure.dynamodb.entity.BranchDynamoEntity;
import franq_management_api.infrastructure.dynamodb.mapper.BranchDynamoMapper;
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
public class BranchRepositoryImpl implements BranchRepository {

    private final DynamoDbAsyncTable<BranchDynamoEntity> table;
    private final DynamoDbAsyncClient dynamoDbClient;

    public BranchRepositoryImpl(
            DynamoDbEnhancedAsyncClient enhancedClient,
            DynamoDbAsyncClient dynamoDbAsyncClient) {

        this.dynamoDbClient = dynamoDbAsyncClient;

        this.table = enhancedClient.table(
                "franchise-management",
                TableSchema.fromBean(BranchDynamoEntity.class)
        );
    }

    @Override
    public Mono<Branch> save(Branch branch) {

        BranchDynamoEntity branchDynamo =
                BranchDynamoMapper.toEntity(branch);

        return Mono.fromFuture(
                table.putItem(branchDynamo)
        ).thenReturn(branch);
    }

    @Override
    public Mono<Branch> findById(UUID franchiseId, UUID branchId) {

        BranchDynamoEntity key = new BranchDynamoEntity();

        key.setPk("FRANCHISE#" + franchiseId);
        key.setSk("BRANCH#" + branchId);

        return Mono.fromFuture(
                table.getItem(key)
        ).map(BranchDynamoMapper::toDomain);
    }

    @Override
    public Mono<Branch> updateName(
            UUID franchiseId,
            UUID branchId,
            String name) {

        return updateAttribute(
                franchiseId,
                branchId,
                "name",
                AttributeValue.builder()
                        .s(name)
                        .build()
        );
    }

    private Mono<Branch> updateAttribute(
            UUID franchiseId,
            UUID branchId,
            String attributeName,
            AttributeValue attributeValue) {

        Map<String, AttributeValue> key = Map.of(
                "PK",
                AttributeValue.builder()
                        .s("FRANCHISE#" + franchiseId)
                        .build(),

                "SK",
                AttributeValue.builder()
                        .s("BRANCH#" + branchId)
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
                        .expressionAttributeNames(attributeNames)
                        .expressionAttributeValues(values)
                        .returnValues(ReturnValue.ALL_NEW)
                        .build();

        return Mono.fromFuture(
                dynamoDbClient.updateItem(request)
        ).map(response ->
                BranchDynamoMapper.toDomain(
                        response.attributes()
                )
        );
    }
}
