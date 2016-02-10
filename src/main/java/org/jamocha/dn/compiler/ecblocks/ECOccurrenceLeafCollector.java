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

import java.util.ArrayList;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;

/**
 * Collects all PathLeafs used within the FunctionWithArguments in order of occurrence.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECOccurrenceLeafCollector implements DefaultFunctionWithArgumentsLeafVisitor<ECOccurrenceLeaf> {
    private final ArrayList<ECOccurrenceLeaf> ecLeafs = new ArrayList<>();

    public static ArrayList<ECOccurrenceLeaf> collect(final FunctionWithArguments<ECOccurrenceLeaf> fwa) {
        final ECOccurrenceLeafCollector instance = new ECOccurrenceLeafCollector();
        fwa.accept(instance);
        return instance.ecLeafs;
    }

    @Override
    public void visit(final ConstantLeaf<ECOccurrenceLeaf> constantLeaf) {
    }

    @Override
    public void visit(final GlobalVariableLeaf<ECOccurrenceLeaf> globalVariableLeaf) {
    }

    @Override
    public void visit(final ECOccurrenceLeaf ecOccurrenceLeaf) {
        this.ecLeafs.add(ecOccurrenceLeaf);
    }
}
