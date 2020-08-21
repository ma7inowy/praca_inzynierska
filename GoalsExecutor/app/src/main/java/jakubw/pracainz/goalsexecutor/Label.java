package jakubw.pracainz.goalsexecutor;

public class Label {
    String name;
    String color;
    String id;

    public Label(String name, String color, String id) {
        this.name = name;
        this.color = color;
        this.id = id;
    }

    public Label() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
