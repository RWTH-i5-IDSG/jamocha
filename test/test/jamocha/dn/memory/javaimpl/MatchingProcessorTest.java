/*
 * Copyright 2002-2015 The Jamocha Team
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
package test.jamocha.dn.memory.javaimpl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jamocha.dn.memory.javaimpl.MatchingProcessor;
import org.jamocha.filter.MatchingConfigurationElement;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MatchingProcessorTest {
	static MatchingConfigurationElement constant(final Object value) {
		return new MatchingConfigurationElement(null, Optional.of(value), true);
	}

	static MatchingConfigurationElement single() {
		return new MatchingConfigurationElement(null, Optional.empty(), true);
	}

	static MatchingConfigurationElement multi() {
		return new MatchingConfigurationElement(null, Optional.empty(), false);
	}

	@Test
	public void testOneValueMatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L };
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(constant(2L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] {}, matchings.get(0));
	}

	@Test
	public void testOneValueMissmatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L };
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(constant(5L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testNoValueMatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = {};
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] {}, matchings.get(0));
	}

	@Test
	public void testNoValueTwoMultiMatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = {};
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(multi(), multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] { 0 }, matchings.get(0));
	}

	@Test
	public void testNoValueMissmatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = {};
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(constant(5L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testSimpleMatchingConstants() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L, 3L, 4L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(constant(2L), constant(3L), constant(4L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] { 1, 2 }, matchings.get(0));
	}

	@Test
	public void testSimpleCrappyConstants() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L, 3L, 4L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(constant(3L), constant(5L), constant(4L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testSingleMatchingMultiField() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L, 3L, 4L };
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] {}, matchings.get(0));
	}

	@Test
	public void testDoublePossibilityForMultiField() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 2L, 3L, 2L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(multi(), constant(2L), multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(2));
		assertThat(matchings, containsInAnyOrder(new int[] { 0, 1 }, new int[] { 2, 3 }));
	}

	@Test
	public void testNonMatchingMultiField() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 1L, 3L, 4L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(multi(), constant(2L)));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testTooManySingles() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L, 3L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(single(), single(), single()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testTooManySingles2() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(single(), single(), single()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testTooManySingles3() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(single(), single()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testTooManySingles4() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(single(), multi(), single()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(0));
	}

	@Test
	public void testMultiSingleMultiMatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(multi(), single(), multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] { 0, 1 }, matchings.get(0));
	}

	@Test
	public void testMultiSingleMultiMultiMatch() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements =
				new ArrayList<>(Arrays.asList(multi(), single(), multi(), multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(1));
		assertArrayEquals(new int[] { 0, 1, 1 }, matchings.get(0));
	}

	@Test
	public void testMultiMultiOnSingleValue() {
		final List<int[]> matchings = new ArrayList<>();
		final Object[] values = { 3L };
		final List<MatchingConfigurationElement> matchingElements = new ArrayList<>(Arrays.asList(multi(), multi()));
		MatchingProcessor.match(values, 0, matchings::add, new int[matchingElements.size()], matchingElements, 0);
		assertThat(matchings, hasSize(2));
		assertThat(matchings, containsInAnyOrder(new int[] { 0 }, new int[] { 1 }));
	}
}
