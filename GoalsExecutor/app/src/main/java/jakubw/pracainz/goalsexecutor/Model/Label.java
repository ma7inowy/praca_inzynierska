package jakubw.pracainz.goalsexecutor.Model;

import androidx.annotation.NonNull;

public class Label {
    private String name;
    private int color;
    private String id;

    public Label(String name, int color, String id) {
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
