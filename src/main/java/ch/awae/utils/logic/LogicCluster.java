package ch.awae.utils.logic;

public final class LogicCluster {

    private Logic[] elements;

    public LogicCluster(Logic... elements) {
        this.elements = elements;
        if (elements.length > 32)
            throw new IllegalArgumentException("only 32 elements are supported!");
    }

    public int evaluate() {
        int Σ = 0;
        for (int i = 0; i < elements.length; i++) {
            Logic λάμδα = elements[i];
            if (λάμδα != null && λάμδα.evaluate())
                Σ += (1 << i);
        }
        return Σ;
    }

}
