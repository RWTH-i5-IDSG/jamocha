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

import static org.jamocha.util.ToArray.toArray;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.jamocha.dn.NetworkToDot;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class ExportGv implements Function<Object> {
    public static final String IN_CLIPS = "export-gv";
    static final SlotType[] PARAM_TYPES = {SlotType.STRING};

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

    @Override
    public SlotType[] getParamTypes() {
        return PARAM_TYPES;
    }

    static {
        FunctionDictionary.addFixedArgsGeneratorWithSideEffects(IN_CLIPS, PARAM_TYPES,
                (final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> new ExportGv() {
                    @Override
                    public Object evaluate(final Function<?>... params) {
                        final String fileName = (String) params[0].evaluate();
                        try (final FileWriter fileWriter = new FileWriter(fileName)) {
                            fileWriter.write(new NetworkToDot(network).toString());
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        FunctionDictionary.addVarArgsGeneratorWithSideEffects(IN_CLIPS,
                (final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
                    if (paramTypes.length < 2 || paramTypes[0] != SlotType.STRING) {
                        return null;
                    }
                    for (int i = 1; i < paramTypes.length; ++i) {
                        if (paramTypes[i] != SlotType.SYMBOL) {
                            return null;
                        }
                    }
                    return new ExportGv() {
                        @Override
                        public Object evaluate(final Function<?>... params) {
                            final String fileName = (String) params[0].evaluate();
                            try (final FileWriter fileWriter = new FileWriter(fileName)) {
                                fileWriter.write(new NetworkToDot(network,
                                        toArray(Arrays.stream(params, 1, params.length)
                                                .map(f -> ((Symbol) f.evaluate()).getImage()), String[]::new))
                                        .toString());
                            } catch (final IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                });
    }
}
