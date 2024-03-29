package MWExtraction.Wrapper;

public class MWE {

    private String text;
    private boolean validated;
    private double measure;

    public MWE(String text, boolean validated, double measure) {
        this.text = text;
        this.validated = validated;
        this.measure = measure;
    }

    @Override
    public String toString() {
        return text + "\t" + validated + "\t" + measure;
    }

    public String getText() {
        return text;
    }

    public double getMeasure() {
        return measure;
    }

    public boolean isValidated() {
        return validated;
    }
}
