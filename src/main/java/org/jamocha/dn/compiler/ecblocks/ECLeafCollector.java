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
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;

/**
 * Collects all PathLeafs used within the FunctionWithArguments in order of occurrence.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECLeafCollector implements DefaultFunctionWithArgumentsLeafVisitor<ECLeaf> {
    private final ArrayList<ECLeaf> ecLeafs = new ArrayList<>();

    public static ArrayList<ECLeaf> collect(final FunctionWithArguments<ECLeaf> fwa) {
        final ECLeafCollector instance = new ECLeafCollector();
        fwa.accept(instance);
        return instance.ecLeafs;
    }

    @Override
    public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
    }

    @Override
    public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
    }

    @Override
    public void visit(final ECLeaf ecLeaf) {
        this.ecLeafs.add(ecLeaf);
    }
}
