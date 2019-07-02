package semantic;

import java.util.ArrayList;

public class Rule {

    private ArrayList<String> condition;
    private ArrayList<String> object;
    private String correspondingPattern;


    public Rule(ArrayList<String> condition, ArrayList<String> object, String correspondingPattern) {
        this.condition = condition;
        this.object = object;
        this.correspondingPattern = correspondingPattern;
    }

    public ArrayList<String> getCondition() {
        return condition;
    }

    public ArrayList<String> getObject() {
        return object;
    }

    public String getCorrespondingPattern() {
        return correspondingPattern;
    }

    @Override
    public String toString() {
        return "Rule{\n" +
                "\tIF : " + condition +
                "\n\t, THEN : " + object +
                "\n\t, correspondingPattern= '" + correspondingPattern + '\'' +
                "\n}";
    }
}
