package historytracking;

import tasks.Task;

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
            for (int i = 0; i < 9; i++) {
                Task temp = historyList.get(i + 1);
                historyList.set(i, temp);
            }
            historyList.set(9, task);
        } else {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
