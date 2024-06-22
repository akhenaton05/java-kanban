public class Task {
    private String title;
    private String description;
    private int id;
    private StatusPriority status;

    public Task(String title, String description, StatusPriority status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusPriority getStatus() {
        return status;
    }

    public void setStatus(StatusPriority status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = String.format("Task{title='%s', description='%s', id='%d', status='%s'}", title, description, id, status);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
