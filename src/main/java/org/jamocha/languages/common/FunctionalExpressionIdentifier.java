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

package org.jamocha.languages.common;

import org.jamocha.function.fwa.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FunctionalExpressionIdentifier implements DefaultFunctionWithArgumentsLeafVisitor<ECLeaf> {

    boolean constant = true;

    public static boolean isConstant(final FunctionWithArguments<ECLeaf> fwa) {
        return fwa.accept(new FunctionalExpressionIdentifier()).constant;
    }

    @Override
    public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
        // still constant
    }

    @Override
    public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
        // global variable treated as constant
    }

    @Override
    public void visit(final ECLeaf leaf) {
        final RuleCondition.EquivalenceClass ec = leaf.getEc();
        this.constant &=
                ec.getFactVariables().isEmpty() && ec.getSlotVariables().isEmpty() && ec.getFunctionalExpressions()
                        .isEmpty();
    }
}
