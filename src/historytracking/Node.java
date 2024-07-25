package historytracking;

public class Node<T> {
    public T task;
    public Node<T> next;
    public Node<T> prev;

    public Node(T task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }
}