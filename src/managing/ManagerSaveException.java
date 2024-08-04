package managing;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Exception e) {
        super(message, e);
    }
}
