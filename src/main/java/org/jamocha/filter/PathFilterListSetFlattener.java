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
package org.jamocha.filter;

import java.util.HashSet;
import java.util.Set;

import org.jamocha.filter.PathFilterList.PathExistentialList;
import org.jamocha.filter.PathFilterList.PathSharedListWrapper.PathSharedList;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilterListSetFlattener implements PathFilterListVisitor {

    public static Set<PathNodeFilterSet> flatten(final Set<PathFilterList> toFlatten) {
        final PathFilterListSetFlattener instance = new PathFilterListSetFlattener();
        toFlatten.forEach(a -> a.accept(instance));
        return instance.result;
    }

    private final Set<PathNodeFilterSet> result = new HashSet<>();

    @Override
    public void visit(final PathNodeFilterSet filter) {
        result.add(filter);
    }

    @Override
    public void visit(final PathExistentialList filter) {
        filter.getPurePart().forEach(p -> p.accept(this));
        filter.getExistentialClosure().accept(this);
    }

    @Override
    public void visit(final PathSharedList filter) {
        filter.getUnmodifiableFilterListCopy().forEach(a -> a.accept(this));
    }
}
