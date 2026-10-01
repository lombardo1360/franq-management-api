package franq_management_api.infrastructure.dynamodb.mapper;

import franq_management_api.domain.models.Branch;
import franq_management_api.domain.models.Franchise;
import franq_management_api.infrastructure.dynamodb.entity.BranchDynamoEntity;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

public class BranchDynamoMapper {

    public static BranchDynamoEntity toEntity(Branch branch) {

        BranchDynamoEntity entity = new BranchDynamoEntity();

        entity.setPk("FRANCHISE#" + branch.getFranchiseId());
        entity.setSk("BRANCH#" + branch.getId());

        entity.setId(branch.getId().toString());
        entity.setFranchiseId(branch.getFranchiseId().toString());
        entity.setName(branch.getName());

        return entity;
    }

    public static Branch toDomain(BranchDynamoEntity entity) {

        return new Branch(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getFranchiseId()),
                entity.getName()
        );
    }

    public static Branch toDomain(
            Map<String, AttributeValue> attributes) {

        UUID id = UUID.fromString(
                attributes.get("id").s()
        );

        UUID franchiseId = UUID.fromString(
                attributes.get("franchiseId").s()
        );

        String name = attributes.get("name").s();

        return new Branch(
                id,
                franchiseId,
                name
        );
    }
}
