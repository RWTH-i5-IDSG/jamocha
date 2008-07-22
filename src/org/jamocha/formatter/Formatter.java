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

package org.jamocha.formatter;

import org.jamocha.Constants;
import org.jamocha.engine.BoundParam;
import org.jamocha.engine.ExpressionList;
import org.jamocha.engine.ExpressionSequence;
import org.jamocha.engine.configurations.AssertConfiguration;
import org.jamocha.engine.configurations.DeclarationConfiguration;
import org.jamocha.engine.configurations.DeffunctionConfiguration;
import org.jamocha.engine.configurations.DefmoduleConfiguration;
import org.jamocha.engine.configurations.DefruleConfiguration;
import org.jamocha.engine.configurations.IfElseConfiguration;
import org.jamocha.engine.configurations.LoopForCountConfiguration;
import org.jamocha.engine.configurations.ModifyConfiguration;
import org.jamocha.engine.configurations.Signature;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.configurations.WhileDoConfiguration;
import org.jamocha.engine.functions.Function;
import org.jamocha.engine.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.engine.nodes.joinfilter.RightFieldAddress;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.MultiSlot;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.FunctionAction;
import org.jamocha.rules.LiteralConstraint;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrderedFactConstraint;
import org.jamocha.rules.Rule;
import org.jamocha.rules.TestCondition;

public abstract class Formatter {

	private StringBuilder prefix;

	protected int indentationWidth = 4;

	protected boolean indent = true;

	protected boolean lineBreak = true;

	public Formatter() {
		prefix = new StringBuilder();
	}

	public String visit(Formattable object) {
		return "Formatting for " + object.getClass().getName()
				+ " not implemented.";
	}

	/*
	 * Here start the visitor methods
	 */

	public abstract String visit(Function object);

	public abstract String visit(AssertConfiguration object);
	
	public abstract String visit(BoundConstraint object);
	
	public abstract String visit(BoundParam object);

	public abstract String visit(DeclarationConfiguration object);

	public abstract String visit(DeffunctionConfiguration object);

	public abstract String visit(DefmoduleConfiguration object);

	public abstract String visit(DefruleConfiguration object);

	public abstract String visit(Deftemplate object);

	public abstract String visit(ExpressionList object);

	public abstract String visit(ExpressionSequence object);

	public abstract String visit(Fact object);

	public abstract String visit(FunctionAction object);

	public abstract String visit(IfElseConfiguration object);

	public abstract String visit(JamochaValue object);

	public abstract String visit(LeftFieldAddress object);

	public abstract String visit(LiteralConstraint object);

	public abstract String visit(LoopForCountConfiguration object);

	public abstract String visit(ModifyConfiguration object);

	public abstract String visit(MultiSlot object);

	public abstract String visit(NotExistsCondition object);

	public abstract String visit(ObjectCondition object);

	public abstract String visit(OrderedFactConstraint object);

	public abstract String visit(RightFieldAddress object);

	public abstract String visit(Rule object);

	public abstract String visit(Signature object);

	public abstract String visit(Slot object);

	public abstract String visit(SlotConfiguration object);

	public abstract String visit(TestCondition object);

	public abstract String visit(TemplateSlot object);

	public abstract String visit(WhileDoConfiguration object);

	/*
	 * Here end the visitor methods
	 */

	public int getIndentationWidth() {
		return indentationWidth;
	}

	public void setIndentationWidth(int indentationWidth) {
		this.indentationWidth = indentationWidth;
	}

	public boolean isIndent() {
		return indent;
	}

	public void setIndent(boolean indent) {
		this.indent = indent;
	}

	public boolean isLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(boolean lineBreak) {
		this.lineBreak = lineBreak;
	}

	protected final void increaseIndent() {
		for (int i = 0; i < indentationWidth; ++i) {
			prefix.append(' ');
		}
	}

	protected final void decreaseIndent() {
		prefix.setLength(Math.max(0, prefix.length() - indentationWidth));
	}

	protected final void newLine(StringBuilder sb) {
		if (lineBreak) {
			sb.append(Constants.LINEBREAK);
		}
		if (indent) {
			sb.append(prefix);
		}
		// we need at least a space as separation between two items
		if (!lineBreak && (!indent || prefix.length() < 1)) {
			sb.append(" ");
		}
	}

	protected String fillToFixedLength(int val, String fill, int length) {
		String res = String.valueOf(val);
		while (res.length() < length)
			res = fill + res;
		return res;
	}
}
