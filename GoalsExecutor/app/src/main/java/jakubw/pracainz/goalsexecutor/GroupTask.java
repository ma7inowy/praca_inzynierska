package jakubw.pracainz.goalsexecutor;

public class GroupTask {
    String title, description, id;
    String priority;
    int estimatedTime;

    public GroupTask() {
    }

    public GroupTask(String title, String description, String id, String priority, int estimatedTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.priority = priority;
        this.estimatedTime = estimatedTime;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
