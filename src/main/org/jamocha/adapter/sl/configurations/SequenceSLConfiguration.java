package org.jamocha.adapter.sl.configurations;

import java.util.LinkedList;
import java.util.List;

public class SequenceSLConfiguration implements SLConfiguration {

	private List<SLConfiguration> items = new LinkedList<SLConfiguration>();

	public void addItem(SLConfiguration item) {
		items.add(item);
	}

	public List<SLConfiguration> getItems() {
		return items;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		boolean first = true;
		for (SLConfiguration conf : items) {
			if (!first)
				res.append(" ");
			res.append(conf.compile(compileType));
			first = false;
		}
		return res.toString();
	}

}
