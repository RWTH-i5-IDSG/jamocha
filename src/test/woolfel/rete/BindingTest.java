/*
 * Copyright 2002-2006 Peter Lin
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
package woolfel.rete;

import org.jamocha.rete.BetaNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Slot;

import woolfel.examples.model.TestBean2;
import woolfel.examples.model.TestBean3;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Tests for binding class. The test will create some bindings
 * and create betaNodes.
 */
public class BindingTest extends TestCase {

	/**
	 * 
	 */
	public BindingTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public BindingTest(String arg0) {
		super(arg0);
	}

    public void setUp(){
        System.out.println("this test does not do any setup");
    }
    
    public void tearDown(){
        System.out.println("this test does not do any teardown");
    }
    
    public void testSingleBinding(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");

        Slot[] slts = dtemp.getAllSlots();

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setRightRow(1);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding[] binds = {bn};
        BetaNode btnode = new BetaNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
    
    public void testTwoBinding(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");

        Slot[] slts = dtemp.getAllSlots();

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setRightRow(1);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setRightRow(1);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding[] binds = {bn,bn2};
        BetaNode btnode = new BetaNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }

    public void testThreeBinding(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        
        Defclass dc2 = new Defclass(TestBean3.class);
        Deftemplate dtemp2 = dc.createDeftemplate("testBean3");
        

        Slot[] slts = dtemp.getAllSlots();
        Slot[] slts2 = dtemp2.getAllSlots();

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setRightRow(1);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setRightRow(1);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding bn3 = new Binding();
        bn3.setLeftRow(1);
        bn3.setRightRow(2);
        bn3.setLeftIndex(0);
        bn3.setRightIndex(0);

        Binding[] binds = {bn,bn2,bn3};
        BetaNode btnode = new BetaNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
    
    public void testThreeBinding2(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        
        Defclass dc2 = new Defclass(TestBean3.class);
        Deftemplate dtemp2 = dc.createDeftemplate("testBean3");
        

        Slot[] slts = dtemp.getAllSlots();
        Slot[] slts2 = dtemp2.getAllSlots();

        Binding bn = new Binding();
        bn.setLeftRow(0);
        bn.setRightRow(1);
        bn.setLeftIndex(0);
        bn.setRightIndex(0);
        
        Binding bn2 = new Binding();
        bn2.setLeftRow(0);
        bn2.setRightRow(1);
        bn2.setLeftIndex(2);
        bn2.setRightIndex(2);
        
        Binding bn3 = new Binding();
        bn3.setLeftRow(0);
        bn3.setRightRow(2);
        bn3.setLeftIndex(0);
        bn3.setRightIndex(0);

        Binding[] binds = {bn,bn2,bn3};
        BetaNode btnode = new BetaNode(1);
        btnode.setBindings(binds);
        
        System.out.println("betaNode::" + btnode.toPPString());
        assertNotNull(btnode.toPPString());
    }
}