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
package test.jamocha.dn.compiler;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.compiler.ShallowFactVariableCollector;
import org.jamocha.filter.Path;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.clips.parser.SFPToCETranslator;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.Warning;
import org.junit.Test;
import test.jamocha.util.NetworkMockup;

import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.*;
import static org.hamcrest.Matchers.*;
import static org.jamocha.util.ToArray.toArray;
import static org.junit.Assert.*;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class FactVariableCollectorTest {

    private static String ruleName = "rule1";
    private static String templateName = "templ1";
    private static String template2Name = "templ2";
    private static String template3Name = "templ3";
    private static String slot1Name = "slot1";

    private static String templateString =
            "(deftemplate " + templateName + " (slot " + slot1Name + " (type INTEGER)))\n";
    private static String template2String =
            "(deftemplate " + template2Name + " (slot " + slot1Name + " (type INTEGER)))\n";
    private static String template3String =
            "(deftemplate " + template3Name + " (slot " + slot1Name + " (type INTEGER)))\n";
    private static String preRule = "(defrule " + ruleName;
    private static String postRule = "=> )\n";

    private static Queue<Warning> run(final SFPParser parser, final SFPToCETranslator visitor) throws ParseException {
        while (true) {
            final SFPStart n = parser.Start();
            if (n == null) return visitor.getWarnings();
            n.jjtAccept(visitor, null);
        }
    }

    private static VariableSymbol getSymbol(final RuleCondition condition, final String image) {
        final VariableSymbol[] array =
                toArray(new SymbolCollector(condition).getSymbols().stream().filter(s -> s.getImage().equals(image)),
                        VariableSymbol[]::new);
        assertEquals(1, array.length);
        return array[0];
    }

    private static RuleCondition clipsToCondition(final NetworkMockup ptn, final String condition)
            throws ParseException {
        final StringReader parserInput = new StringReader(
                new StringJoiner(" ").add(templateString).add(template2String).add(template3String).add(preRule)
                        .add(condition).add(postRule).toString());
        final SFPParser parser = new SFPParser(parserInput);
        final SFPToCETranslator visitor = new SFPToCETranslator(ptn, ptn);
        run(parser, visitor);
        final Defrule rule = ptn.getRule(ruleName);
        return rule.getCondition();
    }

    @Test
    public void simpleTest() throws ParseException {
        final String input = "(and (" + templateName + " (" + slot1Name + " ?x)) (test (> ?x 10)) (test (< ?x 15)))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertEquals(1, conditionalElements.size());
        final Map<EquivalenceClass, Path> ec2Path =
                ShallowFactVariableCollector.generatePaths(ptn.getInitialFactTemplate(), conditionalElements.get(0))
                        .getRight();
        assertEquals(1, ec2Path.size());
        final Entry<EquivalenceClass, Path> ecAndPath = ec2Path.entrySet().iterator().next();
        final EquivalenceClass ec = ecAndPath.getKey();
        final Path path = ecAndPath.getValue();
        final Set<VariableSymbol> dummySymbols = new SymbolCollector(ruleCondition).getDummySymbols();
        assertThat(dummySymbols, hasSize(1));
        final VariableSymbol dummySymbol = dummySymbols.iterator().next();
        assertEquals(dummySymbol.getEqual(), ec);
        assertEquals(templateName, ec.getFactVariables().getFirst().getTemplate().getName());
        assertSame(ec.getFactVariables().getFirst().getTemplate(), path.getTemplate());
    }

    @Test
    public void aLitteMoreComplexTest() throws ParseException {
        final String input =
                "(and (" + templateName + " (" + slot1Name + " ?x)) (and ?y <- (" + template2Name + " (" + slot1Name
                        + " ?x)) (" + template3Name + " (" + slot1Name + " ?x))) (test (> ?x 10)) (test (< ?x 15)))";
        final NetworkMockup ptn = new NetworkMockup();
        final RuleCondition ruleCondition = clipsToCondition(ptn, input);
        final List<ConditionalElement<SymbolLeaf>> conditionalElements = ruleCondition.getConditionalElements();
        assertEquals(1, conditionalElements.size());
        // TBD see above
        final List<SingleFactVariable> variables =
                conditionalElements.get(0).accept(new ShallowFactVariableCollector()).getFactVariables();
        assertEquals(3, variables.size());
        final Set<VariableSymbol> dummySymbols = new SymbolCollector(ruleCondition).getDummySymbols();
        assertThat(dummySymbols.stream().map(VariableSymbol::getEqual).collect(toList()),
                hasItem(variables.get(0).getEqual()));
        assertEquals(templateName, variables.get(0).getTemplate().getName());
        assertSame(getSymbol(ruleCondition, "?y").getEqual(), variables.get(1).getEqual());
        assertEquals(template2Name, variables.get(1).getTemplate().getName());
        assertThat(dummySymbols.stream().map(VariableSymbol::getEqual).collect(toList()),
                hasItem(variables.get(2).getEqual()));
        assertEquals(template3Name, variables.get(2).getTemplate().getName());
    }

}
