import historytracking.HistoryManager;
import managing.Managers;
import managing.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ManagersTest {

    @Test
    public void methodsReturnsInitializedObjects() {
        TaskManager tManager = Managers.getDefault();
        Assertions.assertNotNull(tManager);

        HistoryManager hManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(hManager);
    }
}