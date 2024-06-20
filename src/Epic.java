import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Integer> subtasks = new ArrayList<>();
    private boolean haveSubtasks = false;

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
        haveSubtasks = true;
    }

    public boolean isHaveSubtasks() {
        return haveSubtasks;
    }

    public void setHaveSubtasks(boolean haveSubtasks) {
        this.haveSubtasks = haveSubtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status=" + this.getStatus() +
                ", haveSubtasks=" + haveSubtasks +
                '}';
    }
}
