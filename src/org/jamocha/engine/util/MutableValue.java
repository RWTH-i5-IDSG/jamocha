package org.jamocha.engine.util;

public class MutableValue<T extends Object> {

	protected T v;
	
	public MutableValue(T v) {
		this.v=v;
	}
	
	public T get() {
		return v;
	}
	
	public void set(T v) {
		this.v=v;
	}
	
	public MutableValue<T> clone() {
		return new MutableValue<T>(v);
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
