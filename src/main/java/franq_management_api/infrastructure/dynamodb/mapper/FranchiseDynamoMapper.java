package franq_management_api.infrastructure.dynamodb.mapper;

import franq_management_api.domain.models.Franchise;
import franq_management_api.infrastructure.dynamodb.entity.FranchiseDynamoEntity;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

public class FranchiseDynamoMapper {
    public static FranchiseDynamoEntity toEntity(Franchise franchise) {

        FranchiseDynamoEntity entity = new FranchiseDynamoEntity();

        entity.setPk("FRANCHISE#" + franchise.getId());
        entity.setSk("METADATA");
        entity.setId(franchise.getId().toString());
        entity.setName(franchise.getName());

        return entity;
    }

    public static Franchise toDomain(FranchiseDynamoEntity entity) {

        return new Franchise(
                java.util.UUID.fromString(entity.getId()),
                entity.getName()
        );
    }

    public static Franchise toDomain(
            Map<String, AttributeValue> attributes) {

        UUID id = UUID.fromString(
                attributes.get("id").s()
        );

        String name = attributes.get("name").s();

        return new Franchise(
                id,
                name
        );
    }
}
