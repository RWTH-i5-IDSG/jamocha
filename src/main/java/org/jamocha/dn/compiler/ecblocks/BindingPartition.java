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

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.BindingPartition.BindingSubSet;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingNode;
import org.jamocha.dn.compiler.ecblocks.assignmentgraph.node.binding.BindingType;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class BindingPartition extends InformedPartition<BindingNode, BindingType, BindingSubSet> {
    public static class BindingSubSet extends InformedPartition.InformedSubSet<BindingNode, BindingType> {
        public BindingSubSet(final IdentityHashMap<RowIdentifier, BindingNode> elements, final BindingType info) {
            super(elements, info);
        }

        public BindingSubSet(final BindingSubSet copy) {
            super(copy);
        }

        public BindingSubSet(final Map<RowIdentifier, ? extends BindingNode> elements, final BindingType info) {
            super(elements, info);
        }
    }

    public BindingPartition(final BindingPartition copy) {
        super(copy, BindingSubSet::new);
    }
}
