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

import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;

import woolfel.examples.model.TestBean2;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Simple testcase for deffacts
 */
public class DeffactTest extends TestCase {

	/**
	 * 
	 */
	public DeffactTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DeffactTest(String arg0) {
		super(arg0);
	}

    public void testCreateDeffact(){
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
    }
    
    public void testCreateDeffactWithNull(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1(null);
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
    }
    
    public void testCreateDeffactWithPrimitive(){
        Defclass dc = new Defclass(TestBean2.class);
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        TestBean2 bean = new TestBean2();
        bean.setAttr1("testString");
        
        Fact fact = dtemp.createFact(bean,dc,1);
        assertNotNull(fact);
        System.out.println(fact.toFactString());
    }
}
