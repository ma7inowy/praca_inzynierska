package jakubw.pracainz.goalsexecutor;

public class MyDoes {

    String titledoes, datedoes, descdoes, id;
    Label label;

    public MyDoes() {
    }

    public MyDoes(String titledoes, String datedoes, String descdoes, String id, Label label) {
        this.titledoes = titledoes;
        this.datedoes = datedoes;
        this.descdoes = descdoes;
        this.id = id;
        this.label = label;
    }

    public String getTitledoes() {
        return titledoes;
    }

    public String getDatedoes() {
        return datedoes;
    }

    public String getDescdoes() {
        return descdoes;
    }

    public void setTitledoes(String titledoes) {
        this.titledoes = titledoes;
    }

    public void setDatedoes(String datedoes) {
        this.datedoes = datedoes;
    }

    public void setDescdoes(String descdoes) {
        this.descdoes = descdoes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
