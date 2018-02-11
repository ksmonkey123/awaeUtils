package ch.awae.utils.logic;

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
