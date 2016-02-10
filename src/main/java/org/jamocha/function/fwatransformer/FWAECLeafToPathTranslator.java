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

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class FWAECLeafToPathTranslator extends FWATranslator<ECLeaf, PathLeaf> {

    private final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf;

    @Override
    public FWATranslator<ECLeaf, PathLeaf> of() {
        return new FWAECLeafToPathTranslator(this.equivalenceClassToPathLeaf);
    }

    public static FunctionWithArguments<PathLeaf> translate(final FunctionWithArguments<ECLeaf> toTranslate,
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
        return toTranslate.accept(new FWAECLeafToPathTranslator(equivalenceClassToPathLeaf)).functionWithArguments;
    }

    public static PredicateWithArgumentsComposite<PathLeaf> translate(
            final PredicateWithArgumentsComposite<ECLeaf> toTranslate,
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
        return (PredicateWithArgumentsComposite<PathLeaf>) toTranslate
                .accept(new FWAECLeafToPathTranslator(equivalenceClassToPathLeaf)).functionWithArguments;
    }

    public static PredicateWithArguments<PathLeaf> translate(final PredicateWithArguments<ECLeaf> toTranslate,
            final Map<EquivalenceClass, FunctionWithArguments<PathLeaf>> equivalenceClassToPathLeaf) {
        return (PredicateWithArguments<PathLeaf>) toTranslate
                .accept(new FWAECLeafToPathTranslator(equivalenceClassToPathLeaf)).functionWithArguments;
    }

    @Override
    public void visit(final ECLeaf fwa) {
        this.functionWithArguments = this.equivalenceClassToPathLeaf.get(fwa.getEc());
    }
}
