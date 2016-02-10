/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

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
public final class RuleConditionFormatter implements Formatter<RuleCondition> {

    private static final RuleConditionFormatter SINGLETON = new RuleConditionFormatter();

    public static RuleConditionFormatter getRuleConditionFormatter() {
        return SINGLETON;
    }

    public static String formatRC(final RuleCondition re) {
        return getRuleConditionFormatter().format(re);
    }

    private static void lineBreak(final StringBuffer output, final int level) {
        output.append("\n");
        for (int i = 0; i < level; i++) {
            output.append("   ");
        }
    }

    private static void processStatement(final StringCharacterIterator iter, final StringBuffer output,
            final int level) {
        if (iter.current() != '(') throw new Error("Expected '(' but found '" + iter.current() + "'");
        output.append('(');
        boolean lb = false;
        boolean sub = false;
        while (iter.next() != CharacterIterator.DONE) {
            switch (iter.current()) {
            case '(':
                if (!lb) lineBreak(output, level + 1);
                processStatement(iter, output, level + 1);
                sub = true;
                lb = false;
                break;
            case ')':
                if (!lb && sub) lineBreak(output, level);
                output.append(')');
                return;
            default:
                output.append(iter.current());
                lb = false;
            }
        }
    }

    public static String formatRCindented(final RuleCondition re) {
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
