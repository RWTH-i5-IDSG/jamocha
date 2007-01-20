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

import org.jamocha.rete.Evaluate;
import org.jamocha.rete.*;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EvaluateTest extends TestCase {
    
    public EvaluateTest() {
        super();
    }

    public EvaluateTest(String value) {
        super(value);
    }
    
    public void testLessEqual1() {
        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        System.out.println("---- evaluate int to int");
        assertEquals(true,Evaluate.evaluateLessEqual(int1,int2));
        System.out.println(int1.intValue() + " <= " + int2.intValue() + " is " +
                Evaluate.evaluateLessEqual(int1,int2));
        
        assertEquals(false,Evaluate.evaluateLessEqual(int2,int1));
        System.out.println(int2.intValue() + " <= " + int1.intValue() + " is " +
                Evaluate.evaluateLessEqual(int2,int1));
        
        assertEquals(true,Evaluate.evaluateLessEqual(int1,int1));
        System.out.println(int1.intValue() + " <= " + int1.intValue() + " is " +
                Evaluate.evaluateLessEqual(int1,int1));
        
        // ----- now test using Short ----- //
        Short sh1 = new Short("1");
        Short sh2 = new Short("2");
        System.out.println("---- evaluate short to short");
        assertEquals(true,Evaluate.evaluateLessEqual(sh1,sh2));
        System.out.println(sh1.shortValue() + " <= " + sh2.shortValue() + " is " +
                Evaluate.evaluateLessEqual(sh1,sh2));
        
        assertEquals(false,Evaluate.evaluateLessEqual(sh2,sh1));
        System.out.println(sh2.shortValue() + " <= " + sh1.shortValue() + " is " +
                Evaluate.evaluateLessEqual(sh2,sh1));
        assertEquals(true,Evaluate.evaluateLessEqual(sh1,sh1));
        System.out.println(sh1.shortValue() + " <= " + sh1.shortValue() + " is " +
                Evaluate.evaluateLessEqual(sh1,sh1));
        
        // ----- now test using Long ----- //
        Long ln1 = new Long(10001);
        Long ln2 = new Long(10010);
        System.out.println("---- evaluate long to long");
        assertEquals(true,Evaluate.evaluateLessEqual(ln1,ln2));
        System.out.println(ln1.longValue() + " <= " + ln2.longValue() + " is " +
                Evaluate.evaluateLessEqual(ln1,ln2));
        
        assertEquals(false,Evaluate.evaluateLessEqual(ln2,ln1));
        System.out.println(ln2.longValue() + " <= " + ln1.longValue() + " is " +
                Evaluate.evaluateLessEqual(ln2,ln1));
        assertEquals(true,Evaluate.evaluateLessEqual(ln1,ln1));
        System.out.println(ln1.doubleValue() + " <= " + ln1.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(ln1,ln1));
        
        // ----- now test using Float ----- //
        Float fl1 = new Float(10001.01);
        Float fl2 = new Float(10002.01);
        System.out.println("---- evaluate float to float");
        assertEquals(true,Evaluate.evaluateLessEqual(fl1,fl2));
        System.out.println(fl1.floatValue() + " <= " + fl2.floatValue() + " is " +
                Evaluate.evaluateLessEqual(fl1,fl2));
        
        assertEquals(false,Evaluate.evaluateLessEqual(fl2,fl1));
        System.out.println(fl2.floatValue() + " <= " + fl1.floatValue() + " is " +
                Evaluate.evaluateLessEqual(fl2,fl1));
        
        assertEquals(true,Evaluate.evaluateLessEqual(fl1,fl1));
        System.out.println(fl1.floatValue() + " <= " + fl1.floatValue() + " is " +
                Evaluate.evaluateLessEqual(fl1,fl1));
        
        // ----- now test using Double ----- //
        Double db1 = new Double(1000.00);
        Double db2 = new Double(2000.00);
        System.out.println("---- evaluate double to double");
        assertEquals(true,Evaluate.evaluateLessEqual(db1,db2));
        System.out.println(db1.doubleValue() + " <= " + db2.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(db1,db2));
        
        assertEquals(false,Evaluate.evaluateLessEqual(db2,db1));
        System.out.println(db2.doubleValue() + " <= " + db1.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(db2,db1));
        
        assertEquals(true,Evaluate.evaluateLessEqual(db1,db1));
        System.out.println(db1.doubleValue() + " <= " + db1.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(db1,db1));
        
    }
    
    /**
     * the method will test comparing different numeric types to make sure
     * it all works correctly
     */
    public void testLessEqual2() {
        System.out.println("testLessEqual2 -------");
        Integer int1 = new Integer(1);

        Short sh1 = new Short("2");
        System.out.println("---- evaluate int to short");
        assertEquals(true,Evaluate.evaluateLessEqual(int1,sh1));
        System.out.println(int1.intValue() + " <= " + sh1.longValue() + " is " +
                Evaluate.evaluateLessEqual(int1,sh1));
        
        assertEquals(false,Evaluate.evaluateLessEqual(sh1,int1));
        System.out.println(sh1.intValue() + " <= " + int1.intValue() + " is " +
                Evaluate.evaluateLessEqual(sh1,int1));
        
        Long ln1 = new Long(2);
        System.out.println("---- evaluate int to long");
        assertEquals(true,Evaluate.evaluateLessEqual(int1,ln1));
        System.out.println(int1.intValue() + " <= " + ln1.longValue() + " is " +
                Evaluate.evaluateLessEqual(int1,ln1));
        
        assertEquals(false,Evaluate.evaluateLessEqual(ln1,int1));
        System.out.println(ln1.intValue() + " <= " + int1.intValue() + " is " +
                Evaluate.evaluateLessEqual(ln1,int1));
        
        // ----- now test using float ----- //
        Float fl1 = new Float(10001);
        System.out.println("---- evaluate int to float");
        assertEquals(true,Evaluate.evaluateLessEqual(int1,fl1));
        System.out.println(int1.longValue() + " <= " + fl1.longValue() + " is " +
                Evaluate.evaluateLessEqual(ln1,fl1));
        
        assertEquals(false,Evaluate.evaluateLessEqual(fl1,int1));
        System.out.println(fl1.longValue() + " <= " + int1.longValue() + " is " +
                Evaluate.evaluateLessEqual(fl1,int1));
        
        // ----- now test using Double ----- //
        Double db1 = new Double(1000.00);
        System.out.println("---- evaluate int to double");
        assertEquals(true,Evaluate.evaluateLessEqual(int1,db1));
        System.out.println(int1.doubleValue() + " <= " + db1.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(db1,db1));
        
        assertEquals(false,Evaluate.evaluateLessEqual(db1,int1));
        System.out.println(db1.doubleValue() + " <= " + int1.doubleValue() + " is " +
                Evaluate.evaluateLessEqual(db1,int1));
        
    }
    
    public void testLess1() {
        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        System.out.println("---- evaluate int to int");
        assertEquals(true,Evaluate.evaluateLess(int1,int2));
        System.out.println(int1.intValue() + " < " + int2.intValue() + " is " +
                Evaluate.evaluateLess(int1,int2));
        
        assertEquals(false,Evaluate.evaluateLess(int2,int1));
        System.out.println(int2.intValue() + " < " + int1.intValue() + " is " +
                Evaluate.evaluateLess(int2,int1));
        
        assertEquals(false,Evaluate.evaluateLess(int1,int1));
        System.out.println(int1.intValue() + " < " + int1.intValue() + " is " +
                Evaluate.evaluateLess(int1,int1));
        
        // ----- now test using Short ----- //
        Short sh1 = new Short("1");
        Short sh2 = new Short("2");
        System.out.println("---- evaluate short to short");
        assertEquals(true,Evaluate.evaluateLess(sh1,sh2));
        System.out.println(sh1.shortValue() + " < " + sh2.shortValue() + " is " +
                Evaluate.evaluateLess(sh1,sh2));
        
        assertEquals(false,Evaluate.evaluateLess(sh2,sh1));
        System.out.println(sh2.shortValue() + " < " + sh1.shortValue() + " is " +
                Evaluate.evaluateLess(sh2,sh1));
        assertEquals(false,Evaluate.evaluateLess(sh1,sh1));
        System.out.println(sh1.shortValue() + " < " + sh1.shortValue() + " is " +
                Evaluate.evaluateLess(sh1,sh1));
        
        // ----- now test using Long ----- //
        Long ln1 = new Long(10001);
        Long ln2 = new Long(10010);
        System.out.println("---- evaluate long to long");
        assertEquals(true,Evaluate.evaluateLess(ln1,ln2));
        System.out.println(ln1.longValue() + " < " + ln2.longValue() + " is " +
                Evaluate.evaluateLess(ln1,ln2));
        
        assertEquals(false,Evaluate.evaluateLess(ln2,ln1));
        System.out.println(ln2.longValue() + " < " + ln1.longValue() + " is " +
                Evaluate.evaluateLess(ln2,ln1));
        assertEquals(false,Evaluate.evaluateLess(ln1,ln1));
        System.out.println(ln1.doubleValue() + " < " + ln1.doubleValue() + " is " +
                Evaluate.evaluateLess(ln1,ln1));
        
        // ----- now test using Float ----- //
        Float fl1 = new Float(10001.01);
        Float fl2 = new Float(10002.01);
        System.out.println("---- evaluate float to float");
        assertEquals(true,Evaluate.evaluateLess(fl1,fl2));
        System.out.println(fl1.floatValue() + " < " + fl2.floatValue() + " is " +
                Evaluate.evaluateLess(fl1,fl2));
        
        assertEquals(false,Evaluate.evaluateLess(fl2,fl1));
        System.out.println(fl2.floatValue() + " < " + fl1.floatValue() + " is " +
                Evaluate.evaluateLess(fl2,fl1));
        
        assertEquals(false,Evaluate.evaluateLess(fl1,fl1));
        System.out.println(fl1.floatValue() + " < " + fl1.floatValue() + " is " +
                Evaluate.evaluateLess(fl1,fl1));
        
        // ----- now test using Double ----- //
        Double db1 = new Double(1000.00);
        Double db2 = new Double(2000.00);
        System.out.println("---- evaluate double to double");
        assertEquals(true,Evaluate.evaluateLess(db1,db2));
        System.out.println(db1.doubleValue() + " < " + db2.doubleValue() + " is " +
                Evaluate.evaluateLess(db1,db2));
        
        assertEquals(false,Evaluate.evaluateLess(db2,db1));
        System.out.println(db2.doubleValue() + " < " + db1.doubleValue() + " is " +
                Evaluate.evaluateLess(db2,db1));
        
        assertEquals(false,Evaluate.evaluateLess(db1,db1));
        System.out.println(db1.doubleValue() + " < " + db1.doubleValue() + " is " +
                Evaluate.evaluateLess(db1,db1));
        
    }
    
    public void testBoolean1() {
    	Boolean t = new Boolean(true);
    	String f = "false";
    	String t2 = "true";
    	Boolean f2 = new Boolean(false);
    	assertFalse( Evaluate.evaluateEqual(t,f));
    	assertFalse( Evaluate.evaluateEqual(t2,f2));
    	assertTrue( Evaluate.evaluateEqual(t,t2));
    	assertTrue( Evaluate.evaluateEqual(f,f2));
    	
    	String t3 = "TRUE";
    	String f3 = "FALSE";
    	assertFalse( Evaluate.evaluateEqual(t,t3));
    	assertFalse( Evaluate.evaluateEqual(f2,f3));
    }
}
