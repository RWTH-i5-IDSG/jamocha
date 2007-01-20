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

import java.util.HashMap;

import org.jamocha.rete.CompositeIndex;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;

import woolfel.examples.model.TestBean2;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Simple testcase for the CompositeIndex used by ObjectTypeNode
 */
public class CompositeIndexTest extends TestCase {

	/**
	 * 
	 */
	public CompositeIndexTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public CompositeIndexTest(String arg0) {
		super(arg0);
	}

    public void testEqual(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = new CompositeIndex("attr1",Constants.EQUAL,fact.getSlotValue(0));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
    }
    
    public void testNotEqual() {
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = 
            new CompositeIndex("attr1",Constants.NOTEQUAL,fact.getSlotValue(0));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
    }
    
    public void testNil() {
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = 
            new CompositeIndex("attr1",Constants.NILL,fact.getSlotValue(0));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
    }
    
    public void testNotNil() {
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = 
            new CompositeIndex("attr1",Constants.NOTNILL,fact.getSlotValue(0));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
    }
    
    public void testIndex(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = 
            new CompositeIndex("attr1",Constants.EQUAL,fact.getSlotValue(0));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
        HashMap map = new HashMap();
        map.put(ci,bean);
        
        CompositeIndex ci2 = 
            new CompositeIndex("attr1",Constants.EQUAL,fact.getSlotValue(0));
        assertTrue(map.containsKey(ci2));
        
        CompositeIndex ci3 = 
            new CompositeIndex("attr1",Constants.NOTEQUAL,fact.getSlotValue(0));
        assertFalse(map.containsKey(ci3));
        
        CompositeIndex ci4 = 
            new CompositeIndex("attr1",Constants.NILL,fact.getSlotValue(0));
        assertFalse(map.containsKey(ci4));
        
        CompositeIndex ci5 = 
            new CompositeIndex("attr1",Constants.NOTNILL,fact.getSlotValue(0));
        assertFalse(map.containsKey(ci5));
    }
    
    public void testIndex2(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        bean.setAttr2(1);
        short a3 = 3;
        bean.setAttr3(a3);
        long a4 = 101;
        bean.setAttr4(a4);
        float a5 = 10101;
        bean.setAttr5(a5);
        double a6 = 101.101;
        bean.setAttr6(a6);
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
        CompositeIndex ci = 
            new CompositeIndex("attr2",Constants.EQUAL,fact.getSlotValue(1));
        assertNotNull(ci);
        System.out.println(ci.toPPString());
        HashMap map = new HashMap();
        map.put(ci,bean);
        
        CompositeIndex ci2 = 
            new CompositeIndex("attr2",Constants.EQUAL,fact.getSlotValue(1));
        assertTrue(map.containsKey(ci2));
        
        CompositeIndex ci3 = 
            new CompositeIndex("attr2",Constants.NOTEQUAL,fact.getSlotValue(1));
        assertFalse(map.containsKey(ci3));
        
        CompositeIndex ci4 = 
            new CompositeIndex("attr2",Constants.NILL,fact.getSlotValue(1));
        assertFalse(map.containsKey(ci4));
        
        CompositeIndex ci5 = 
            new CompositeIndex("attr2",Constants.NOTNILL,fact.getSlotValue(1));
        assertFalse(map.containsKey(ci5));
    }
}
