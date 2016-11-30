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

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.RHSVariableLeaf;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.VariableValueContext;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class FWASymbolToRHSVariableLeafTranslator extends FWATranslator<SymbolLeaf, RHSVariableLeaf> {
    final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> ec2PathLeaf;
    final VariableValueContext context;

    @Override
    public FWATranslator<SymbolLeaf, RHSVariableLeaf> of() {
        return new FWASymbolToRHSVariableLeafTranslator(this.ec2PathLeaf, this.context);
    }

    public static List<FunctionWithArguments<RHSVariableLeaf>> translate(
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> ec2PathLeaf,
            final VariableValueContext context, final Collection<FunctionWithArguments<SymbolLeaf>> actions) {
        final FWASymbolToRHSVariableLeafTranslator instance =
                new FWASymbolToRHSVariableLeafTranslator(ec2PathLeaf, context);
        return actions.stream().map(fwa -> fwa.accept(instance).functionWithArguments).collect(Collectors.toList());
    }

    @Override
    public void visit(final SymbolLeaf symbolLeaf) {
        final VariableSymbol symbol = symbolLeaf.getSymbol();
        final FunctionWithArguments<PathLeaf> pathLeaf = this.ec2PathLeaf.get(symbol.getEqual());
        if (pathLeaf != null) {
            if (pathLeaf instanceof PathLeaf) {
                this.context.addInitializer(symbol,
                        new SlotInFactAddress(((PathLeaf) pathLeaf).getPath().getFactAddressInCurrentlyLowestNode(),
                                ((PathLeaf) pathLeaf).getSlot()));
            } else if (pathLeaf instanceof ConstantLeaf) {
                this.context.addInitializer(symbol, pathLeaf.evaluate());
            }
        }
        this.functionWithArguments = new RHSVariableLeaf(this.context, symbol, symbolLeaf.getReturnType());
    }
}
