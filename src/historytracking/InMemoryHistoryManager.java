package historytracking;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> historyMap;
    private Node<Task> head;
    private Node<Task> tail;


    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    private Node linkLast(Task task) {
        Node node = new Node(task);
        if (head == null) {
            this.head = node;
            this.tail = node;
            return node;
        }
        node.prev = tail;
        tail.next = node;
        tail = node;
        return node;
    }

    private void removeNode(Node node) {
        if (historyMap.containsValue(node)) {
            if (node.equals(head)) {
                if (node.equals(tail)) {
                    this.head = null;
                    this.tail = null;
                } else {
                    head = node.next;
                    head.prev = null;
                }
            } else if (node.equals(tail)) {
                tail = node.prev;
                tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            Task task = (Task) node.task;
            historyMap.remove(task.getId());
        }
    }

    @Override
    public void add(Task task) {
        //Проверка наличия таска а истории
        removeNode(historyMap.get(task.getId()));
        historyMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = head;
        while (node != null) {
            result.add((Task) node.task);
            node = node.next;
        }
        return result;
    }


    private static class Node<Task> {
        public Task task;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Task task) {
            this.task = task;
            this.next = null;
            this.prev = null;
        }
    }
}




