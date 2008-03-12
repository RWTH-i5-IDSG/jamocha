package org.jamocha.rete.util;

import java.util.Comparator;

import org.jamocha.rete.wme.Fact;

public class FactTemplateComparator implements Comparator<Fact> {

	public FactTemplateComparator() {
		super();
	}

	public int compare(Fact lf, Fact rf) {
		return lf.getTemplate().getName().compareTo(
				rf.getTemplate().getName());
	}

}
