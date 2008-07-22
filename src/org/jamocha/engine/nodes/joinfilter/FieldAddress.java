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

package org.jamocha.engine.nodes.joinfilter;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn 
 * 
 * This is the first step for replacing our 101
 * binding-like classes by a more systematic structure of classes. a
 * FieldAddress is immutable, because that word sound so good ;)
 */

public abstract class FieldAddress implements Parameter {

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); // abstract class
	}

	public abstract String toPPString();

	/**
	 * for a FieldAddess, this method ALWAYS RETURNS FALSE!
	 * the class cannot determine, whether it refers to a
	 * fact or just a slot.
	 */
	public boolean isFactBinding() {
		return false;
	}

	public JamochaValue getValue(Engine e) throws EvaluationException{
		return null;
	}
	
	public abstract JamochaValue getIndexedValue(final WorkingMemoryElement wme) throws EvaluationException;
}
