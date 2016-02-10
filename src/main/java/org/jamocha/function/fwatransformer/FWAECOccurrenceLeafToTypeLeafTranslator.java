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

import org.jamocha.dn.compiler.ecblocks.ECOccurrenceLeaf;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

public class FWAECOccurrenceLeafToTypeLeafTranslator extends FWATranslator<ECOccurrenceLeaf, TypeLeaf> {
    public static PredicateWithArguments<TypeLeaf> translate(final PredicateWithArguments<ECOccurrenceLeaf> predicate) {
        final FWAECOccurrenceLeafToTypeLeafTranslator instance = new FWAECOccurrenceLeafToTypeLeafTranslator();
        predicate.accept(instance);
        return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
    }

    public static FunctionWithArguments<TypeLeaf> translate(final FunctionWithArguments<ECOccurrenceLeaf> function) {
        final FWAECOccurrenceLeafToTypeLeafTranslator instance = new FWAECOccurrenceLeafToTypeLeafTranslator();
        function.accept(instance);
        return instance.functionWithArguments;
    }

    @Override
    public FWATranslator<ECOccurrenceLeaf, TypeLeaf> of() {
        return new FWAECOccurrenceLeafToTypeLeafTranslator();
    }

    @Override
    public void visit(final ECOccurrenceLeaf leaf) {
        final FunctionWithArguments<ECLeaf> peek = leaf.getEcOccurrence().getEc().getConstantExpressions().peek();
        if (null != peek) {
            this.functionWithArguments = new ConstantLeaf<TypeLeaf>(peek.evaluate(), peek.getReturnType());
        } else {
            this.functionWithArguments = new TypeLeaf(leaf.getReturnType());
        }
    }
}
