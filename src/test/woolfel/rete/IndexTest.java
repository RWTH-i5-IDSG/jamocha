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

import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;

import woolfel.examples.model.TestBean2;
import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * A basic test to validate the Index works correctly
 */
public class IndexTest extends TestCase {

	/**
	 * 
	 */
	public IndexTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public IndexTest(String arg0) {
		super(arg0);
	}

    /**
     * Startout with a simple test of 2 Long objects
     */
    public void testObjectEquals() {
        Long l1 = new Long(2);
        Long l2 = new Long(2);
        assertEquals(true,l1.equals(l2));
    }

    /**
     * Test an Index with a Fact[] array with 1 fact
     */
    public void testOneFact() {
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
        Fact[] list1 = new Fact[] {fact};
        Fact[] list2 = new Fact[] {fact};
        
        Index in1 = new Index(list1);
        Index in2 = new Index(list2);
        assertEquals(true,in1.equals(in2));
    }
    
    /**
     * Test an Index with a Fact[] array with 5 fact
     */
    public void testFiveFacts() {
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
        
        TestBean2 bean2 = new TestBean2();
        bean2.setAttr1("testString2");
        bean2.setAttr2(12);
        short a32 = 32;
        bean2.setAttr3(a32);
        long a42 = 1012;
        bean2.setAttr4(a42);
        float a52 = 101012;
        bean2.setAttr5(a52);
        double a62 = 101.1012;
        bean2.setAttr6(a62);

        TestBean2 bean3 = new TestBean2();
        bean3.setAttr1("testString3");
        bean3.setAttr2(13);
        short a33 = 33;
        bean3.setAttr3(a33);
        long a43 = 1013;
        bean3.setAttr4(a43);
        float a53 = 101013;
        bean3.setAttr5(a53);
        double a63 = 101.1013;
        bean3.setAttr6(a63);

        TestBean2 bean4 = new TestBean2();
        bean4.setAttr1("testString4");
        bean4.setAttr2(14);
        short a34 = 34;
        bean4.setAttr3(a34);
        long a44 = 1014;
        bean4.setAttr4(a44);
        float a54 = 101014;
        bean4.setAttr5(a54);
        double a64 = 101.1014;
        bean4.setAttr6(a64);

        TestBean2 bean5 = new TestBean2();
        bean5.setAttr1("testString5");
        bean5.setAttr2(15);
        short a35 = 35;
        bean5.setAttr3(a35);
        long a45 = 1015;
        bean5.setAttr4(a45);
        float a55 = 101015;
        bean5.setAttr5(a55);
        double a65 = 101.1015;
        bean5.setAttr6(a65);

        Fact fact = dtemp.createFact(bean,dc,1);
        Fact fact2 = dtemp.createFact(bean2,dc,1);
        Fact fact3 = dtemp.createFact(bean3,dc,1);
        Fact fact4 = dtemp.createFact(bean4,dc,1);
        Fact fact5 = dtemp.createFact(bean5,dc,1);

        Fact[] list1 = new Fact[] {fact,fact2,fact3,fact4,fact5};
        Fact[] list2 = new Fact[] {fact,fact2,fact3,fact4,fact5};
        
        Index in1 = new Index(list1);
        Index in2 = new Index(list2);
        assertEquals(true,in1.equals(in2));
    }
    
    /**
     * Test the index with a HashMap and make sure it all works
     * as expected.
     */
    public void testHashMapIndex() {
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
        
        TestBean2 bean2 = new TestBean2();
        bean2.setAttr1("testString2");
        bean2.setAttr2(12);
        short a32 = 32;
        bean2.setAttr3(a32);
        long a42 = 1012;
        bean2.setAttr4(a42);
        float a52 = 101012;
        bean2.setAttr5(a52);
        double a62 = 101.1012;
        bean2.setAttr6(a62);

        TestBean2 bean3 = new TestBean2();
        bean3.setAttr1("testString3");
        bean3.setAttr2(13);
        short a33 = 33;
        bean3.setAttr3(a33);
        long a43 = 1013;
        bean3.setAttr4(a43);
        float a53 = 101013;
        bean3.setAttr5(a53);
        double a63 = 101.1013;
        bean3.setAttr6(a63);

        TestBean2 bean4 = new TestBean2();
        bean4.setAttr1("testString4");
        bean4.setAttr2(14);
        short a34 = 34;
        bean4.setAttr3(a34);
        long a44 = 1014;
        bean4.setAttr4(a44);
        float a54 = 101014;
        bean4.setAttr5(a54);
        double a64 = 101.1014;
        bean4.setAttr6(a64);

        TestBean2 bean5 = new TestBean2();
        bean5.setAttr1("testString5");
        bean5.setAttr2(15);
        short a35 = 35;
        bean5.setAttr3(a35);
        long a45 = 1015;
        bean5.setAttr4(a45);
        float a55 = 101015;
        bean5.setAttr5(a55);
        double a65 = 101.1015;
        bean5.setAttr6(a65);

        Fact fact = dtemp.createFact(bean,dc,1);
        Fact fact2 = dtemp.createFact(bean2,dc,1);
        Fact fact3 = dtemp.createFact(bean3,dc,1);
        Fact fact4 = dtemp.createFact(bean4,dc,1);
        Fact fact5 = dtemp.createFact(bean5,dc,1);

        Fact[] list1 = new Fact[] {fact,fact2,fact3,fact4,fact5};
        Fact[] list2 = new Fact[] {fact,fact2,fact3,fact4,fact5};
        
        Index in1 = new Index(list1);
        Index in2 = new Index(list2);
        assertEquals(true,in1.equals(in2));
        
        HashMap map = new HashMap();
        map.put(in1,list1);
        // simple test to see if HashMap.containsKey(in1) works
        assertEquals(true, map.containsKey(in1));
        // now test with the second instance of index, this should return
        // true, since Index class overrides equals() and hashCode().
        assertEquals(true, map.containsKey(in2));
    }
}
