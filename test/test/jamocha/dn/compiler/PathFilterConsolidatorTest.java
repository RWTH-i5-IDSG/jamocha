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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ConstructCache.Defrule.Translated;
import org.jamocha.dn.compiler.PathFilterConsolidator;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.DummyPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.languages.clips.parser.SFPVisitorImpl;
import org.jamocha.languages.clips.parser.generated.ParseException;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.RuleConditionProcessor;
import org.jamocha.languages.common.Warning;
import org.junit.Test;

import test.jamocha.util.NetworkMockup;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class PathFilterConsolidatorTest {
	private static final String templateString =
			"(deftemplate templ1 (slot slot1 (type INTEGER)))\n"
					+ "(deftemplate templ2 (slot slot1 (type INTEGER)))\n"
					+ "(deftemplate templ3 (slot slot1 (type INTEGER)))\n";
	private static final String preRule = "(defrule rule1 ";
	private static final String postRule = " => )\n";

	private static Queue<Warning> run(final SFPParser parser, final SFPVisitorImpl visitor)
			throws ParseException {
		while (true) {
			final SFPStart n = parser.Start();
			if (n == null)
				return visitor.getWarnings();
			n.jjtAccept(visitor, null);
		}
	}

	private static List<Translated> clipsToFilters(final String condition) throws ParseException {
		final StringReader parserInput =
				new StringReader(new StringBuilder().append(templateString).append(preRule)
						.append(condition).append(postRule).toString());
		final SFPParser parser = new SFPParser(parserInput);
		final NetworkMockup ptn = new NetworkMockup();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(ptn, ptn);
		run(parser, visitor);
		final Defrule rule = ptn.getRule("rule1");
		final List<ConditionalElement> conditionalElements =
				rule.getCondition().getConditionalElements();
		RuleConditionProcessor.flatten(conditionalElements);
		assertThat(conditionalElements, hasSize(1));
		return new PathFilterConsolidator(rule).consolidate();
	}

	@Test
	public void testSimpleVariableBinding() throws ParseException {
		final String input = "(templ1 (slot1 ?x))";
		final List<Translated> filterPartitions = clipsToFilters(input);
		assertThat(filterPartitions, hasSize(1));
		final List<PathFilter> filters = filterPartitions.get(0).getCondition();
		assertThat(filters, hasSize(1));
		final PathFilter filter = filters.get(0);
		assertThat(filter.getPositiveExistentialPaths(), hasSize(0));
		assertThat(filter.getNegativeExistentialPaths(), hasSize(0));
		final PathFilterElement[] filterElements = filter.getFilterElements();
		assertEquals(1, filterElements.length);
		final PathFilterElement filterElement = filterElements[0];
		assertThat(filterElement, instanceOf(DummyPathFilterElement.class));
		final Path[] paths = ((DummyPathFilterElement) filterElement).getPaths();
		assertEquals(1, paths.length);
		final Path path = paths[0];
		assertEquals("templ1", path.getTemplate().getName());
	}

	@Test
	public void testSimpleExistential() throws ParseException {
		final String input = "(and (initial-fact) (exists (templ1 (slot1 ?x))))";
		final List<Translated> filterPartitions = clipsToFilters(input);
		assertThat(filterPartitions, hasSize(1));
		final List<PathFilter> filters = filterPartitions.get(0).getCondition();
		assertThat(filters, hasSize(1));
		final PathFilter filter = filters.get(0);
		final Set<Path> positiveExistentialPaths = filter.getPositiveExistentialPaths();
		assertThat(positiveExistentialPaths, hasSize(1));
		final Path exPath = positiveExistentialPaths.iterator().next();
		assertEquals("templ1", exPath.getTemplate().getName());
		assertThat(filter.getNegativeExistentialPaths(), hasSize(0));
		final PathFilterElement[] filterElements = filter.getFilterElements();
		assertEquals(1, filterElements.length);
		final PathFilterElement filterElement = filterElements[0];
		assertThat(filterElement, instanceOf(DummyPathFilterElement.class));
		final Path[] paths = ((DummyPathFilterElement) filterElement).getPaths();
		assertEquals(1, paths.length);
		final Path path = paths[0];
		assertEquals("initial-fact", path.getTemplate().getName());
	}

	@Test
	public void testSimpleOr() throws ParseException {
		final String input = "(or (templ1 (slot1 ?x)) (templ1 (slot1 ?y)))";
		final List<Translated> filterPartitions = clipsToFilters(input);
		assertThat(filterPartitions, hasSize(2));
		final Path compare;
		{
			final List<PathFilter> filters = filterPartitions.get(0).getCondition();
			assertThat(filters, hasSize(1));
			final PathFilter filter = filters.get(0);
			assertThat(filter.getPositiveExistentialPaths(), hasSize(0));
			assertThat(filter.getNegativeExistentialPaths(), hasSize(0));
			final PathFilterElement[] filterElements = filter.getFilterElements();
			assertEquals(1, filterElements.length);
			final PathFilterElement filterElement = filterElements[0];
			assertThat(filterElement, instanceOf(DummyPathFilterElement.class));
			final Path[] paths = ((DummyPathFilterElement) filterElement).getPaths();
			assertEquals(1, paths.length);
			final Path path = paths[0];
			assertEquals("templ1", path.getTemplate().getName());
			compare = path;
		}
		{
			final List<PathFilter> filters = filterPartitions.get(1).getCondition();
			assertThat(filters, hasSize(1));
			final PathFilter filter = filters.get(0);
			assertThat(filter.getPositiveExistentialPaths(), hasSize(0));
			assertThat(filter.getNegativeExistentialPaths(), hasSize(0));
			final PathFilterElement[] filterElements = filter.getFilterElements();
			assertEquals(1, filterElements.length);
			final PathFilterElement filterElement = filterElements[0];
			assertThat(filterElement, instanceOf(DummyPathFilterElement.class));
			final Path[] paths = ((DummyPathFilterElement) filterElement).getPaths();
			assertEquals(1, paths.length);
			final Path path = paths[0];
			assertEquals("templ1", path.getTemplate().getName());
			assertNotSame(compare, path);
		}
	}
}
