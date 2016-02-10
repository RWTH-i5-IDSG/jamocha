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
package org.jamocha.dn.compiler.ecblocks;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * Collects all PathLeafs used within the FunctionWithArguments in order of occurrence.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class OrderedECCollector implements DefaultFunctionWithArgumentsLeafVisitor<ECLeaf> {
    private final ArrayList<EquivalenceClass> ecs = new ArrayList<>();

    public static ArrayList<EquivalenceClass> collect(final FunctionWithArguments<ECLeaf> fwa) {
        final OrderedECCollector instance = new OrderedECCollector();
        fwa.accept(instance);
        return instance.ecs;
    }

    @Override
    public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
    }

    @Override
    public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
    }

    @Override
    public void visit(final ECLeaf ecLeaf) {
        this.ecs.add(ecLeaf.getEc());
    }
}
