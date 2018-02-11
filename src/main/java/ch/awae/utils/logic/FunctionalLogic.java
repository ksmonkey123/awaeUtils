package ch.awae.utils.logic;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Functional implementation of the {@link Logic} interface. Allows the
 * evaluation to be determined by an externally provided function
 * 
 * @author Andreas Wälchli
 * 
 * @see Logic
 */
final class FunctionalLogic implements Logic {

    private final BooleanSupplier λάμδα;

    /**
     * Creates a new instance.
     * 
     * @param λ
     *            the function to be evaluated for the logic value
     * @throws NullPointerException
     *             if {@code λ} is {@code null}
     */
    FunctionalLogic(final BooleanSupplier λ) {
        λάμδα = Objects.requireNonNull(λ, "'λ' may not be null!");
    }

    @Override
    public boolean evaluate() {
        return λάμδα.getAsBoolean();
    }

}
