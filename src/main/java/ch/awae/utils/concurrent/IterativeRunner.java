package ch.awae.utils.concurrent;

/**
 * This prebuilt Runnable implementation allows for a very simple
 * implementation. It consists of an infinite loop with a user provided step
 * implementation. Before the first step a start handler is called.According to
 * indication of the step implementation the base loop can be terminated. If the
 * step yields an interrupt, the loop is terminated as well. After loop
 * termination a termination handling method is called, after an interrupt
 * handler has been run in case of an interrupt being the reason for the loop
 * termination.
 *
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
public abstract class IterativeRunner implements Runnable {

	@Override
	public final void run() {
		this.onStart();
		Thread owner = Thread.currentThread();
		interruptBox : {
			loop : while (!owner.isInterrupted()) {
				try {
					if (this.step())
						continue;
					break interruptBox;
				}
				catch (InterruptedException e) {
					break loop;
				}
			}
			this.onInterrupt();
		}
		this.onTerminate();
	}

	/**
	 * This method is invoked once each iteration step.
	 *
	 * @return {@code true} if the iteration should proceed, {@code false} if it
	 *         should terminate.
	 * @throws InterruptedException
	 *             if the iteration was interrupted and should be terminated
	 *             using the interrupt shutdown procedure
	 */
	protected abstract boolean step() throws InterruptedException;

	/**
	 * This method is invoked if an interrupt is detected. The implementation of
	 * must always return normally.
	 */
	protected void onInterrupt() {
		// default: no action
	}

	/**
	 * This method is invoked whenever the iteration is terminated.
	 *
	 * This allows for the implementation of cleanup code. If the termination
	 * was due to an interrupt, {@link #onInterrupt()} will be invoked before
	 * this method.
	 */
	protected void onTerminate() {
		// default: no action
	}

	/**
	 * This method is invoked before the first iteration step.
	 *
	 * This allows for the implementation of setup code.
	 */
	protected void onStart() {
		// default: no action
	}

}
