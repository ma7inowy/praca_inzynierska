package jakubw.pracainz.goalsexecutor;

public class NextAction {

    String title, description, id;
//    Label label;
    String labelName, priority;
    int estimatedTime;

    public NextAction() {
    }

    public NextAction(String title, String description, String id, String labelName, String priority, int estimatedTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.labelName = labelName;
        this.priority = priority;
        this.estimatedTime = estimatedTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
