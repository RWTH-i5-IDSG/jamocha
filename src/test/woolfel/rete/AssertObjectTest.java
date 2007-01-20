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
import org.jamocha.rete.exception.AssertException;

import woolfel.examples.model.Account;
import woolfel.examples.model.BackupAccount;
import woolfel.examples.model.DeletedAccount;
import woolfel.examples.model.IAccount;
import woolfel.examples.model.TestBean3;
import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AssertObjectTest extends TestCase {

	/**
	 * 
	 */
	public AssertObjectTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public AssertObjectTest(String arg0) {
		super(arg0);
	}

    public void testSimpleAssert() {
        System.out.println("start testSimpleAssert");
        Rete engine = new Rete();
        engine.declareObject(Account.class);
        assertNotNull(engine);
        Account acc1 = new Account();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,null,false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getDefclasses().size());
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertTwoObjects() {
        System.out.println("start testAssertTwoObjects");
        Rete engine = new Rete();
        engine.declareObject(Account.class);
        engine.declareObject(TestBean3.class);
        assertNotNull(engine);
        // create instance of Account
        Account acc1 = new Account();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        // create instance of TestBean3
        TestBean3 b = new TestBean3();
        b.setCount(3);
        b.setFloat(10000);
        try {
            long start = System.nanoTime();
            engine.assertObject(acc1,null,false,true);
            int count = engine.getObjectCount();
            engine.assertObject(b,null,false,true);
            int count2 = engine.getObjectCount();
            long end = System.nanoTime();

            assertTrue(true);
            assertTrue(true);
            assertEquals(1,count);
            assertEquals(2,count2);
            System.out.println("Number of facts: " + count);
            System.out.println("Number of facts: " + count2);
            System.out.println("ET: " + (end - start) + " ns");
            double el = ((double)end - (double)start) / 100000;
            System.out.println("ET: " + el + " ms");
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testRepeatedAssert() {
        System.out.println("start testRepatedAssert");
        Rete engine = new Rete();
        engine.declareObject(Account.class);
        engine.declareObject(TestBean3.class);
        assertNotNull(engine);
        // create instance of Account
        Account acc1 = new Account();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        // create instance of TestBean3
        TestBean3 b = new TestBean3();
        b.setCount(3);
        b.setFloat(10000);
        try {
            long start = System.nanoTime();
            for (int idx=0; idx < 100; idx++) {
                engine.assertObject(acc1,null,false,true);
            }
            int count = engine.getObjectCount();
            for (int idx=0; idx < 100; idx++) {
                engine.assertObject(b,null,false,true);
            }
            int count2 = engine.getObjectCount();
            long end = System.nanoTime();

            assertTrue(true);
            assertTrue(true);
            assertEquals(1,count);
            assertEquals(2,count2);
            System.out.println("Number of facts: " + count);
            System.out.println("Number of facts: " + count2);
            System.out.println("ET: " + (end - start) + " ns");
            double el = ((double)end - (double)start) / 100000;
            System.out.println("ET: " + el + " ms");
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithInterface() {
        System.out.println("start testAssertWithInterface");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        assertNotNull(engine);
        Account acc1 = new Account();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,"account",false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclass() {
        System.out.println("start testAssertWithSubclass");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        assertNotNull(engine);
        BackupAccount acc1 = new BackupAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,"account",false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclass2() {
        System.out.println("\nstart testAssertWithSubclass2");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        engine.declareObject(BackupAccount.class,"backupAccount");
        assertNotNull(engine);
        BackupAccount acc1 = new BackupAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,"backupAccount",false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclass3() {
        System.out.println("\nstart testAssertWithSubclass3");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        engine.declareObject(BackupAccount.class,"backupAccount");
        assertNotNull(engine);
        BackupAccount acc1 = new BackupAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,"account",false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclassWithParent() {
        System.out.println("\nstart testAssertWithSubclassWithParent");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        engine.declareObject(BackupAccount.class,"backupAccount","account");
        assertNotNull(engine);
        BackupAccount acc1 = new BackupAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,"backupAccount",false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclassWithParent2() {
        System.out.println("\nstart testAssertWithSubclassWithParent2");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        engine.declareObject(BackupAccount.class,null,"account");
        assertNotNull(engine);
        BackupAccount acc1 = new BackupAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,null,false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
    
    public void testAssertWithSubclassWithParent3() {
        System.out.println("\nstart testAssertWithSubclassWithParent3");
        Rete engine = new Rete();
        engine.declareObject(IAccount.class,"account");
        engine.declareObject(BackupAccount.class,null,"account");
        engine.declareObject(DeletedAccount.class,null,BackupAccount.class.getName());
        assertNotNull(engine);
        DeletedAccount acc1 = new DeletedAccount();
        acc1.setAccountId("1234");
        acc1.setAccountType("new");
        acc1.setFirst("fName");
        acc1.setLast("lName");
        acc1.setMiddle("m");
        acc1.setOfficeCode("MA");
        acc1.setRegionCode("NE");
        acc1.setStatus("active");
        acc1.setTitle("MR");
        acc1.setUsername("user1");
        try {
            engine.assertObject(acc1,null,false,true);
            assertTrue(true);
            assertEquals(1,engine.getObjectCount());
            System.out.println("Number of facts: " + engine.getObjectCount());
            engine.printWorkingMemory(true,true);
        } catch (AssertException e) {
            fail();
        }
    }
}
