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
package org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding;

import lombok.Getter;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.AssignmentGraphNodeVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class SlotBindingNode extends SlotOrFactBindingNode {
    final SingleFactVariable.SingleSlotVariable slotInGroupingFactVariable;

    public SlotBindingNode(final RuleCondition.EquivalenceClass equivalenceClass,
            final TemplateSlotLeaf schema,
            final SingleFactVariable.SingleSlotVariable slotInGroupingFactVariable) {
        super(equivalenceClass, schema);
        this.slotInGroupingFactVariable = slotInGroupingFactVariable;
    }

    @Override
    public SingleFactVariable getGroupingFactVariable() {
        return this.slotInGroupingFactVariable.getFactVariable();
    }

    @Override
    public <V extends AssignmentGraphNodeVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }
}
