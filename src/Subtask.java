public class Subtask extends Task{
    private int epicId;

    public Subtask(String title, String description, StatusPriority status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String result = String.format("Subtask{description='%s', id='%d', epicId='%d', status='%s'}", this.getDescription(), this.getId(), epicId, this.getStatus());
        return result;
    }
}
