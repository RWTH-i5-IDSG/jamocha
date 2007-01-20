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
package org.jamocha.rete.util;

/**
 * @author Peter Lin
 * 
 * Why make a map interface that looks identicle to java.util.Map?
 * The reason is to make it easier to write an optimized HashMap, similar
 * to what Mark Proctor did for Drools3. By having an interface, it makes
 * it easier to wrap distributed data grid products like JCache and
 * JavaSpaces.
 */
public interface Map {

	int size();

	boolean isEmpty();

	boolean containsKey(Object key);

	Object get(Object key);

	Object put(Object arg0, Object arg1);

	Object remove(Object key);

	void clear();

	Iterator keyIterator();
	
}
