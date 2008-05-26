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

package org.jamocha.engine.nodes.joinfilter;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.engine.Parameter;
import org.jamocha.engine.Engine;

/**
 * @author Josef Alexander Hahn This is the first step for replacing our 101
 *         binding-like classes by a more systematic structure of classes. a
 *         FieldAddress is immutable, because that world sound so good ;)
 */

public abstract class FieldAddress implements Parameter {

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); // abstract class
	}

	public abstract boolean refersWholeFact();

	public abstract int getSlotIndex() throws FieldAddressingException;

	public abstract String toPPString();

	public boolean isObjectBinding() {
		return refersWholeFact();
	}

	public JamochaValue getValue(final Engine engine)
			throws EvaluationException {
		return null;
	}
}
