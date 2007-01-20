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

import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;

import woolfel.examples.model.Account;
import woolfel.examples.model.Account2;
import woolfel.examples.model.Account3;
import woolfel.examples.model.BackupAccount;
import woolfel.examples.model.TestBean3;
import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * Test the declareObject functionality
 */
public class DeclareClassTest extends TestCase {

	/**
	 * 
	 */
	public DeclareClassTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public DeclareClassTest(String arg0) {
		super(arg0);
	}

    public void testDeclareClass() {
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        int count = engine.getDefclasses().size();
        assertEquals(1,count);
        System.out.println("number of Defclass is " + count);
    }
    
    public void testDeclareClass2() {
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        engine.declareObject(TestBean3.class);
        int count = engine.getDefclasses().size();
        assertEquals(2,count);
        System.out.println("number of Defclass is " + count);
    }
    
    public void testDeftemplate() {
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        assertNotNull(engine.getCurrentFocus().getTemplates());
        int count = engine.getCurrentFocus().getTemplateCount();
        assertEquals(2,count);
        System.out.println("number of Deftemplates is " + count);
    }
    
    public void testDeclareClassInheritance() {
        System.out.println("\ntestDeclareClassInheritance");
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        engine.declareObject(BackupAccount.class);
        int count = engine.getDefclasses().size();
        assertEquals(2,count);
        System.out.println("number of Defclass is " + count);
        Template acctemp = engine.getCurrentFocus().getTemplate(Account.class.getName());
        Template bkacc = engine.getCurrentFocus().getTemplate(BackupAccount.class.getName());
        Slot[] accslots = acctemp.getAllSlots();
        Slot[] bkslots = bkacc.getAllSlots();
        for (int idx=0; idx < accslots.length; idx++) {
            assertTrue(accslots[idx].getName().equals(bkslots[idx].getName()));
            System.out.println(accslots[idx].getName() + "=" + bkslots[idx].getName());
        }
    }
    
    public void testDeclareClassInheritance2() {
        System.out.println("\ntestDeclareClassInheritance2");
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(BackupAccount.class);
        engine.declareObject(Account.class);
        int count = engine.getDefclasses().size();
        assertEquals(2,count);
        System.out.println("number of Defclass is " + count);
        Template acctemp = engine.getCurrentFocus().getTemplate(Account.class.getName());
        Template bkacc = engine.getCurrentFocus().getTemplate(BackupAccount.class.getName());
        Slot[] accslots = acctemp.getAllSlots();
        Slot[] bkslots = bkacc.getAllSlots();
        for (int idx=0; idx < accslots.length; idx++) {
            assertTrue(accslots[idx].getName().equals(bkslots[idx].getName()));
            System.out.println(accslots[idx].getName() + "=" + bkslots[idx].getName());
        }
    }
    
    public void testDeclareClassInheritance3() {
        System.out.println("\ntestDeclareClassInheritance3");
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        engine.declareObject(Account2.class,null,Account.class.getName());
        int count = engine.getDefclasses().size();
        assertEquals(2,count);
        System.out.println("number of Defclass is " + count);
        Template acctemp = engine.getCurrentFocus().getTemplate(Account.class.getName());
        Template acc2 = engine.getCurrentFocus().getTemplate(Account2.class.getName());
        Slot[] accslots = acctemp.getAllSlots();
        Slot[] acc2slots = acc2.getAllSlots();
        for (int idx=0; idx < accslots.length; idx++) {
            assertTrue(accslots[idx].getName().equals(acc2slots[idx].getName()));
            System.out.println(accslots[idx].getName() + "=" + acc2slots[idx].getName());
        }
    }
    
    public void testDeclareClassInheritance4() {
        System.out.println("\ntestDeclareClassInheritance3");
        Rete engine = new Rete();
        assertNotNull(engine);
        engine.declareObject(Account.class);
        engine.declareObject(Account2.class,null,Account.class.getName());
        engine.declareObject(Account3.class,null,Account2.class.getName());
        int count = engine.getDefclasses().size();
        assertEquals(3,count);
        System.out.println("number of Defclass is " + count);
        Template acctemp = engine.getCurrentFocus().getTemplate(Account.class.getName());
        Template acc3 = engine.getCurrentFocus().getTemplate(Account3.class.getName());
        Slot[] accslots = acctemp.getAllSlots();
        Slot[] acc3slots = acc3.getAllSlots();
        for (int idx=0; idx < accslots.length; idx++) {
            assertTrue(accslots[idx].getName().equals(acc3slots[idx].getName()));
            System.out.println(accslots[idx].getName() + "=" + acc3slots[idx].getName());
        }
    }
}
