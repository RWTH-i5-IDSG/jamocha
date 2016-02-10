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
package org.jamocha.function.fwatransformer;

import java.util.Collection;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FWAPathToAddressTranslator extends FWATranslator<PathLeaf, ParameterLeaf> {
    private final Collection<SlotInFactAddress> addresses;

    public static FunctionWithArguments<ParameterLeaf> translate(final FunctionWithArguments<PathLeaf> fwa,
            final Collection<SlotInFactAddress> addresses) {
        return fwa.accept(new FWAPathToAddressTranslator(addresses)).functionWithArguments;
    }

    public static PredicateWithArguments<ParameterLeaf> translate(final PredicateWithArguments<PathLeaf> fwa,
            final Collection<SlotInFactAddress> addresses) {
        return (PredicateWithArguments<ParameterLeaf>) fwa
                .accept(new FWAPathToAddressTranslator(addresses)).functionWithArguments;
    }

    public FWAPathToAddressTranslator(final Collection<SlotInFactAddress> addresses) {
        this.addresses = addresses;
    }

    @Override
    public FWATranslator<PathLeaf, ParameterLeaf> of() {
        return new FWAPathToAddressTranslator(addresses);
    }

    @Override
    public void visit(final PathLeaf pathLeaf) {
        final FactAddress factAddressInCurrentlyLowestNode = pathLeaf.getPath().getFactAddressInCurrentlyLowestNode();
        this.addresses.add(new SlotInFactAddress(factAddressInCurrentlyLowestNode, pathLeaf.getSlot()));
        this.functionWithArguments = new ParameterLeaf(pathLeaf.getReturnType(), pathLeaf.hash());
    }
}
