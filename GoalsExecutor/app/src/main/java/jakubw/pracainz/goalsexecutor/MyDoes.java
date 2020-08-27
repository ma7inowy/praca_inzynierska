package jakubw.pracainz.goalsexecutor;

public class MyDoes {

    String titledoes, datedoes, descdoes, id;
//    Label label;
    String labelName;
    public MyDoes() {
    }

    public MyDoes(String titledoes, String datedoes, String descdoes, String id, String labelName) {
        this.titledoes = titledoes;
        this.datedoes = datedoes;
        this.descdoes = descdoes;
        this.id = id;
        this.labelName = labelName;
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

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
