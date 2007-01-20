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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.BetaNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.NotJoin;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.EventCounter;
import org.jamocha.rete.*;

import junit.framework.TestCase;

import woolfel.examples.model.TestBean2;

/**
 * @author Peter Lin
 *
 * We test the BetaNode to make sure it works correctly.
 */
public class NotNodeTest extends TestCase {

	/**
	 * 
	 */
	public NotNodeTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public NotNodeTest(String arg0) {
		super(arg0);
	}

    public void testCreateNode() {
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);
    }
    
    public void testCreateNode2() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);
    }
    
    public void testAssertLeftOne() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);
        
        TestBean2 bean = new TestBean2();
        bean.setAttr1("random1");
        bean.setAttr2(101);
        short s = 10001;
        bean.setAttr3(s);
        long l = 10101018;
        bean.setAttr4(l);
        bean.setAttr5(1010101);
        bean.setAttr6(1001.1001);
        Fact f1 = dtemp.createFact(bean,dc,engine.nextFactId());
        try {
            bn.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
            Map bmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
            assertEquals(1,bmem.size());
        } catch (AssertException e) {
            e.printStackTrace();
        }
    }
    
    public void testAssertLeftMultiple() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random" + ( idx + 1));
            bean.setAttr2(101);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                bn.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map bmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
        assertEquals(count,bmem.size());
    }

    /**
     * Assert several object down the right input
     */
    public void testAssertRightMultiple() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random" + ( idx + 1));
            bean.setAttr2(101);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                bn.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map bmem = (Map)engine.getWorkingMemory().getBetaRightMemory(bn);
        assertEquals(count,bmem.size());
    }
    
    /**
     * Try asserting 10 objects and make sure the results are correct
     */
    public void testMatch() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random");
            bean.setAttr2(101);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                bn.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
                bn.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(bn);
        assertEquals(count,rbmem.size());
        
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
        assertEquals(count,lbmem.size());
        
        // now check the BetaMemory has matches
        System.out.println(bn.toPPString());
        Iterator mitr = lbmem.values().iterator();
        while (mitr.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr.next();
            assertEquals(9,btm.matchCount());
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
    }
    
    /**
     * Try asserting 10 objects and make sure the results are correct
     */
    public void testAssertAndRetract() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random");
            bean.setAttr2(101);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                bn.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
                bn.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(bn);
        assertEquals(count,rbmem.size());
        
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
        assertEquals(count,lbmem.size());

        int retract = 5;
        try {
            for (int idx=0; idx < 5; idx++) {
                Fact f2 = (Fact)data.get(idx);
                bn.retractRight(f2,engine,engine.getWorkingMemory());
            }
        } catch (RetractException e) {
            e.printStackTrace();
        }
        
        rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(bn);
        assertEquals(5,rbmem.size());
        
        lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
        assertEquals(count,lbmem.size());
        
        // now check the BetaMemory has matches
        System.out.println(bn.toPPString());
        Iterator mitr = lbmem.values().iterator();
        while (mitr.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr.next();
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
    }
    
    /**
     * test the NotJoin with facts that don't match. Each BetaMemory
     * should have a match count of zero.
     */
    public void testNoMatch() {
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin bn = new NotJoin(engine.nextNodeId());
        assertNotNull(bn);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        // set the binding
        bn.setBindings(binds);

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random" + idx);
            bean.setAttr2(101 + idx);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018 + idx;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                bn.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
                bn.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(bn);
        assertEquals(count,rbmem.size());
        
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn);
        assertEquals(count,lbmem.size());
        
        // now check the BetaMemory has matches
        System.out.println(bn.toPPString());
        Iterator mitr = lbmem.values().iterator();
        while (mitr.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr.next();
            assertEquals(0,btm.matchCount());
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
    }
    
    /**
     * the test creates a NotJoin and adds a BetaNode as a successor.
     * it then asserts 10 facts and makes sure the facts are propogated
     * to the BetaNode.
     */
    public void testPropogateNoMatch() {
        System.out.println("testPropogateNoMatch");
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin nj = new NotJoin(engine.nextNodeId());
        BetaNode bn2 = new BetaNode(engine.nextNodeId());
        assertNotNull(nj);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        Binding[] binds2 = new Binding[1];
        Binding b2 = new Binding();
        b2.setLeftIndex(1);
        b2.setIsObjectVar(false);
        b2.setLeftRow(0);
        b2.setRightIndex(1);
        b2.setRightRow(0);
        b2.setVarName("var2");
        binds2[0] = b2;

        // set the binding
        nj.setBindings(binds);
        
        bn2.setBindings(binds2);
        
        // now add the second Not to the first
        try {
            nj.addSuccessorNode(bn2,engine,engine.getWorkingMemory());
        } catch (AssertException e) {
            e.printStackTrace();
        }

        int count = 10;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random" + idx);
            bean.setAttr2(101 + idx);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018 + idx;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                nj.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
                nj.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(nj);
        assertEquals(count,rbmem.size());
        
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(nj);
        assertEquals(count,lbmem.size());
        
        // now check the BetaMemory has matches
        System.out.println(nj.toPPString());
        Iterator mitr = lbmem.values().iterator();
        while (mitr.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr.next();
            assertEquals(0,btm.matchCount());
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
        
        Map lbmem2 = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn2);
        assertEquals(count,lbmem2.size());
        System.out.println(bn2.toPPString());
        Iterator mitr2 = lbmem2.values().iterator();
        while (mitr2.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr2.next();
            assertEquals(0,btm.matchCount());
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
    }
    
    /**
     * method will make sure the correct number of facts are asserted,
     * propogated and retracted.
     */
    public void testPropogateChange() {
        System.out.println("testPropogateChange");
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin nj = new NotJoin(engine.nextNodeId());
        BetaNode bn2 = new BetaNode(engine.nextNodeId());
        assertNotNull(nj);

        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        Binding[] binds2 = new Binding[1];
        Binding b2 = new Binding();
        b2.setLeftIndex(1);
        b2.setIsObjectVar(false);
        b2.setLeftRow(0);
        b2.setRightIndex(1);
        b2.setRightRow(0);
        b2.setVarName("var2");
        binds2[0] = b2;

        // set the binding
        nj.setBindings(binds);
        
        bn2.setBindings(binds2);
        
        // now add the second Not to the first
        try {
            nj.addSuccessorNode(bn2,engine,engine.getWorkingMemory());
        } catch (AssertException e) {
            e.printStackTrace();
        }

        int count = 2;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random");
            bean.setAttr2(101 + idx);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018 + idx;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                nj.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(nj);
        assertEquals(count,lbmem.size());
        
        Map lbmem2 = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn2);
        assertEquals(2,lbmem2.size());

        itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                nj.assertRight(f1,engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map rbmem = (Map)engine.getWorkingMemory().getBetaRightMemory(nj);
        assertEquals(count,rbmem.size());
        
        // once the facts are asserted to the right, there should be no
        // facts in successor. this makes sure that assertRight correctly
        // results in a retract.
        lbmem2 = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn2);
        assertEquals(0,lbmem2.size());
    }
    
    /**
     * This test makes sure that when facts are asserted, only the
     * first results in retract propogation. this is because NotJoin
     * should only propogate assert and retract when the match count
     * goes from zero to one or one to zero.
     */
    public void testPropogateChange2() {
        System.out.println("testPropogateChange2");
        // first create a rule engine instance
        Rete engine = new Rete();
        NotJoin nj = new NotJoin(engine.nextNodeId());
        BetaNode bn2 = new BetaNode(engine.nextNodeId());
        assertNotNull(nj);
        assertNotNull(bn2);
        
        // create a defclass
        Defclass dc = new Defclass(TestBean2.class);
        // create deftemplate
        Deftemplate dtemp = dc.createDeftemplate("testBean2");
        assertNotNull(dtemp);
        Binding[] binds = new Binding[1];
        Binding b1 = new Binding();
        b1.setLeftIndex(0);
        b1.setIsObjectVar(false);
        b1.setLeftRow(0);
        b1.setRightIndex(0);
        b1.setRightRow(0);
        b1.setVarName("var1");
        binds[0] = b1;
        
        Binding[] binds2 = new Binding[1];
        Binding b2 = new Binding();
        b2.setLeftIndex(1);
        b2.setIsObjectVar(false);
        b2.setLeftRow(0);
        b2.setRightIndex(1);
        b2.setRightRow(0);
        b2.setVarName("var2");
        binds2[0] = b2;

        // set the binding
        nj.setBindings(binds);
        
        bn2.setBindings(binds2);
        
        // now add the second Not to the first
        try {
            nj.addSuccessorNode(bn2,engine,engine.getWorkingMemory());
        } catch (AssertException e) {
            e.printStackTrace();
        }

        int count = 5;
        ArrayList data = new ArrayList();
        for (int idx=0; idx < count; idx++) {
            TestBean2 bean = new TestBean2();
            bean.setAttr1("random");
            bean.setAttr2(101 + idx);
            short s = 10001;
            bean.setAttr3(s);
            long l = 10101018 + idx;
            bean.setAttr4(l);
            bean.setAttr5(1010101);
            bean.setAttr6(1001.1001);
            Fact fact = dtemp.createFact(bean,dc,engine.nextFactId());
            data.add(fact);
        }
        
        Iterator itr = data.iterator();
        while (itr.hasNext()) {
            try {
                Fact f1 = (Fact)itr.next();
                nj.assertLeft(new Fact[]{f1},engine,engine.getWorkingMemory());
            } catch (AssertException e) {
                e.printStackTrace();
            }
        }
        Map lbmem = (Map)engine.getWorkingMemory().getBetaLeftMemory(nj);
        assertEquals(count,lbmem.size());
        
        Map lbmem2 = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn2);
        assertEquals(count, lbmem2.size());


		try {
            Fact f = (Fact)data.get(0);
			nj.assertRight(f, engine, engine.getWorkingMemory());
		} catch (AssertException e) {
			e.printStackTrace();
		}
        
		Map rbmem = (Map) engine.getWorkingMemory().getBetaRightMemory(nj);
        assertEquals(1,rbmem.size());
        
        // once the facts are asserted to the right, there should be no
        // facts in successor. this makes sure that assertRight correctly
        // results in a retract.
        lbmem2 = (Map)engine.getWorkingMemory().getBetaLeftMemory(bn2);
        assertEquals(1,lbmem2.size());
        
        // now assert the other facts to the NotJoin.assertRight
        try {
            for (int idx=1; idx < count; idx++) {
                Fact f = (Fact)data.get(idx);
                nj.assertRight(f, engine, engine.getWorkingMemory());
            }
        } catch (AssertException e) {
            e.printStackTrace();
        }
        
        System.out.println(nj.toPPString());
        Iterator mitr2 = lbmem.values().iterator();
        while (mitr2.hasNext()) {
            BetaMemory btm = (BetaMemory)mitr2.next();
            System.out.println("match count=" + btm.matchCount() +
                    " - " + btm.toPPString());
        }
    }
}
