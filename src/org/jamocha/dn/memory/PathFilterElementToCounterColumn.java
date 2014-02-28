package org.jamocha.dn.memory;

import org.jamocha.filter.PathFilter.PathFilterElement;

public interface PathFilterElementToCounterColumn {
	public CounterColumn getCounterColumn(final PathFilterElement filterElement);
}