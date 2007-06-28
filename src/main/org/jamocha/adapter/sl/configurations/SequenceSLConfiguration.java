/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
		if (compileType.equals(SLCompileType.RULE_RESULT)) {
			res.append("(create$");
			for (SLConfiguration conf : items) {
				res.append(" ");
				res.append(conf.compile(compileType));
			}
			res.append(")");
		} else {
			boolean first = true;
			for (SLConfiguration conf : items) {
				if (!first)
					res.append(" ");
				res.append(conf.compile(compileType));
				first = false;
			}
		}
		return res.toString();
	}

}
