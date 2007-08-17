package org.jamocha.formatter;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ExpressionSequence;
import org.jamocha.rete.configurations.AssertConfiguration;
import org.jamocha.rete.configurations.DeclarationConfiguration;
import org.jamocha.rete.configurations.DeffunctionConfiguration;
import org.jamocha.rete.configurations.DefmoduleConfiguration;
import org.jamocha.rete.configurations.DefruleConfiguration;
import org.jamocha.rete.configurations.IfElseConfiguration;
import org.jamocha.rete.configurations.LoopForCountConfiguration;
import org.jamocha.rete.configurations.ModifyConfiguration;
import org.jamocha.rete.configurations.Signature;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.configurations.WhileDoConfiguration;
import org.jamocha.rete.functions.Function;
import org.jamocha.rete.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.rete.nodes.joinfilter.RightFieldAddress;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.FunctionAction;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.NotCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

public abstract class Formatter {

	private StringBuilder prefix;

	protected int intendationWidth = 4;

	protected boolean intend = true;

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

	public abstract String visit(ExpressionSequence object);

	public abstract String visit(FunctionAction object);

	public abstract String visit(IfElseConfiguration object);

	public abstract String visit(JamochaValue object);

	public abstract String visit(LeftFieldAddress object);

	public abstract String visit(LiteralConstraint object);

	public abstract String visit(LoopForCountConfiguration object);

	public abstract String visit(ModifyConfiguration object);

	public abstract String visit(NotCondition object);

	public abstract String visit(ObjectCondition object);

	public abstract String visit(RightFieldAddress object);

	public abstract String visit(Rule object);

	public abstract String visit(Signature object);

	public abstract String visit(SlotConfiguration object);

	public abstract String visit(TestCondition object);

	public abstract String visit(WhileDoConfiguration object);

	/*
	 * Here end the visitor methods
	 */

	public int getIntendationWidth() {
		return intendationWidth;
	}

	public void setIntendationWidth(int intendationWidth) {
		this.intendationWidth = intendationWidth;
	}

	public boolean isIntend() {
		return intend;
	}

	public void setIntend(boolean intend) {
		this.intend = intend;
	}

	public boolean isLineBreak() {
		return lineBreak;
	}

	public void setLineBreak(boolean lineBreak) {
		this.lineBreak = lineBreak;
	}

	protected final void increaseIndent() {
		for (int i = 0; i < intendationWidth; ++i) {
			prefix.append(' ');
		}
	}

	protected final void decreaseIndent() {
		prefix.setLength(Math.max(0, prefix.length() - intendationWidth));
	}

	protected final void newLine(StringBuilder sb) {
		if (lineBreak) {
			sb.append(Constants.LINEBREAK);
		}
		if (intend) {
			sb.append(prefix);
		}
		// we need at least a space as separation between two items
		if (!lineBreak && (!intend || prefix.length() < 1)) {
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
