/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.EqualityIndex;
import org.jamocha.engine.workingmemory.elements.Fact;

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

	public synchronized long add(final Fact fact) {
		long result = -1;
		if (!deffactMap.containsKey(fact.equalityIndex())) {
			// Here the fact doesn't exist yet. So we add it to the map with its
			// equalityIndex and eventually give it a new fact-id.
			deffactMap.put(fact.equalityIndex(), fact);
			if (fact.getFactId() == -1) {
				result = lastFactId;
				fact.setFactId(result);
				lastFactId++;
			}
			idToCLIPSElement.put(fact.getFactId(), fact);
		} else {
			// Here the fact already exists with the same equalityIndex (i.e.
			// the same slot values). So we don't give it a new fact id but use
			// the existing one.
			final Fact existingFact = deffactMap.get(fact.equalityIndex());
			fact.setFactId(existingFact.getFactId());
		}
		result = fact.getFactId();
		return result;
	}

	public synchronized Fact remove(final long factId) {
		Fact result = null;
		if (idToCLIPSElement.containsKey(factId)) {
			result = (Fact) idToCLIPSElement.remove(factId);
			deffactMap.remove(result.equalityIndex());
		}
		return result;
	}

	protected Fact getFactById(final long id) {
		return (Fact) idToCLIPSElement.get(id);
	}

	public synchronized List<Fact> getFacts() {
		final List<Fact> facts = new ArrayList<Fact>();
		// clearadd all templates from hashmap to resulting list:
		for (final Object key : idToCLIPSElement.keySet()) {
			final Fact fact = (Fact) idToCLIPSElement.get(key);
			facts.add(fact);
		}
		return facts;
	}

	protected Fact getFactByFact(final Fact fact) {
		return deffactMap.get(fact.equalityIndex());
	}

}
