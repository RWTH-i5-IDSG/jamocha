package org.jamocha.rete.util;

import java.util.Comparator;

import org.jamocha.rete.wme.Fact;

public class FactComparator implements Comparator<Fact> {

	public FactComparator() {
		super();
	}

	public int compare(Fact lf, Fact rf) {
		if (lf.getFactId() > rf.getFactId()) {
			return 1;
		} else if (lf.getFactId() == rf.getFactId()) {
			return 0;
		} else {
			return -1;
		}
	}

}
