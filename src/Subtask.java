public class Subtask extends Task{
    private int epicId;


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", epicId=" + epicId +
                ", status=" + this.getStatus() +
                '}';
    }
}
