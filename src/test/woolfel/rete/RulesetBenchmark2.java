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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jamocha.parser.clips.CLIPSParser;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnValue;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.AssertFunction;
import org.jamocha.rete.functions.ShellFunction;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.FunctionAction;

import woolfel.examples.model.Account;
import woolfel.examples.model.Transaction;

/**
 * @author Peter Lin
 *
 * RulesetBenchmark2 measure the performance of node sharing.
 */
public class RulesetBenchmark2 {

    private static Random ran = new Random();
    
	/**
	 * 
	 */
	public RulesetBenchmark2() {
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
					if (itm instanceof ShellFunction) {
						ShellFunction sf = (ShellFunction)itm;
						if (sf.getName().equals(AssertFunction.ASSERT)) {
							factlist.add(sf.getParameters()[0].getValue());
						} else {
							ReturnVector rv = ((Function) itm).executeFunction(engine,
									null);
							Iterator itr = rv.getIterator();
							while (itr.hasNext()) {
								ReturnValue rval = (ReturnValue) itr.next();
								System.out.println(rval.getStringValue());
							}
						}
					} else {
						ReturnVector rv = ((Function) itm).executeFunction(engine,
								null);
						Iterator itr = rv.getIterator();
						while (itr.hasNext()) {
							ReturnValue rval = (ReturnValue) itr.next();
							System.out.println(rval.getStringValue());
						}
					}
				}
			}
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }    
    
    public static void main(String args[]) {
        String rulefile = "./benchmark_files/share_5nodes.clp";
        boolean keepopen = false;
        int fcount = 50000;
        if (args != null && args.length > 0) {
        	rulefile = args[0];
        }
        if (args.length >= 2) {
            if (args[1].equals("true")) {
                keepopen = true;
            }
        }
        System.out.println("Using file " + rulefile);
        
        RulesetBenchmark2 mb = new RulesetBenchmark2();
        long begin = System.currentTimeMillis();
        long totalET = 0;
        long parseET = 0;
        ArrayList facts = new ArrayList(50000);
        Runtime rt = Runtime.getRuntime();
        long total1 = rt.totalMemory();
        long free1 = rt.freeMemory();
        long used1 = total1 - free1;
        int loopcount = 5;
        System.out.println("Used memory before creating engine " + used1 + " bytes " +
                (used1/1024) + " Kb");
        for (int loop=0; loop < loopcount; loop++) {
            System.out.println(" ---------------------------------- ");
            Rete engine = new Rete();
            facts.clear();
            // declare the objects
            engine.declareObject(Account.class,"account");
            engine.declareObject(Transaction.class,"transaction");

            long total2 = rt.totalMemory();
            long free2 = rt.freeMemory();
            long used2 = total2 - free2;
            System.out.println("Used memory after creating engine " + used2 + " bytes " +
                    (used2/1024) + " Kb");

            try {
                FileInputStream freader = new FileInputStream(rulefile);
                CLIPSParser parser = new CLIPSParser(engine,freader);
                long start = System.currentTimeMillis();
                mb.parse(engine,parser,facts);
                long end = System.currentTimeMillis();
                long el = end - start;
                parser.close();
                rt.gc();
                parseET += el;

                long total3 = rt.totalMemory();
                long free3 = rt.freeMemory();
                long used3 = total3 - free3;
                System.out.println("Used memory after loading rules, and parsing data " +
                        (used3/1024) + " Kb " + (used3/1024/1024) + " Mb");
                System.out.println("elapsed time to parse the rules and data " + 
                        el + " ms");
                
                System.out.println("Number of rules: " + 
                        engine.getCurrentFocus().getRuleCount());
                // now create the facts
                Account acc = new Account();
                acc.setAccountType("standard");
                facts.add(acc);
                for (int i=0; i < fcount; i++) {
                	Transaction tx = new Transaction();
                	tx.setAccountId("acc" + i);
                	tx.setExchange("NYSE");
                	tx.setIssuer("AAA");
                	tx.setShares(100.00);
                	tx.setSecurityType("stocks");
                	tx.setSubIndustryID(25201010);
                	facts.add(tx);
                }
                Iterator itr = facts.iterator();
                long start2 = System.currentTimeMillis();
                while (itr.hasNext()) {
                	Object d = itr.next();
                	if (d instanceof Account) {
                        engine.assertObject(d,"account",false,true);
                	} else {
                        engine.assertObject(d,"transaction",false,false);
                	}
                }
                int actCount = engine.getActivationList().size();
                long end2 = System.currentTimeMillis();
                long et2 = end2 - start2;
                totalET += et2;
                // now fire the rules
                long start3 = 0;
                long end3 = 0;
                int fired = 0;
                try {
                    start3 = System.currentTimeMillis();
                    fired = engine.fire();
                    end3 = System.currentTimeMillis();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                facts.clear();
                
                long total4 = rt.totalMemory();
                long free4 = rt.freeMemory();
                long used4 = total4 - free4;

                System.out.println("");
                System.out.println("Number of facts - " + engine.getObjectCount());
                System.out.println("Time to assert facts " + et2 + " ms");
                System.out.println("Used memory after assert " +
                        (used4/1024) + " Kb " + (used4/1024/1024) + " Mb");
                engine.printWorkingMemory(true,false);
                System.out.println("number of activations " + actCount);
                System.out.println("rules fired " + fired);
                System.out.println("time to fire rules " + (end3 - start3) + " ms");
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (AssertException e) {
            	e.printStackTrace();
            }
            engine.close();
            engine = null;
            rt.gc();
        }
        long finished = System.currentTimeMillis();
        System.out.println("average parse ET - " + parseET/loopcount + " ms");
        System.out.println("average assert ET - " + totalET/loopcount + " ms");
        System.out.println("total run time " + (finished - begin) + " ms");
    }
}
