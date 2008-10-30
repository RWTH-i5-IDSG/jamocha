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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MutableValue) {
			MutableValue other = (MutableValue) o;
			return v.equals(other.v);
		} 
		return false;
	}
	
}
