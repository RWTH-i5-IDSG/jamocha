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

import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.EqualityIndex;
import org.jamocha.rete.Fact;
import org.jamocha.rule.Rule;

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
	
	
	
	public long add(Fact fact){
		long result =-1;
		//add to map with equalityIndex:
		if (!this.deffactMap.containsKey(fact.equalityIndex())){
		this.deffactMap.put(fact.equalityIndex(), fact);
		this.idToCLIPSElement.put(lastFactId, fact);
		fact.setFactId(lastFactId);
		result = lastFactId;
		lastFactId++;
		}
		return result;
	}
	
	public Fact remove(long factId){
		Fact result = null;
		if (this.idToCLIPSElement.containsKey(factId)){
			result = (Fact)idToCLIPSElement.remove(factId);
			deffactMap.remove(result.equalityIndex());
		}
		return result;
	}
	
	
}
