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

package org.jamocha.engine.configurations;

import org.jamocha.engine.Engine;
import org.jamocha.engine.Parameter;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Karl-Heinz Krempels, Sebastian Reinartz
 * 
 */
public class SlotConfiguration extends AbstractConfiguration {

	private String slotName = null;

	/**
	 * the id of the slot
	 */
	private int id;

	private Parameter[] slotValues = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.rete.Parameter#isObjectBinding()
	 */
	public boolean isFactBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public SlotConfiguration clone() {
		SlotConfiguration sc = new SlotConfiguration(slotName,id, slotValues.clone());
		return sc;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getExpressionString()
	 */
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jamocha.parser.Expression#getValue(org.jamocha.rete.Rete)
	 */
	public JamochaValue getValue(Engine engine) throws EvaluationException {
		// multislot:
		if (isMultislot()) {
			JamochaValue[] jvs = new JamochaValue[slotValues.length];
			for (int i = 0; i < slotValues.length; i++)
				jvs[i] = slotValues[i].getValue(engine);
			return JamochaValue.newList(jvs);

			// single slot:
		} else if (slotValues.length > 0)
			return slotValues[0].getValue(engine);
		else
			return JamochaValue.NIL;

	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public Parameter[] getSlotValues() {
		return slotValues;
	}

	public void setSlotValues(Parameter[] slotValues) {
		this.slotValues = slotValues;
	}

	public Boolean isMultislot() {
		// count values:
		Boolean result = slotValues.length > 1;
		// if values =1 we might have a boundvariable and this can bind a list:
		// we have to check:
		// if (!result && slotValues.length == 1){
		// if (slotValues[0] instanceof BoundParam){
		// result = ((BoundParam)slotValues[0]).isMultislot();
		// }
		// }
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public SlotConfiguration(String slotName, int id, Parameter[] slotValues) {
		super();
		this.slotName = slotName;
		this.id = id;
		this.slotValues = slotValues;
	}

	public SlotConfiguration() {
	}

}
