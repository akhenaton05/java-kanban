package historytracking;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public Map<Integer, Node> historyMap;
    private Node node;
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;


    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
    }

    public Node linkLast(Task task) {
        if (node == null) {
            this.node = new Node(task);
            this.head = node;
            this.tail = node;
            this.size++;
            return this.node;
        } else {
            Node newNode = new Node(task);
            newNode.prev = tail;
            newNode.next = null;
            tail.next = newNode;
            tail = newNode;
            this.size++;
            return newNode;
        }

    }

    public void removeNode(Node node) {
        if (node.equals(head)) {
            Node nextNode = node.next;
            if (nextNode != null) {
                nextNode.prev = null;
            }
            head = nextNode;
            size--;
            return;
        }
        if (node.equals(tail)) {
            Node prevNode = node.prev;
            prevNode.next = null;
            tail = prevNode;
            size--;
            return;
        }
        Node nextNode = node.next;
        Node prevNode = node.prev;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            //Удаляем ключ и значение из Map
            removeNode(historyMap.get(task.getId()));
            historyMap.remove(task.getId());
        }
        historyMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = head;
        //Если список пуст - возврат пустого списка
        if (node == null) {
            List<Task> emptyList = new ArrayList<>();
            return emptyList;
        }
        while (node != tail) {
            result.add((Task) node.task);
            node = node.next;
        }
        result.add(tail.task);
        return result;
    }
}


