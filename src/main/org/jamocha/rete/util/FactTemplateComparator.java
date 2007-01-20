package org.jamocha.rete.util;

import java.util.Comparator;

import org.jamocha.rete.Fact;

public class FactTemplateComparator implements Comparator {

	public FactTemplateComparator() {
		super();
	}

	public int compare(Object left, Object right) {
		Fact lf = (Fact)left;
		Fact rf = (Fact)right;
		return lf.getDeftemplate().getName().compareTo(
				rf.getDeftemplate().getName());
	}

}
