import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;
    private boolean haveSubtasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        this.haveSubtasks = false;
    }

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
        String result = String.format("Epic{title='%s', description='%s', id='%d', status='%s', haveSubtasks='%b'}", this.getTitle(), this.getDescription(), this.getId(), this.getStatus(), haveSubtasks);
        return result;
    }
}
