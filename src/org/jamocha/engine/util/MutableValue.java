package org.jamocha.engine.util;

public class MutableValue<T extends Object> {

	private T v;
	
	public MutableValue(T v) {
		this.v=v;
	}
	
	public T get() {
		return v;
	}
	
	public void set(T v) {
		this.v=v;
	}
	
}
