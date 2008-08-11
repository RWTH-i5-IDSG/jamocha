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

package org.jamocha.engine.workingmemory.elements;

import org.jamocha.engine.BoundParam;
import org.jamocha.engine.ConversionUtils;
import org.jamocha.parser.JamochaType;

public class TemporalDeffact extends Deffact implements TemporalFact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected long expirationTime = 0;

	protected String sourceURL = null;

	protected String serviceType = null;

	protected int validity;

	public TemporalDeffact(final Template template, final Slot[] values,
			final long id) {
		super(template, values);
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public String getServiceType() {
		return serviceType;
	}

	public String getSource() {
		return sourceURL;
	}

	public int getValidity() {
		return validity;
	}

	public void setExpirationTime(final long time) {
		expirationTime = time;
	}

	public void setServiceType(final String type) {
		serviceType = type;
	}

	public void setSource(final String url) {
		sourceURL = url;
	}

	public void setValidity(final int valid) {
		validity = valid;
	}

	public String toFactString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("f-" + id + " (" + template.getName());
		if (slots.length > 0)
			buf.append(" ");
		for (int idx = 0; idx < slots.length; idx++)
			buf.append("(" + slots[idx].getName() + " "
					+ ConversionUtils.formatSlot(slots[idx].value) + ") ");
		// append the temporal attributes
		buf.append("(" + TemporalFact.EXPIRATION + " " + expirationTime + ")");
		buf.append("(" + TemporalFact.SERVICE_TYPE + " " + serviceType + ")");
		buf.append("(" + TemporalFact.SOURCE + " " + sourceURL + ")");
		buf.append("(" + TemporalFact.VALIDITY + " " + validity + ")");
		buf.append(")");
		return buf.toString();
	}

	/**
	 * the class overrides the method to include the additional attributes.
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append("(" + template.getName());
		if (slots.length > 0)
			buf.append(" ");
		for (int idx = 0; idx < slots.length; idx++)
			if (slots[idx].value.getType().equals(JamochaType.BINDING)) {
				final BoundParam bp = (BoundParam) slots[idx].value
						.getObjectValue();
				buf.append("(" + slots[idx].getName() + " ?"
						+ bp.getVariableName() + ") ");
			} else
				buf.append("(" + slots[idx].getName() + " "
						+ ConversionUtils.formatSlot(slots[idx].value) + ") ");
		// append the temporal attributes
		buf.append("(" + TemporalFact.EXPIRATION + " " + expirationTime + ")");
		buf.append("(" + TemporalFact.SERVICE_TYPE + " " + serviceType + ")");
		buf.append("(" + TemporalFact.SOURCE + " " + sourceURL + ")");
		buf.append("(" + TemporalFact.VALIDITY + " " + validity + ") ");
		buf.append(")");
		return buf.toString();
	}
}
