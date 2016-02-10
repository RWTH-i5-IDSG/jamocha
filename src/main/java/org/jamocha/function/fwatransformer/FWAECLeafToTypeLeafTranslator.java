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

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

public class FWAECLeafToTypeLeafTranslator extends FWATranslator<ECLeaf, TypeLeaf> {
    public static PredicateWithArguments<TypeLeaf> translate(final PredicateWithArguments<ECLeaf> predicate) {
        final FWAECLeafToTypeLeafTranslator instance = new FWAECLeafToTypeLeafTranslator();
        predicate.accept(instance);
        return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
    }

    public static FunctionWithArguments<TypeLeaf> translate(final FunctionWithArguments<ECLeaf> function) {
        final FWAECLeafToTypeLeafTranslator instance = new FWAECLeafToTypeLeafTranslator();
        function.accept(instance);
        return instance.functionWithArguments;
    }

    @Override
    public FWATranslator<ECLeaf, TypeLeaf> of() {
        return new FWAECLeafToTypeLeafTranslator();
    }

    @Override
    public void visit(final ECLeaf leaf) {
        // final FunctionWithArguments<ECLeaf> peek = leaf.getEc().getConstantExpressions().peek();
        // if (null != peek) {
        //   this.functionWithArguments = new ConstantLeaf<>(peek.evaluate(), peek.getReturnType());
        // } else {
        this.functionWithArguments = new TypeLeaf(leaf.getReturnType());
        // }
    }
}
