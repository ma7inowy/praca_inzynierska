package jakubw.pracainz.goalsexecutor.Model;

import java.util.ArrayList;

public class GroupTask {
    private String title, description, id;
    private String priority;
    private int estimatedTime;
    private ArrayList<String> collaborants;

    public GroupTask() {
    }

    public GroupTask(String title, String description, String id, String priority, int estimatedTime, ArrayList<String> collaborants) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.priority = priority;
        this.estimatedTime = estimatedTime;
        this.collaborants = collaborants;
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

    public ArrayList<String> getCollaborants() {
        return collaborants;
    }

    public void setCollaborants(ArrayList<String> collaborants) {
        this.collaborants = collaborants;
    }
}
