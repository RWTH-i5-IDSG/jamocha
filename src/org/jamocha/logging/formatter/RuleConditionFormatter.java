package org.jamocha.logging.formatter;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.RuleCondition;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author "Christoph Terwelp <christoph.terwelp@rwth-aachen.de>"
 */
public class RuleConditionFormatter implements Formatter<RuleCondition> {

	private static final RuleConditionFormatter singleton = new RuleConditionFormatter();

	static public RuleConditionFormatter getRuleConditionFormatter() {
		return singleton;
	}

	static public String formatRC(final RuleCondition re) {
		return getRuleConditionFormatter().format(re);
	}

	static private void lineBreak(final StringBuffer output, final int level) {
		output.append("\n");
		for (int i = 0; i < level; i++) {
			output.append("   ");
		}
	}

	static private void processStatement(final StringCharacterIterator iter, final StringBuffer output, final int level) {
		if (iter.current() != '(')
			throw new Error("Expected '(' but found '" + iter.current() + "'");
		output.append('(');
		boolean lb = false;
		boolean sub = false;
		while (iter.next() != CharacterIterator.DONE) {
			switch (iter.current()) {
			case '(':
				if (!lb)
					lineBreak(output, level + 1);
				processStatement(iter, output, level + 1);
				sub = true;
				lb = false;
				break;
			case ')':
				if (!lb && sub)
					lineBreak(output, level);
				output.append(')');
				return;
			default:
				output.append(iter.current());
				lb = false;
			}
		}
	}

	static public String formatRCindented(final RuleCondition re) {
		final String input = formatRC(re);
		final StringBuffer output = new StringBuffer();
		final StringCharacterIterator iterator = new StringCharacterIterator(input);
		processStatement(iterator, output, 0);
		return output.toString();
	}

	private RuleConditionFormatter() {
	}

	@Override
	public String format(final RuleCondition re) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		final ConditionalElementFormatter cef =
				new ConditionalElementFormatter(new SymbolCollector(re).toSlotVariablesByFactVariable());
		for (final ConditionalElement<SymbolLeaf> ce : re.getConditionalElements()) {
			sb.append(" ");
			sb.append(cef.format(ce));
		}
		sb.append(")");
		return sb.toString();
	}
}
