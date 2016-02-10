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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Execute implements Function<Object> {
    public static final String IN_CLIPS = "execute";
    private static final SlotType[] PARAM_TYPES = new SlotType[]{SlotType.STRING};

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
    public String inClips() {
        return IN_CLIPS;
    }

    @Override
    public SlotType[] getParamTypes() {
        return PARAM_TYPES;
    }

    static {
        FunctionDictionary.addFixedArgsGeneratorWithSideEffects(IN_CLIPS, PARAM_TYPES,
                (final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> new Execute() {
                    @Override
                    public Object evaluate(final Function<?>... params) {
                        final String command = ((String) params[0].evaluate());
                        final Runtime r = Runtime.getRuntime();
                        try {
                            final Process p = r.exec(command);
                            p.waitFor();
                            try (final BufferedReader b = new BufferedReader(
                                    new InputStreamReader(p.getInputStream()))) {
                                String line = "";
                                while ((line = b.readLine()) != null) {
                                    System.out.println(line);
                                }
                            }
                        } catch (final IOException | InterruptedException e) {
                            System.err.println("failed to execute " + command);
                        }
                        return null;
                    }
                });
    }
}
