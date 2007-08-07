/*
 * Copyright 2007 Josef Alexander Hahn, Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.EqualityIndex;
import org.jamocha.rete.Fact;

/**
 * @author Josef Alexander Hahn, Sebastian Reinartz
 * 
 */
public class FactDataContainer extends ModulesDataContainer {

	private long lastFactId = 1;

	/**
	 * We use a HashMap to make it easy to determine if an existing deffact
	 * already exists in the working memory. this is only used for deffacts and
	 * not for objects
	 */
	protected Map<EqualityIndex, Fact> deffactMap = new HashMap<EqualityIndex, Fact>();

	public FactDataContainer() {
		super();
		idToCLIPSElement = new HashMap<Long, Fact>();
	}

	@Override
	protected void handleClear() {
		deffactMap.clear();
		lastFactId = 1;
	}

	public long add(Fact fact) {
		long result = -1;
		// add to map with equalityIndex:
		if (!this.deffactMap.containsKey(fact.equalityIndex())) {
			this.deffactMap.put(fact.equalityIndex(), fact);
			// look at fact ids:
			// does fact contain fact id <>-1 we take a new one:
			if (fact.getFactId() != -1) {
				result = fact.getFactId();
			} else {
				result = lastFactId;
				fact.setFactId(result);
				lastFactId++;
			}
			this.idToCLIPSElement.put(result, fact);
		}
		return result;
	}

	public Fact remove(long factId) {
		Fact result = null;
		if (this.idToCLIPSElement.containsKey(factId)) {
			result = (Fact) idToCLIPSElement.remove(factId);
			deffactMap.remove(result.equalityIndex());
		}
		return result;
	}
	
	public Fact getFactById(long id){
		return (Fact) this.idToCLIPSElement.get(id);
	}

	public List<Fact> getFacts() {
		List<Fact> facts = new ArrayList<Fact>();
		// clearadd all templates from hashmap to resulting list:
		Iterator itr = this.idToCLIPSElement.keySet().iterator();
		while (itr.hasNext()) {
			Object key = itr.next();
			Fact fact = (Fact) this.idToCLIPSElement.get(key);
			facts.add(fact);
		}
		return facts;
	}

	public Fact getFactByFact(Fact fact) {
		return this.deffactMap.get(fact.equalityIndex());
	}

}
