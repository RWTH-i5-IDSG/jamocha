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

import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.TemplateSlotLeaf;

public class FWAPathLeafToTemplateSlotLeafTranslator extends FWATranslator<PathLeaf, TemplateSlotLeaf> {
    public static PredicateWithArguments<TemplateSlotLeaf> getArguments(
            final PredicateWithArguments<PathLeaf> predicate) {
        final FWAPathLeafToTemplateSlotLeafTranslator instance = new FWAPathLeafToTemplateSlotLeafTranslator();
        predicate.accept(instance);
        return (PredicateWithArguments<TemplateSlotLeaf>) instance.functionWithArguments;
    }

    @Override
    public FWAPathLeafToTemplateSlotLeafTranslator of() {
        return new FWAPathLeafToTemplateSlotLeafTranslator();
    }

    @Override
    public void visit(final PathLeaf leaf) {
        this.functionWithArguments = new TemplateSlotLeaf(leaf.getPath().getTemplate(), leaf.getSlot());
    }
}
