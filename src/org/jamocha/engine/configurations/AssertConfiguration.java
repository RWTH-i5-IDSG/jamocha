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
import org.jamocha.engine.TemporalValidity;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class AssertConfiguration extends AbstractConfiguration {

	private String templateName = null;

	private Parameter[] data = null;
	
	private TemporalValidityConfiguration temporalValidityConf = null;
	
	private TemporalValidity temporalValidity = null;

	public TemporalValidity getTemporalValidity() {
		return temporalValidity;
	}

	public void setTemporalValidity(TemporalValidity temporalValidity) {
		this.temporalValidity = temporalValidity;
	}

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

	public SlotConfiguration[] getSlotConfigurations()
			throws EvaluationException {
		SlotConfiguration[] slots = new SlotConfiguration[data.length];
		Signature sig;
		for (int i = 0; i < data.length; i++) {

			if (!(data[i] instanceof Signature))
				throw new EvaluationException(
						"wrong syntax for assert of unordered fact");
			sig = (Signature) data[i];
			slots[i] = new SlotConfiguration(sig.signatureName, i, sig
					.getParameters());
		}
		return slots;

	}

	public Parameter[] getData() {
		return data;
	}

	public void setData(Parameter[] data) {
		this.data = data;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public TemporalValidityConfiguration getTemporalValidityConfiguration() {
		return temporalValidityConf;
	}

	public void setTemporalValidityConfiguration(TemporalValidityConfiguration temporalValidity) {
		this.temporalValidityConf = temporalValidity;
	}

}
