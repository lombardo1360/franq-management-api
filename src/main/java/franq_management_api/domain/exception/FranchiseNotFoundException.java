package franq_management_api.domain.exception;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException(String message) {
        super(message);
    }
}
