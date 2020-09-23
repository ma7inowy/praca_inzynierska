package jakubw.pracainz.goalsexecutor;

public class NextAction {

    String title, datedoes, description, id;
//    Label label;
    String labelName, priority;

    public NextAction() {
    }

    public NextAction(String title, String datedoes, String description, String id, String labelName, String priority) {
        this.title = title;
        this.datedoes = datedoes;
        this.description = description;
        this.id = id;
        this.labelName = labelName;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDatedoes() {
        return datedoes;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDatedoes(String datedoes) {
        this.datedoes = datedoes;
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
}
