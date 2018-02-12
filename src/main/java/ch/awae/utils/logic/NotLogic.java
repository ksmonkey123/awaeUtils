package ch.awae.utils.logic;

import java.util.Objects;

/**
 * Inverted {@link Logic} instance
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 * 
 * @see Logic
 */
final class NotLogic implements Logic {

    private final Logic λάμδα;

    /**
     * Creates a new NotLogic instance from a backing Logic instance
     * 
     * @param backer
     *            the backing instance. May not be {@code null}
     * @throws NullPointerException
     *             if {@code backer} is {@code null}
     */
    public NotLogic(Logic backer) {
        λάμδα = Objects.requireNonNull(backer, "backer may not be null");
    }

    @Override
    public boolean evaluate() {
        return !λάμδα.evaluate();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Returns the backer itself, such that the following performance
     * optimisation is provided:<br/>
     * {@code new NotLogic(a).not() == a}
     * </p>
     */
    @Override
    public Logic not() {
        return λάμδα;
    }

}
