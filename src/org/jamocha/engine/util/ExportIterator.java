/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.util;

import java.util.Iterator;

import org.jamocha.engine.Engine;
import org.jamocha.engine.RetractException;
import org.jamocha.engine.workingmemory.elements.Deffact;

public class ExportIterator implements Iterator<Deffact> {

	int len, act;
	long[] facts;
	Engine engine;

	public ExportIterator(final Engine engine, final long[] factIds) {
		len = factIds.length;
		act = 0;
		this.engine = engine;
		facts = factIds;
	}

	public boolean hasNext() {
		return act < len;
	}

	public Deffact next() {
		return (Deffact) engine.getFactById(facts[act++]);
	}

	public void remove() {
		try {
			engine.retractById(facts[act - 1]);
		} catch (final RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
