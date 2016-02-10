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

package test.jamocha.util.builder.rule;

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.ConstructCache;
import org.jamocha.dn.ConstructCache.Defrule.PathRule;
import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilterList;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.RuleCondition;
import test.jamocha.util.Slots;
import test.jamocha.util.builder.fwa.PathFunctionBuilder;
import test.jamocha.util.builder.fwa.PathPredicateBuilder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class PathRuleBuilder {
    final String rulename;
    final ArrayList<PathFilterList> condition = new ArrayList<>();

    public PathRuleBuilder add(final PredicateWithArguments<PathLeaf> predicate) {
        return add(new PathFilter(predicate));
    }

    public PathRuleBuilder add(final PathFilter filter) {
        return add(PathNodeFilterSet.newRegularPathNodeFilterSet(filter));
    }

    public PathRuleBuilder add(final PathFilterList pfl) {
        condition.add(pfl);
        return this;
    }

    public PathRule build() {
        final ConstructCache.Defrule defrule = new ConstructCache.Defrule(rulename, "", 0, (RuleCondition) null,
                new ArrayList<FunctionWithArguments<SymbolLeaf>>());
        final PathRule pathRule = defrule.newTranslated(condition, Collections.emptyMap());
        return pathRule;
    }

    public static void main(final String[] args) {
        final Network network = new Network();

        // TODO implement the notation-paper test in a benchmark folder

        final Path p1 = new Path(
                MemoryFactory.getMemoryFactory().newTemplate("t1", "", Slots.newDouble("s1"), Slots.newDouble("s2")));
        final Path p2 = new Path(Slots.DOUBLE);
        final Path p3 = new Path(Slots.DOUBLE);
        final SlotAddress a1 = new SlotAddress(0);
        final SlotAddress a2 = new SlotAddress(0);
        final SlotAddress a3 = new SlotAddress(0);
        final SlotAddress a4 = new SlotAddress(1);
        final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
        final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
        final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);

        final PathFilter f = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
        final PathFilter g = new PathPredicateBuilder(equals)
                .addFunction(new PathFunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
                .addFunction(new PathFunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();

        final PathRule asd = new PathRuleBuilder("asd").add(f).add(g).build();
        network.buildRule(asd);
    }
}
