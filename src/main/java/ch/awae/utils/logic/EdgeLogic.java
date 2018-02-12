package ch.awae.utils.logic;

/**
 * Rising Edge Logic
 * 
 * Only evaluates to {@code true} the first time the base {@link Logic}
 * evaluates to {@code true} after previously evaluating to {@code false}.
 * This emulates edge triggering.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
final class EdgeLogic implements Logic {

    private final Logic base;

    private boolean active = false;

    public EdgeLogic(Logic base) {
        this.base = base;
    }

    @Override
    public boolean evaluate() {
        boolean next = base.evaluate();
        if (active) {
            // currently active - ignore
            active = next;
            return false;
        } else if (next) {
            // currently inactive and activating - trigger
            active = true;
            return true;
        } else {
            // currently inactive and not activating - ignore
            return false;
        }
    }

}
