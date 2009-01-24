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
import org.jamocha.rules.Rule;

public class ModifyConfiguration extends AbstractConfiguration {

	@Override
	public Object clone() {
		ModifyConfiguration mc = new ModifyConfiguration();
		mc.factBinding = factBinding;
		mc.slots = new SlotConfiguration[slots.length];
		for (int i=0 ; i < slots.length; i++) {
			mc.slots[i] = (SlotConfiguration)( slots[i].clone() );
		}
		return mc;
	}

	private Parameter factBinding = null;

	private SlotConfiguration[] slots = null;

	public boolean isFactBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	public JamochaValue getValue(Engine engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public SlotConfiguration[] getSlots() {
		return slots;
	}

	public void setSlots(SlotConfiguration[] slots) {
		this.slots = slots;
	}

	public Parameter getFactBinding() {
		return factBinding;
	}

	public void setFactBinding(Parameter factBinding) {
		this.factBinding = factBinding;
	}

	@Override
	public void configure(Engine engine, Rule rule) {
		// we need to set the row value if the binding is a slot or fact
		// TODO remove that lines here Binding b1 =
		// rule.getBinding(factBinding.getVariableName());
		// if (b1 != null) {
		// factBinding.setRow(b1.getLeftRow());
		// if (b1.getLeftIndex() == -1) {
		// factBinding.setObjectBinding(true);
		// }
		// }
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
}
