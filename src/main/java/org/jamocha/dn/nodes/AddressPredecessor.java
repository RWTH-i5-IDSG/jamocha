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

package org.jamocha.dn.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jamocha.dn.memory.FactAddress;

/**
 * Combines a {@link FactAddress fact address} with an {@link Edge edge}. Identifies a {@link FactAddress fact address}
 * in the target {@link Node node} of the {@link Edge edge}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see FactAddress
 * @see Edge
 */
@Getter
@RequiredArgsConstructor
public class AddressPredecessor {
    /**
     * {@link Edge} connecting the {@link Node node} the {@link FactAddress address} is valid for as source {@link Node
     * node} and a target {@link Node node}, for which the {@link Edge edge} can localize the {@link FactAddress
     * address}.
     */
    final Edge edge;
    /**
     * {@link FactAddress Address} valid in the source {@link Node node} of {@link Edge edge}.
     */
    final FactAddress address;
}
