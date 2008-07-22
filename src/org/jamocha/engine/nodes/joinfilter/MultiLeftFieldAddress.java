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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Josef Alexander Hahn
 *
 * A MultiLeftFieldAddress is a kind of hack, which is used
 * for or-conditions. in an or-condition, it can happen, that
 * one bound value can be found at different places inside the
 * tuple.
 */
public class MultiLeftFieldAddress extends FieldAddress {
	
	protected List<LeftFieldAddress> alternatives;
	
	@Override
	public Object clone() {
		return this;
	}

	public MultiLeftFieldAddress() {
		alternatives = new ArrayList<LeftFieldAddress>();
	}

	public void addAlternativeField(LeftFieldAddress alt) {
		alternatives.add(alt);
	}

	@Override
	public String toPPString() {
		return getExpressionString();
	}

	public String getExpressionString() {
		final StringBuffer result = new StringBuffer();

		result.append("[[OR:");
		for (LeftFieldAddress alt: alternatives) result.append(alt.getExpressionString());
		result.append("]]");
		
		return result.toString();
	}

	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	@Override
	public JamochaValue getIndexedValue(WorkingMemoryElement wme) throws EvaluationException {
		for (LeftFieldAddress a : alternatives) {
			JamochaValue aval = a.getIndexedValue(wme);
			if (aval != JamochaValue.NIL) return aval;
		}
		return JamochaValue.NIL;
	}

}
