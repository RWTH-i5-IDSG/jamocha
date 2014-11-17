/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.dn.compiler;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ParserToNetwork;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.compiler.FactVariableCollector;
import org.jamocha.dn.compiler.SymbolToPathTranslator;
import org.jamocha.filter.Path;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.Warning;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class SymbolToPathTranslatorTest {

	private static final String templateString = "(deftemplate templ1 (slot slot1 (type INTEGER)))\n"
			+ "(deftemplate templ2 (slot slot1 (type INTEGER)))\n"
			+ "(deftemplate templ3 (slot slot1 (type INTEGER)))\n";
	private static final String preRule = "(defrule rule1 ";
	private static final String postRule = " => )\n";

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor) throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static <T extends SideEffectFunctionToNetwork & ParserToNetwork> List<ConditionalElement> clipsToCondition(
			final T ptn, final String condition) throws ParseException {
		final StringReader parserInput =
				new StringReader(new StringBuilder().append(templateString).append(preRule).append(condition)
						.append(postRule).toString());
		final SFPParser parser = new SFPParser(parserInput);
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		final Defrule rule = ptn.getRule("rule1");
		final RuleCondition ruleCondition = rule.getCondition();
		RuleConditionProcessor.flatten(ruleCondition);
		return ruleCondition.getConditionalElements();
	}

	@Test
	public void simpleTest() throws ParseException {
		final String input = "(and (templ1 (slot1 ?x)) (test (> ?x 10)))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement andCe = conditionalElements.get(0);
		final Map<SingleFactVariable, Path> paths =
				FactVariableCollector.collectPaths(ptn.getInitialFactTemplate(), andCe).getRight();
		final List<ConditionalElement> andChildren = andCe.getChildren();
		assertThat(andChildren, hasSize(2));
		final SingleFactVariable xFactVar;
		{
			final ConditionalElement templCe = andChildren.get(0);
			assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
			xFactVar = ((TemplatePatternConditionalElement) templCe).getFactVariable();
		}
		{
			final ConditionalElement testCe = andChildren.get(1);
			assertThat(testCe, instanceOf(TestConditionalElement.class));
			final PredicateWithArguments translated =
					SymbolToPathTranslator.translate(((TestConditionalElement) testCe).getPredicateWithArguments(),
							paths);
			assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
			final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) translated).getArgs();
			assertThat(args, is(arrayWithSize(2)));
			final FunctionWithArguments x = args[0];
			assertThat(x, instanceOf(PathLeaf.class));
			final Path path = ((PathLeaf) x).getPath();
			assertEquals(path.getTemplate().getName(), "templ1");
			assertSame(path, paths.get(xFactVar));
		}
	}

	@Test
	public void samePathInAndTest() throws ParseException {
		final String input = "(and (templ1 (slot1 ?x)) (test (> ?x 10)) (test (< ?x 20)))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement andCe = conditionalElements.get(0);
		final Map<SingleFactVariable, Path> paths =
				FactVariableCollector.collectPaths(ptn.getInitialFactTemplate(), andCe).getRight();
		final List<ConditionalElement> andChildren = andCe.getChildren();
		assertThat(andChildren, hasSize(3));
		final SingleFactVariable xFactVar;
		{
			final ConditionalElement templCe = andChildren.get(0);
			assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
			xFactVar = ((TemplatePatternConditionalElement) templCe).getFactVariable();
		}
		{
			final ConditionalElement testCe = andChildren.get(1);
			assertThat(testCe, instanceOf(TestConditionalElement.class));
			final PredicateWithArguments translated =
					SymbolToPathTranslator.translate(((TestConditionalElement) testCe).getPredicateWithArguments(),
							paths);
			assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
			final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) translated).getArgs();
			assertThat(args, is(arrayWithSize(2)));
			final FunctionWithArguments x = args[0];
			assertThat(x, instanceOf(PathLeaf.class));
			final Path path = ((PathLeaf) x).getPath();
			assertEquals(path.getTemplate().getName(), "templ1");
			assertSame(path, paths.get(xFactVar));
		}
		{
			final ConditionalElement testCe = andChildren.get(2);
			assertThat(testCe, instanceOf(TestConditionalElement.class));
			final PredicateWithArguments translated =
					SymbolToPathTranslator.translate(((TestConditionalElement) testCe).getPredicateWithArguments(),
							paths);
			assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
			final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) translated).getArgs();
			assertThat(args, is(arrayWithSize(2)));
			final FunctionWithArguments x = args[0];
			assertThat(x, instanceOf(PathLeaf.class));
			final Path path = ((PathLeaf) x).getPath();
			assertEquals(path.getTemplate().getName(), "templ1");
			assertSame(path, paths.get(xFactVar));
		}
	}

	@Test
	public void differentPathsInOrTest() throws ParseException {
		final String input = "(and (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 20))))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCe = conditionalElements.get(0);
		final List<ConditionalElement> orChildren = orCe.getChildren();
		assertThat(orChildren, hasSize(2));
		final Path firstPath;
		{
			final ConditionalElement andCe = orChildren.get(0);
			final Map<SingleFactVariable, Path> paths =
					FactVariableCollector.collectPaths(ptn.getInitialFactTemplate(), andCe).getRight();
			final List<ConditionalElement> andChildren = andCe.getChildren();
			assertThat(andChildren, hasSize(2));
			final SingleFactVariable xFactVar;
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				final ConditionalElement templCe = ((SharedConditionalElementWrapper) shared).getCe();
				assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
				xFactVar = ((TemplatePatternConditionalElement) templCe).getFactVariable();
				assertThat(paths, hasKey(xFactVar));
			}
			{
				final ConditionalElement testCe = andChildren.get(1);
				assertThat(testCe, instanceOf(TestConditionalElement.class));
				final PredicateWithArguments translated =
						SymbolToPathTranslator.translate(((TestConditionalElement) testCe).getPredicateWithArguments(),
								paths);
				assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
				final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) translated).getArgs();
				assertThat(args, is(arrayWithSize(2)));
				final FunctionWithArguments x = args[0];
				assertThat(x, instanceOf(PathLeaf.class));
				final Path path = ((PathLeaf) x).getPath();
				assertEquals(path.getTemplate().getName(), "templ1");
				assertSame(path, paths.get(xFactVar));
				firstPath = path;
			}
		}
		{
			final ConditionalElement andCe = orChildren.get(1);
			final Map<SingleFactVariable, Path> paths =
					FactVariableCollector.collectPaths(ptn.getInitialFactTemplate(), andCe).getRight();
			final List<ConditionalElement> andChildren = andCe.getChildren();
			assertThat(andChildren, hasSize(2));
			final SingleFactVariable xFactVar;
			{
				final ConditionalElement shared = andChildren.get(0);
				assertThat(shared, instanceOf(SharedConditionalElementWrapper.class));
				final ConditionalElement templCe = ((SharedConditionalElementWrapper) shared).getCe();
				assertThat(templCe, instanceOf(TemplatePatternConditionalElement.class));
				xFactVar = ((TemplatePatternConditionalElement) templCe).getFactVariable();
				assertThat(paths, hasKey(xFactVar));
			}
			{
				final ConditionalElement testCe = andChildren.get(1);
				assertThat(testCe, instanceOf(TestConditionalElement.class));
				final PredicateWithArguments translated =
						SymbolToPathTranslator.translate(((TestConditionalElement) testCe).getPredicateWithArguments(),
								paths);
				assertThat(translated, instanceOf(PredicateWithArgumentsComposite.class));
				final FunctionWithArguments[] args = ((PredicateWithArgumentsComposite) translated).getArgs();
				assertThat(args, is(arrayWithSize(2)));
				final FunctionWithArguments x = args[0];
				assertThat(x, instanceOf(PathLeaf.class));
				final Path path = ((PathLeaf) x).getPath();
				assertEquals(path.getTemplate().getName(), "templ1");
				assertSame(path, paths.get(xFactVar));
				assertNotSame(firstPath, path);
			}
		}
	}

	@Test(expected = Error.class)
	public void testCantUseFactVariableCollectorOnOrCE() throws ParseException {
		final String input = "(and (templ1 (slot1 ?x)) (or (test (> ?x 10)) (test (< ?x 20))))";
		final NetworkMockup ptn = new NetworkMockup();
		final List<ConditionalElement> conditionalElements = clipsToCondition(ptn, input);
		assertThat(conditionalElements, hasSize(1));
		final ConditionalElement orCe = conditionalElements.get(0);
		FactVariableCollector.collectPaths(ptn.getInitialFactTemplate(), orCe).getRight();
	}
}
