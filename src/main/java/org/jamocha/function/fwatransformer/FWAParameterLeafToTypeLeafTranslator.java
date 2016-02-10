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

import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

@RequiredArgsConstructor
public class FWAParameterLeafToTypeLeafTranslator extends FWATranslator<ParameterLeaf, TypeLeaf> {
    @Override
    public FWAParameterLeafToTypeLeafTranslator of() {
        return new FWAParameterLeafToTypeLeafTranslator();
    }

    public static PredicateWithArguments<TypeLeaf> getArguments(final PredicateWithArguments<ParameterLeaf> predicate) {
        final FWAParameterLeafToTypeLeafTranslator instance = new FWAParameterLeafToTypeLeafTranslator();
        predicate.accept(instance);
        return (PredicateWithArguments<TypeLeaf>) instance.functionWithArguments;
    }

    @Override
    public void visit(final ParameterLeaf leaf) {
        this.functionWithArguments = new TypeLeaf(leaf.getType());
    }
}
