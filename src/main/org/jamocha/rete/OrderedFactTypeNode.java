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
package org.jamocha.rete;

import java.io.Serializable;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.AbstractAlpha;

/**
 * OrderedFactTypeNode is the object type node for ordered facts. The
 * difference between the two is this type node is used only for
 * ordered facts. Jamocha groups the ordered facts by the number of slots
 * first. The second thing is it uses a HashMap for the successors, since
 * the first slot in the ordered fact is a symbol. This means it is always
 * an equality test, which means we shouldn't iterate over all successors.
 * The class should just look it up and only propogate to the one node.
 * @author Peter Lin
 */

public class OrderedFactTypeNode extends AbstractAlpha implements Serializable {

    /**
     * The Class that defines object type
     */
    private Template deftemplate = null;

    /**
     * HashMap entries for unique AlphaNodes
     */
//    private Map entries = CollectionsFactory.localMap();

    /**
     * @param id
     */
    public OrderedFactTypeNode(int id, Template tmpl) {
        super(id);
        this.deftemplate = tmpl;
    }

    public void assertFact(Fact factInstance, Rete engine, WorkingMemory mem)
            throws AssertException {

    }

    public String hashString() {
        // TODO Auto-generated method stub
        return null;
    }

    public void retractFact(Fact factInstance, Rete engine, WorkingMemory mem)
            throws RetractException {
        // TODO Auto-generated method stub

    }

    public String toPPString() {
        return "InputNode for Template(" + this.deftemplate.getName() + ")";
    }

    public String toString() {
        return "OrderedFactTypeNode(" + this.deftemplate.getName() + ")";
    }

}
