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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
public class ECOccurrence {
    private final EquivalenceClass ec;

    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}