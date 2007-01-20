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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnValue;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.CompileRuleException;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.FunctionAction;

import woolfel.examples.model.Account;

/**
 * @author Peter Lin
 *
 * Basic test for measuring memory usage by objects and facts.
 */
public class AssertWRules {

    private static Random ran = new Random();
    
	/**
	 * 
	 */
	public AssertWRules() {
		super();
	}

    public void parse(Rete engine, CLIPSParser parser, List factlist) {
		Object itm = null;
		try {
			while ((itm = parser.basicExpr()) != null) {
				// System.out.println("obj is " + itm.getClass().getName());
				if (itm instanceof Defrule) {
					Defrule rule = (Defrule) itm;
					engine.getRuleCompiler().addRule(rule);
				} else if (itm instanceof Deftemplate) {
					Deftemplate dt = (Deftemplate) itm;
					System.out.println("template=" + dt.getName());
					engine.declareTemplate(dt);
				} else if (itm instanceof FunctionAction) {
					FunctionAction fa = (FunctionAction) itm;

				} else if (itm instanceof Function) {
					ReturnVector rv = ((Function) itm).executeFunction(engine,
							null);
					Iterator itr = rv.getIterator();
					while (itr.hasNext()) {
						ReturnValue rval = (ReturnValue) itr.next();
						System.out.println(rval.getStringValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    
    
    public static void main(String args[]) {
        String rulefile = "./benchmark_files/account_5.clp";

        ArrayList objects = new ArrayList();
        AssertWRules awr = new AssertWRules();
        
        Runtime rt = Runtime.getRuntime();
        long total1 = rt.totalMemory();
        long free1 = rt.freeMemory();
        long used1 = total1 - free1;
        int count = 5000;
        System.out.println("loading file " + rulefile);
        System.out.println("Used memory before creating objects " + used1 + " bytes " +
                (used1/1024) + " Kb");
        for (int idx=0; idx < count; idx++) {
            Account acc = new Account();
            acc.setAccountId("acc" + idx);
            // acc.setAccountId("acc" + ran.nextInt(4));
            acc.setAccountType("standard");
            acc.setFirst(String.valueOf(ran.nextInt(100000)));
            acc.setLast(String.valueOf(ran.nextInt(100000)));
            acc.setMiddle(String.valueOf(ran.nextInt(100000)));
            acc.setOfficeCode(String.valueOf(ran.nextInt(100000)));
            acc.setRegionCode(String.valueOf(ran.nextInt(100000)));
            acc.setStatus("active");
            acc.setTitle("mr");
            acc.setUsername(String.valueOf(ran.nextInt(100000)));
            acc.setAreaCode(String.valueOf(ran.nextInt(999)));
            acc.setExchange(String.valueOf(ran.nextInt(999)));
            acc.setNumber(String.valueOf(ran.nextInt(999)));
            acc.setExt(String.valueOf(ran.nextInt(9999)));
            objects.add(acc);
        }
        long total2 = rt.totalMemory();
        long free2 = rt.freeMemory();
        long used2 = total2 - free2;
        System.out.println("Used memory after creating objects " + used2 + " bytes " +
                (used2/1024) + " Kb " + (used2/1024/1024) + " Mb");
        int loop = 5;
        long ETTotal = 0;
        for (int idx=0; idx < loop; idx++) {
            Rete engine = new Rete();
            engine.declareObject(Account.class,"Account");

            try {
                FileInputStream freader = new FileInputStream(rulefile);
                CLIPSParser parser = new CLIPSParser(engine,freader);
                Object item = null;
                ArrayList list = new ArrayList();
                long start = System.currentTimeMillis();
                awr.parse(engine,parser,list);
                long end = System.currentTimeMillis();
                long el = end - start;
                // parser.close();
                rt.gc();
                System.out.println("time to parse rules " + el + " ms");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Iterator itr = objects.iterator();
            long start2 = System.currentTimeMillis();
            try {
                while (itr.hasNext()) {
                    engine.assertObject(itr.next(),"Account",false,false);
                }
            } catch (AssertException e) {
                e.printStackTrace();
            }
            long end2 = System.currentTimeMillis();
            long start3 = System.currentTimeMillis();
            int fired = 0;
            try {
                fired = engine.fire();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end3 = System.currentTimeMillis();
            long total3 = rt.totalMemory();
            long free3 = rt.freeMemory();
            long used3 = total3 - free3;
            System.out.println("Number of rules: " + 
                    engine.getCurrentFocus().getRuleCount());
            System.out.println("rules fired " + fired);
            System.out.println("Used memory after asserting objects " + used3 + " bytes " +
                    (used3/1024) + " Kb " + (used3/1024/1024) + " Mb");
            System.out.println("number of facts " + engine.getObjectCount() );
            System.out.println("memory used by facts " + (used3 - used2)/1024/1024 + " Mb" );
            System.out.println("elapsed time to assert " + (end2 - start2) + " ms");
            System.out.println("elapsed time to fire " + (end3 - start3) + " ms");
            ETTotal += (end2 - start2);
            engine.printWorkingMemory(true,false);
            engine.close();
            engine.clearAll();
            rt.gc();
        }
        System.out.println("Average ET to assert " + (ETTotal/loop) + " ms");
    }
}
