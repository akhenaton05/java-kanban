import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList;

    public InMemoryHistoryManager() {
        this.historyList = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        if (historyList.size() >= 10) {
            ArrayList<Task> tempHistoryList = new ArrayList<>(10);
            for (int i = 1; i < 10; i++) {
                tempHistoryList.add(historyList.get(i));
            }
            tempHistoryList.add(task);
            historyList = tempHistoryList;
        } else {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
