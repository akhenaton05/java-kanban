package historytracking;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList;

    public InMemoryHistoryManager() {
        this.historyList = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (historyList.size() >= 10) {
            historyList.remove(0);
            historyList.add(task);
        } else {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
