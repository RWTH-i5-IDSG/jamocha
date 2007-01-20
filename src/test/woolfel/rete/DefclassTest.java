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

import java.beans.PropertyDescriptor;
import java.util.Collection;

import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Rete;


import woolfel.examples.model.Account;
import woolfel.examples.model.IAccount;
import woolfel.examples.model.TestBean;
import woolfel.examples.model.TestBean2;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * DefclassTest performs basic unit test of the core functionality
 * of Defclass.
 */
public class DefclassTest extends TestCase {

	/**
	 * 
	 */
	public DefclassTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DefclassTest(String arg0) {
		super(arg0);
	}
    
    /**
     * Test TestBean2, which does implement add/remove
     * listener.
     */
    public void testJavaBean(){
        Defclass dc = new Defclass(TestBean2.class);
        assertEquals(true,dc.isJavaBean());
    }

    /**
     * Test TestBean, which does not implement add/remove
     * listener.
     */
    public void testNonJavaBeans(){
        Defclass dc = new Defclass(TestBean.class);
        assertEquals(false,dc.isJavaBean());
    }

    /**
     * Test TestBean and get the BeanInfo
     */
    public void testBeanInfo() {
        Defclass dc = new Defclass(TestBean2.class);
        assertNotNull(dc.getBeanInfo());
    }
    
    /**
     * Test TestBean2 and make sure the PropertyDescriptor
     * isn't null.
     */
    public void testPropertyDescriptor() {
        Defclass dc = new Defclass(TestBean2.class);
        assertNotNull(dc.getPropertyDescriptors());
    }
    
    /**
     * Test TestBean2 and make sure it has the right number
     * PropertyDescriptors
     */
    public void testPropertyCount() {
        Defclass dc = new Defclass(TestBean2.class);
        if (dc.getPropertyDescriptors() != null){
            PropertyDescriptor[] pds = dc.getPropertyDescriptors();
            System.out.println(pds.length);
            for (int idx=0; idx < pds.length; idx++){
                PropertyDescriptor pd = pds[idx];
                System.out.println(idx + " name=" + pd.getName());
            }
            assertEquals(6,pds.length);
        } else {
            // this will report the test failed
            assertNotNull(dc.getPropertyDescriptors());
        }
    }
    
    /**
     * Test createDeftemplate(String) method to make sure the
     * Defclass can create a deftemplate for the given Defclass.
     */
    public void testGetDeftemplate() {
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        assertEquals("testBean2",dtemp.getName());
        System.out.println("deftemplate name: " + dtemp.getName());
        assertEquals(6,dtemp.getNumberOfSlots());
        System.out.println("slot count: " + dtemp.getNumberOfSlots());
    }
    
    /**
     * Test defclass using an interface, which defines a domain object
     */
    public void testInterface() {
        Defclass dc = new Defclass(IAccount.class);
        assertNotNull(dc);
        Deftemplate dtemp = dc.createDeftemplate("account");
        assertNotNull(dtemp);
        assertEquals(14,dtemp.getAllSlots().length);
    }

    /**
     * Test defclass using an object that implements an interface
     */
    public void testInterfaceSlot() {
        Defclass dc = new Defclass(IAccount.class);
        assertNotNull(dc);
        Deftemplate dtemp = dc.createDeftemplate("account");
        assertNotNull(dtemp);
        assertEquals(14,dtemp.getAllSlots().length);
    }

    public void testObject() {
        Defclass dc = new Defclass(Account.class);
        assertNotNull(dc);
        Deftemplate dtemp = dc.createDeftemplate("account");
        assertNotNull(dtemp);
    }
    
    public void testDeclareObject() {
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        Collection templ = engine.getCurrentFocus().getTemplates();
        assertEquals(2,templ.size());
    }
}
