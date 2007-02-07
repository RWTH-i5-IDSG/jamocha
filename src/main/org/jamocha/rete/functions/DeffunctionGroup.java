/*
 * Copyright 2002-2007 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

/**
 * @author pete
 *
 */
public class DeffunctionGroup implements FunctionGroup {

    private List<Function> funcs = new ArrayList<Function>();
    
    /**
     * 
     */
    public DeffunctionGroup() {
    }

    public String getName() {
        return DeffunctionGroup.class.getName();
    }

    /* (non-Javadoc)
     * @see org.jamocha.rete.FunctionGroup#listFunctions()
     */
    public List listFunctions() {
        return funcs;
    }

    /**
     * At engine initialization time, the function group doesn't
     * have any functions.
     */
    public void loadFunctions(Rete engine) {
    }

    /**
     * Add a function to the group
     * @param f
     */
    public void addFunction(Function f) {
        this.funcs.add(f);
    }
}
