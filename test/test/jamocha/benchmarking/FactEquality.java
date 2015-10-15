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
package test.jamocha.benchmarking;

import java.util.Date;

import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.impls.functions.Plus;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Less;
import org.jamocha.languages.clips.parser.generated.ParseException;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PathRuleBuilder;
import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.Slots;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FactEquality {

	public static void main(final String[] args) throws ParseException {
		final Network network = new Network();
		network.getInteractiveEventsLogger().info("starting at " + new Date());
		// testDuplicateF1(network);
		// testDuplicateF2(network);
		testFactEqualityNode(network);
		network.loadFactsFromFile("abc.fct");
		network.getInteractiveEventsLogger().info("  done   at " + new Date());
		System.exit(0);
	}

	private static void testDuplicateF1(final Network network) {

		final Template templateA = network.defTemplate("A", "", Slots.newLong("a"));
		final Template templateB = network.defTemplate("B", "", Slots.newLong("a"), Slots.newLong("c"));
		final Template templateC = network.defTemplate("C", "", Slots.newLong("c"));

		final Path r1_a = new Path(templateA);
		final Path r1_b = new Path(templateB);
		final Path r2_a = new Path(templateA);
		final Path r2_b = new Path(templateB);
		final Path r2_c = new Path(templateC);
		final Path r3_b = new Path(templateB);
		final Path r3_c = new Path(templateC);

		final SlotAddress s1 = new SlotAddress(0);
		final SlotAddress s2 = new SlotAddress(1);

		final Function<?> plus = FunctionDictionary.lookup(Plus.inClips, SlotType.LONG, SlotType.LONG);
		final Predicate less = FunctionDictionary.lookupPredicate(Less.inClips, SlotType.LONG, SlotType.LONG);

		final PathFilter r1_f1 = createFilter(r1_a, s1, r1_b, s1, plus, less);
		final PathFilter r2_f1 = createFilter(r2_a, s1, r2_b, s1, plus, less);
		final PathFilter r2_f2 = createFilter(r2_b, s2, r2_c, s1, plus, less);
		final PathFilter r3_f2 = createFilter(r3_b, s2, r3_c, s1, plus, less);

		final PathRule r1 = new PathRuleBuilder("r1").add(r1_f1).build();
		final PathRule r2 = new PathRuleBuilder("r2").add(r2_f1).add(r2_f2).build();
		final PathRule r3 = new PathRuleBuilder("r3").add(r3_f2).build();
		network.buildRule(r3);
		network.buildRule(r2);
		network.buildRule(r1);
	}

	private static void testDuplicateF2(final Network network) {
		final Template templateA = network.defTemplate("A", "", Slots.newLong("a"));
		final Template templateB = network.defTemplate("B", "", Slots.newLong("a"), Slots.newLong("c"));
		final Template templateC = network.defTemplate("C", "", Slots.newLong("c"));

		final Path r1_a = new Path(templateA);
		final Path r1_b = new Path(templateB);
		final Path r2_a = new Path(templateA);
		final Path r2_b = new Path(templateB);
		final Path r2_c = new Path(templateC);
		final Path r3_b = new Path(templateB);
		final Path r3_c = new Path(templateC);

		final SlotAddress s1 = new SlotAddress(0);
		final SlotAddress s2 = new SlotAddress(1);

		final Function<?> plus = FunctionDictionary.lookup(Plus.inClips, SlotType.LONG, SlotType.LONG);
		final Predicate less = FunctionDictionary.lookupPredicate(Less.inClips, SlotType.LONG, SlotType.LONG);

		final PathFilter r1_f1 = createFilter(r1_a, s1, r1_b, s1, plus, less);
		final PathFilter r2_f1 = createFilter(r2_a, s1, r2_b, s1, plus, less);
		final PathFilter r2_f2 = createFilter(r2_b, s2, r2_c, s1, plus, less);
		final PathFilter r3_f2 = createFilter(r3_b, s2, r3_c, s1, plus, less);

		final PathRule r1 = new PathRuleBuilder("r1").add(r1_f1).build();
		final PathRule r2 = new PathRuleBuilder("r2").add(r2_f1).add(r2_f2).build();
		final PathRule r3 = new PathRuleBuilder("r3").add(r3_f2).build();
		network.buildRule(r1);
		network.buildRule(r2);
		network.buildRule(r3);
	}

	private static void testFactEqualityNode(final Network network) {
		final Template templateA = network.defTemplate("A", "", Slots.newLong("a"));
		final Template templateB = network.defTemplate("B", "", Slots.newLong("a"), Slots.newLong("c"));
		final Template templateC = network.defTemplate("C", "", Slots.newLong("c"));

		final Path r1_a = new Path(templateA);
		final Path r1_b = new Path(templateB);
		final Path r2_a = new Path(templateA);
		final Path r2_b = new Path(templateB);
		final Path r2_bprime = new Path(templateB);
		final Path r2_c = new Path(templateC);
		final Path r3_b = new Path(templateB);
		final Path r3_c = new Path(templateC);

		final SlotAddress s1 = new SlotAddress(0);
		final SlotAddress s2 = new SlotAddress(1);

		final Function<?> plus = FunctionDictionary.lookup(Plus.inClips, SlotType.LONG, SlotType.LONG);
		final Predicate less = FunctionDictionary.lookupPredicate(Less.inClips, SlotType.LONG, SlotType.LONG);

		final PathFilter r1_f1 = createFilter(r1_a, s1, r1_b, s1, plus, less);
		final PathFilter r2_f1 = createFilter(r2_a, s1, r2_b, s1, plus, less);
		final PathFilter r2_f2 = createFilter(r2_bprime, s2, r2_c, s1, plus, less);
		final PathFilter r3_f2 = createFilter(r3_b, s2, r3_c, s1, plus, less);

		final PathFilter r2_fe =
				new PredicateBuilder(FunctionDictionary.lookupPredicate(Equals.inClips, SlotType.FACTADDRESS,
						SlotType.FACTADDRESS)).addPath(r2_b, null).addPath(r2_bprime, null).buildFilter();

		final PathRule r1 = new PathRuleBuilder("r1").add(r1_f1).build();
		final PathRule r2 = new PathRuleBuilder("r2").add(r2_f1).add(r2_f2).add(r2_fe).build();
		final PathRule r3 = new PathRuleBuilder("r3").add(r3_f2).build();
		network.buildRule(r1);
		network.buildRule(r2);
		network.buildRule(r3);
	}

	private static PathFilter createFilter(final Path p_a, final SlotAddress a_s, final Path p_b,
			final SlotAddress b_s, final Function<?> inner, final Predicate outer) {
		return new PredicateBuilder(outer)
				.addFunction(new FunctionBuilder(inner).addPath(p_a, a_s).addPath(p_b, b_s).build()).addLong(101)
				.buildFilter();
	}

}
