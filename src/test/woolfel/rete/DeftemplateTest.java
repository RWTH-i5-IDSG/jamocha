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

import org.jamocha.rete.Constants;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Slot;

import woolfel.examples.model.Account;
import woolfel.examples.model.TestBean2;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeftemplateTest extends TestCase {

	/**
	 * 
	 */
	public DeftemplateTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DeftemplateTest(String arg0) {
		super(arg0);
	}

    /**
     * Basic test of Defclass.createDeftemplate(String). the method
     * uses TestBean2 to create a Defclass.
     */
    public void testCreateTemplateFromClass(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        System.out.println(dtemp.toPPString());
    }
    
    /**
     * the test creates 4 slots and uses them to create a deftemplate
     * the method only test the getNumberOfSlots method and 
     * toPPEString.
     */
    public void testCreateTemplateFromSlots(){
        Slot[] slots = new Slot[4];
        slots[0] = new Slot();
        slots[0].setId(0);
        slots[0].setName("col1");
        slots[0].setValueType(Constants.INT_PRIM_TYPE);

        slots[1] = new Slot();
        slots[1].setId(1);
        slots[1].setName("col2");
        slots[1].setValueType(Constants.DOUBLE_PRIM_TYPE);
        
        slots[2] = new Slot();
        slots[2].setId(2);
        slots[2].setName("col3");
        slots[2].setValueType(Constants.OBJECT_TYPE);
        
        slots[3] = new Slot();
        slots[3].setId(3);
        slots[3].setName("col4");
        slots[3].setValueType(Constants.LONG_PRIM_TYPE);
        
        Deftemplate dtemp = new Deftemplate("template1",null,slots);
        assertNotNull(dtemp);
        assertEquals(4,dtemp.getNumberOfSlots());
        System.out.println(dtemp.toPPString());
    }

    /**
     * method uses Account class to create a Defclass. the method tests
     * toPPEString.
     */
    public void testCreateTemplate2(){
        Defclass dc = new Defclass(Account.class);
        Deftemplate dtemp = dc.createDeftemplate("account");
        assertNotNull(dtemp);
        assertNotNull(dtemp.toPPString());
        System.out.println(dtemp.toPPString());
    }
    
    /**
     * 
     *
     */
    public void testCreateFactFromInstance(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("random1");
        bean.setAttr2(101);
        short s = 10001;
        bean.setAttr3(s);
        long l = 10101018;
        bean.setAttr4(l);
        bean.setAttr5(1010101);
        bean.setAttr6(1001.1001);
        org.jamocha.rete.Fact fact = dtemp.createFact(bean,dc,0);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
    }
    
    /**
     * 
     *
     */
    public void testSlotID() {
        Slot[] slots = new Slot[4];
        slots[0] = new Slot();
        slots[0].setId(0);
        slots[0].setName("col1");
        slots[0].setValueType(Constants.INT_PRIM_TYPE);

        slots[1] = new Slot();
        slots[1].setId(1);
        slots[1].setName("col2");
        slots[1].setValueType(Constants.DOUBLE_PRIM_TYPE);
        
        slots[2] = new Slot();
        slots[2].setId(2);
        slots[2].setName("col3");
        slots[2].setValueType(Constants.OBJECT_TYPE);
        
        slots[3] = new Slot();
        slots[3].setId(3);
        slots[3].setName("col4");
        slots[3].setValueType(Constants.LONG_PRIM_TYPE);
        
        Deftemplate dtemp = new Deftemplate("template1",null,slots);
        assertNotNull(dtemp);
        assertEquals(4,dtemp.getNumberOfSlots());
        System.out.println(dtemp.toPPString());
        Slot[] theslots = dtemp.getAllSlots();
        for (int idx=0; idx < theslots.length; idx++) {
            Slot aslot = theslots[idx];
            assertEquals(idx,aslot.getId());
            System.out.println("slot id: " + aslot.getId());
        }
    }
    
    /**
     * method will use Account class to create a Defclass first.
     * Once it has the deftemplate, we check to make sure the slots
     * have the correct slot id, which is the column id. this makes
     * sure that we can efficiently update facts using the slot id.
     */
    public void testCreateTemplateSlot(){
        String acc = "account";
        Defclass dc = new Defclass(Account.class);
        Deftemplate dtemp = dc.createDeftemplate(acc);
        assertNotNull(dtemp);
        assertNotNull(dtemp.toPPString());
        assertEquals(acc,dtemp.getName());
        System.out.println(dtemp.toPPString());
        Slot[] theslots = dtemp.getAllSlots();
        for (int idx=0; idx < theslots.length; idx++) {
            Slot aslot = theslots[idx];
            assertEquals(idx,aslot.getId());
            System.out.println("slot id: " + aslot.getId());
        }
    }
}
