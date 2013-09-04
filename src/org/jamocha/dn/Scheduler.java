package org.jamocha.dn;

/**
 * This interface declares a scheduler usable by any {@link Network network}. It should take
 * {@link Runnable Runnables} and {@link Runnable#run() run} them (optional in separate threads).
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * 
 * @see Runnable
 */
public interface Scheduler {

	/**
	 * Add a {@link Runnable} to be processed by the scheduler.
	 * 
	 * @param runnable
	 *            the {@link Runnable} to add to the schedulers queue
	 */
	public abstract void enqueue(Runnable runnable);

}