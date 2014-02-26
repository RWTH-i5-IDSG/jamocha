package org.jamocha.dn.memory;

import org.jamocha.filter.Filter.FilterElement;

public interface FilterElementToCounterColumn {
	public CounterColumn getCounterColumn(final FilterElement filterElement);
}