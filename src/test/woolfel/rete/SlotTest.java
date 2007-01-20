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

import org.jamocha.rete.AlphaNode;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.Slot;

import woolfel.examples.model.TestBean2;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Simple test for slot to make sure it works correctly
 */
public class SlotTest extends TestCase {

	/**
	 * 
	 */
	public SlotTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public SlotTest(String arg0) {
		super(arg0);
	}

    public void testOneSlot(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        Slot[] slts = dtemp.getAllSlots();
        ObjectTypeNode otn = new ObjectTypeNode(1,dtemp);
        AlphaNode an = new AlphaNode(1);
        slts[0].setValue(ConversionUtils.convert(110));
        an.setOperator(Constants.EQUAL);
        an.setSlot(slts[0]);
        System.out.println("node::" + an.toString());
        assertNotNull(an.toString());
    }
    
    public void testTwoSlots(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        Slot[] slts = dtemp.getAllSlots();
        ObjectTypeNode otn = new ObjectTypeNode(1,dtemp);
        AlphaNode an1 = new AlphaNode(1);
        AlphaNode an2 = new AlphaNode(1);
        
        slts[0].setValue("testString");
        slts[1].setValue(ConversionUtils.convert(999));
        
        an1.setSlot(slts[0]);
        an1.setOperator(Constants.EQUAL);
        System.out.println("node::" + an1.toPPString());
        assertNotNull(an1.toPPString());
        
        an2.setSlot(slts[1]);
        an2.setOperator(Constants.GREATER);
        System.out.println("node::" + an2.toPPString());
        assertNotNull(an2.toPPString());
    }
}
