package ch.awae.utils.logic;

/**
 * A LogicCluster allows the simultaneous evaluation and handling of up to 32
 * {@link Logic} instances.
 * 
 * When a cluster is evaluated all its {@link Logic} elements are evaluated and
 * the cluster evaluation returns a 32-bit integer representing the result of
 * all the elements. A '1' bit represents a {@link Logic} instance that
 * evaluated to {@code true}, a '0' bit represents one that evaluated to
 * {@code false}. The returned bit pattern can then be easily processed using a
 * {@code switch} table.
 * 
 * The first element controls the 1s place, the second element the 2s place and
 * so on. In general the {@code n}-nth element controls the {@code 2^n} place.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public final class LogicCluster {

    private Logic[] elements;

    /**
     * Creates a new cluster instance from a list of {@link Logic} elements.
     * 
     * @param elements
     *            the elements to build a cluster from. may not be null
     * 
     * @throws NullPointerException
     *             the elements parameter is null
     * @throws IllegalArgumentException
     *             the elements array is empty or contains more than 32 elements
     */
    public LogicCluster(Logic... elements) {
        if (elements == null)
            throw new NullPointerException("elements array may not be null!");
        if (elements.length < 1)
            throw new IllegalArgumentException("at least 1 element is required!");
        if (elements.length > 32)
            throw new IllegalArgumentException("only 32 elements are supported!");
        this.elements = elements;
    }

    /**
     * Evaluates the cluster.
     * 
     * @return a 32-bit integer representation of the evaluation results for all
     *         elements. Bits representing non-existing logics instances (i.e.
     *         the bits above the last element) always evaluate to a constant
     *         {@code 0}.
     */
    public int evaluate() {
        int Σ = 0;
        for (int i = 0; i < elements.length; i++) {
            Logic λάμδα = elements[i];
            if (λάμδα != null && λάμδα.evaluate())
                Σ += (1 << i);
        }
        return Σ;
    }

    /**
     * provides a {@code Logic} instance testing for a specific evaluation
     * pattern. Whenever the cluster evaluates to that given pattern the
     * {@link Logic} instance evaluates to {@code true}.
     * 
     * @param code
     *            the 32-bit pattern that must be present for the logic instance
     *            to evaluate to {@code true}
     * @return a logic instance
     * @since awaeUtils 0.0.7
     */
    public Logic pattern(int code) {
        return () -> evaluate() == code;
    }

}
