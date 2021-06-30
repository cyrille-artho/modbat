package modbat.graph.testrequirements;

public class AbstractTestRequirement implements TestRequirement {
    private boolean covered;

    protected AbstractTestRequirement() {
        this.covered = false;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }
}
