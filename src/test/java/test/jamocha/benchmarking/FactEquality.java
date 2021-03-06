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
package test.jamocha.benchmarking;

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
import test.jamocha.util.Slots;
import test.jamocha.util.builder.fwa.PathFunctionBuilder;
import test.jamocha.util.builder.fwa.PathPredicateBuilder;
import test.jamocha.util.builder.rule.PathRuleBuilder;

import java.util.Date;

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

        final Path r1A = new Path(templateA);
        final Path r1B = new Path(templateB);
        final Path r2A = new Path(templateA);
        final Path r2B = new Path(templateB);
        final Path r2C = new Path(templateC);
        final Path r3B = new Path(templateB);
        final Path r3C = new Path(templateC);

        final SlotAddress s1 = new SlotAddress(0);
        final SlotAddress s2 = new SlotAddress(1);

        final Function<?> plus = FunctionDictionary.lookup(Plus.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate less = FunctionDictionary.lookupPredicate(Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);

        final PathFilter r1F1 = createFilter(r1A, s1, r1B, s1, plus, less);
        final PathFilter r2F1 = createFilter(r2A, s1, r2B, s1, plus, less);
        final PathFilter r2F2 = createFilter(r2B, s2, r2C, s1, plus, less);
        final PathFilter r3F2 = createFilter(r3B, s2, r3C, s1, plus, less);

        final PathRule r1 = new PathRuleBuilder("r1").add(r1F1).build();
        final PathRule r2 = new PathRuleBuilder("r2").add(r2F1).add(r2F2).build();
        final PathRule r3 = new PathRuleBuilder("r3").add(r3F2).build();
        network.buildRule(r3);
        network.buildRule(r2);
        network.buildRule(r1);
    }

    private static void testDuplicateF2(final Network network) {
        final Template templateA = network.defTemplate("A", "", Slots.newLong("a"));
        final Template templateB = network.defTemplate("B", "", Slots.newLong("a"), Slots.newLong("c"));
        final Template templateC = network.defTemplate("C", "", Slots.newLong("c"));

        final Path r1A = new Path(templateA);
        final Path r1B = new Path(templateB);
        final Path r2A = new Path(templateA);
        final Path r2B = new Path(templateB);
        final Path r2C = new Path(templateC);
        final Path r3B = new Path(templateB);
        final Path r3C = new Path(templateC);

        final SlotAddress s1 = new SlotAddress(0);
        final SlotAddress s2 = new SlotAddress(1);

        final Function<?> plus = FunctionDictionary.lookup(Plus.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate less = FunctionDictionary.lookupPredicate(Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);

        final PathFilter r1F1 = createFilter(r1A, s1, r1B, s1, plus, less);
        final PathFilter r2F1 = createFilter(r2A, s1, r2B, s1, plus, less);
        final PathFilter r2F2 = createFilter(r2B, s2, r2C, s1, plus, less);
        final PathFilter r3F2 = createFilter(r3B, s2, r3C, s1, plus, less);

        final PathRule r1 = new PathRuleBuilder("r1").add(r1F1).build();
        final PathRule r2 = new PathRuleBuilder("r2").add(r2F1).add(r2F2).build();
        final PathRule r3 = new PathRuleBuilder("r3").add(r3F2).build();
        network.buildRule(r1);
        network.buildRule(r2);
        network.buildRule(r3);
    }

    private static void testFactEqualityNode(final Network network) {
        final Template templateA = network.defTemplate("A", "", Slots.newLong("a"));
        final Template templateB = network.defTemplate("B", "", Slots.newLong("a"), Slots.newLong("c"));
        final Template templateC = network.defTemplate("C", "", Slots.newLong("c"));

        final Path r1A = new Path(templateA);
        final Path r1B = new Path(templateB);
        final Path r2A = new Path(templateA);
        final Path r2B = new Path(templateB);
        final Path r2Bprime = new Path(templateB);
        final Path r2C = new Path(templateC);
        final Path r3B = new Path(templateB);
        final Path r3C = new Path(templateC);

        final SlotAddress s1 = new SlotAddress(0);
        final SlotAddress s2 = new SlotAddress(1);

        final Function<?> plus = FunctionDictionary.lookup(Plus.IN_CLIPS, SlotType.LONG, SlotType.LONG);
        final Predicate less = FunctionDictionary.lookupPredicate(Less.IN_CLIPS, SlotType.LONG, SlotType.LONG);

        final PathFilter r1F1 = createFilter(r1A, s1, r1B, s1, plus, less);
        final PathFilter r2F1 = createFilter(r2A, s1, r2B, s1, plus, less);
        final PathFilter r2F2 = createFilter(r2Bprime, s2, r2C, s1, plus, less);
        final PathFilter r3F2 = createFilter(r3B, s2, r3C, s1, plus, less);

        final PathFilter r2Fe = new PathPredicateBuilder(
                FunctionDictionary.lookupPredicate(Equals.IN_CLIPS, SlotType.FACTADDRESS, SlotType.FACTADDRESS))
                .addPath(r2B, null).addPath(r2Bprime, null).buildFilter();

        final PathRule r1 = new PathRuleBuilder("r1").add(r1F1).build();
        final PathRule r2 = new PathRuleBuilder("r2").add(r2F1).add(r2F2).add(r2Fe).build();
        final PathRule r3 = new PathRuleBuilder("r3").add(r3F2).build();
        network.buildRule(r1);
        network.buildRule(r2);
        network.buildRule(r3);
    }

    private static PathFilter createFilter(final Path pA, final SlotAddress aS, final Path pB, final SlotAddress bS,
            final Function<?> inner, final Predicate outer) {
        return new PathPredicateBuilder(outer)
                .addFunction(new PathFunctionBuilder(inner).addPath(pA, aS).addPath(pB, bS).build()).addLong(101)
                .buildFilter();
    }

}
