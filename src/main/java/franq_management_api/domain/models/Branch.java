package franq_management_api.domain.models;

import java.util.UUID;

public class Branch {
    private UUID id;
    private UUID franchiseId;
    private String name;

    public Branch(UUID id, UUID franchiseId, String name) {
        this.id = id;
        this.franchiseId = franchiseId;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(UUID franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
