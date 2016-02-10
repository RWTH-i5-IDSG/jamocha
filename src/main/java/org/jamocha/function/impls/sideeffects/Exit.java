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
package org.jamocha.function.impls.sideeffects;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Exit implements Function<Object> {
    public static final String IN_CLIPS = "exit";

    @Override
    public String inClips() {
        return IN_CLIPS;
    }

    @Override
    public SlotType getReturnType() {
        return SlotType.NIL;
    }

    @Override
    public <V extends FunctionVisitor> V accept(final V visitor) {
        visitor.visit(this);
        return visitor;
    }

    static {
        FunctionDictionary.addVarArgsGeneratorWithSideEffects(IN_CLIPS,
                (final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
                    if (!(paramTypes.length == 0 || (paramTypes.length == 1 && paramTypes[0] == SlotType.LONG))) {
                        return null;
                    }
                    return new Exit() {
                        @Override
                        public Object evaluate(final Function<?>... params) {
                            final int exitStatus =
                                    paramTypes.length == 0 ? 0 : ((Long) params[0].evaluate()).intValue();
                            System.exit(exitStatus);
                            return null;
                        }

                        @Override
                        public SlotType[] getParamTypes() {
                            return paramTypes;
                        }
                    };
                });
    }
}
