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
import org.jamocha.rule.Defrule;
import org.jamocha.rule.FunctionAction;


/**
 * @author pete
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MemoryBenchmark {

    private static Random ran = new Random();
    
	/**
	 * 
	 */
	public MemoryBenchmark() {
		super();
	}

    public void parse(Rete engine, CLIPSParser parser, List factlist) {
        Object itm = null;
        try {
            while ( (itm = parser.basicExpr()) != null ) {
                // System.out.println("obj is " + itm.getClass().getName());
                if (itm instanceof Defrule) {
                  Defrule rule = (Defrule)itm;
                  engine.getRuleCompiler().addRule(rule);
                } else if (itm instanceof Deftemplate) {
                  Deftemplate dt = (Deftemplate)itm;
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
            // e.printStackTrace();
        	parser.ReInit(System.in);
        }
    }    
    
    public static void main(String args[]) {
        String rulefile = "./benchmark_files/random_5_w_50Kdata.clp";
        String datafile = "./benchmark_files/test.clp";
        // in case it's run within OptimizeIt and we want to keep the test running
        boolean keepopen = false;

        if (args != null && args.length > 0) {
        	rulefile = args[0];
        }
        if (args.length >= 2) {
            if (args[1].equals("true")) {
                keepopen = true;
            }
        }
        System.out.println("Using file " + rulefile);
        
        
        MemoryBenchmark mb = new MemoryBenchmark();
        ArrayList facts = new ArrayList(50000);
        Runtime rt = Runtime.getRuntime();
        long total1 = rt.totalMemory();
        long free1 = rt.freeMemory();
        long used1 = total1 - free1;
        int count = 100000;
        System.out.println("Used memory before creating engine " + used1 + " bytes " +
                (used1/1024) + " Kb");
        Rete engine = new Rete();

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
            // parser.close();
            // rt.gc();

            long total3 = rt.totalMemory();
            long free3 = rt.freeMemory();
            long used3 = total3 - free3;
            System.out.println("Used memory after loading rules, data and asserting facts " +
                    used3 + " bytes " + (used3/1024) + " Kb " + (used3/1024/1024) + " Mb");
            System.out.println("elapsed time to parse and assert the data " + 
                    (end - start) + " ms");
            engine.printWorkingMemory(true,false);
            
            if (keepopen) {
                
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
