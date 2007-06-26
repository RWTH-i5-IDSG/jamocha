/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 
 * @author Peter Lin
 * 
 * The purpose of DefglobalMap is to centralize the handling of defglobals
 * in a convienant class that can be serialized easily from one engine
 * to another.
 */
public class DefglobalMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * later on we should replace this and have it
	 */
	private Map<String,Object> variables = new HashMap<String,Object>();
	
	public DefglobalMap() {
		super();
	}

	/**
	 * The current implementation doesn't check and simply puts the
	 * new defglobal into the underlying HashMap
	 * @param name
	 * @param value
	 */
	public void declareDefglobal(String name, Object value) {
		this.variables.put(name,value);
	}
	
	/**
	 * The current implementation calls HashMap.get(key). if the key
	 * and value aren't in the HashMap, it returns null.
	 * @param name
	 * @return
	 */
	public Object getValue(String name) {
		return this.variables.get(name);
	}
	
	public Map<String,Object> getDefglobals() {
		return variables;
	}
	
	/**
	 * Convienance method for iterating over the entries in the HashMap
	 * and printing it out. The implementation prints the String key and
	 * calls Object.toString() for the value.
	 * @param engine
	 */
	public void printDefglobals(Rete engine) {
		Iterator itr = this.variables.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			Object val = this.variables.get(key);
			engine.writeMessage(key + "=" + val.toString());
		}
	}
}
