package org.jamocha.rete.util;

import java.util.Comparator;

import org.jamocha.rete.Fact;

public class FactComparator implements Comparator {

	public FactComparator() {
		super();
	}

	public int compare(Object left, Object right) {
		Fact lf = (Fact)left;
		Fact rf = (Fact)right;
		if (lf.getFactId() > rf.getFactId()) {
			return 1;
		} else if (lf.getFactId() == rf.getFactId()) {
			return 0;
		} else {
			return -1;
		}
	}

}
