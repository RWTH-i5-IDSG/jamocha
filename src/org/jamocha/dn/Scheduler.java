package org.jamocha.dn;

public interface Scheduler {

	public abstract void enqueue(Runnable runnable);

}