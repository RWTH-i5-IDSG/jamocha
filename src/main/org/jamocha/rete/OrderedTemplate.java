/*
 * Copyright 2007 Sebastian Reinartz, Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import org.jamocha.Constants;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;

/**
 * @author Sebastian Reinartz, Alexander Wilden
 */
public class OrderedTemplate implements Template {

	private static final long serialVersionUID = 1L;

	private TemplateSlot data;

	private String templateName = null;

	private boolean watch = false;

	public OrderedTemplate(String name) {
		this.templateName = name;
		this.data = new TemplateSlot("__data");
		this.data.setMultiSlot(true);

	}

	public Fact createFact(Object data, Rete engine) throws EvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateSlot[] getAllSlots() {
		TemplateSlot[] res = new TemplateSlot[1];
		res[0] = data;
		return res;
	}

	public String getClassName() {
		return null;
	}

	public String getName() {
		return this.templateName;
	}

	public int getNumberOfSlots() {
		return 1;
	}

	public Template getParent() {
		return null;
	}

	public TemplateSlot getSlot(String name) {
		if (name.equals(data.getName()))
			return data;
		return null;
	}

	public TemplateSlot getSlot(int column) {
		if (column == 0)
			return data;
		return null;
	}

	public boolean getWatch() {
		return watch;
	}

	public boolean inUse() {
		if (data.getNodeCount() > 0)
			return true;
		return false;
	}

	public void setParent(Template parent) {
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.templateName + Constants.LINEBREAK);
		buf.append("  (" + data.getName() + " (type " + data.getValueType() + ") )" + Constants.LINEBREAK);
		buf.append(")");
		return buf.toString();
	}

	public String getDump(String modName) {
		return toPPString();
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getDescription() {
		return "(implied)";
	}

}
