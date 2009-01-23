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

package org.jamocha.communication.jsr94.internal;

import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> Classes implementing
 *         this interface help translating between Jamocha-facts <->
 *         Java-objects and Jamocha-templates <-> Java-classes
 */
public interface JavaClassAdaptor {

	public List<String> getFields(Class<? extends Object> c)
			throws Template2JavaClassAdaptorException;

	public Object getFieldValue(String field, Object o)
			throws Template2JavaClassAdaptorException;

	/**
	 * this method can fill the values into the parameter 'o' or can
	 * return a new object. the caller has to handle both ways!
	 */
	public Object storeToObject(Fact f, Object o, Engine engine)
			throws Template2JavaClassAdaptorException;

	public Fact getFactFromObject(Object o, Engine engine)
			throws Template2JavaClassAdaptorException;

}
