import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void methodsReturnsInitializedObjects() {
        TaskManager tManager = Managers.getDefault();
        Assertions.assertNotNull(tManager);

        HistoryManager hManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(hManager);
    }
}