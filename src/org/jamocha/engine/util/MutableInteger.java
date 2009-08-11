package org.jamocha.engine.util;

public class MutableInteger extends MutableValue<Integer> {

	public MutableInteger(Integer v) {
		super(v);
	}
	
	public MutableInteger clone() {
		return new MutableInteger(v);
	}

}
