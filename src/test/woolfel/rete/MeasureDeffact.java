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
import java.util.Random;

import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;

import woolfel.examples.model.Account;

/**
 * @author Peter Lin
 *
 * Basic test for measuring memory usage by objects and facts.
 */
public class MeasureDeffact {

    private static Random ran = new Random();
    
	/**
	 * 
	 */
	public MeasureDeffact() {
		super();
	}

    public static void main(String args[]) {
        ArrayList objects = new ArrayList();
        Runtime rt = Runtime.getRuntime();
        long total1 = rt.totalMemory();
        long free1 = rt.freeMemory();
        long used1 = total1 - free1;
        int count = 50000;
        System.out.println("Used memory before creating objects " + used1 + " bytes " +
                (used1/1024) + " Kb");
        for (int idx=0; idx < count; idx++) {
            Account acc = new Account();
            acc.setAccountId(String.valueOf(ran.nextInt(100000)));
            acc.setAccountType(String.valueOf(ran.nextInt(100000)));
            acc.setFirst(String.valueOf(ran.nextInt(100000)));
            acc.setLast(String.valueOf(ran.nextInt(100000)));
            acc.setMiddle(String.valueOf(ran.nextInt(100000)));
            acc.setOfficeCode(String.valueOf(ran.nextInt(100000)));
            acc.setRegionCode(String.valueOf(ran.nextInt(100000)));
            acc.setStatus(String.valueOf(ran.nextInt(100000)));
            acc.setTitle(String.valueOf(ran.nextInt(100000)));
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
        Rete engine = new Rete();
        engine.declareObject(Account.class);
        Iterator itr = objects.iterator();
        long start = System.currentTimeMillis();
        try {
            while (itr.hasNext()) {
                engine.assertObject(itr.next(),null,false,false);
            }
        } catch (AssertException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long total3 = rt.totalMemory();
        long free3 = rt.freeMemory();
        long used3 = total3 - free3;
        rt.gc();
        System.out.println("Used memory after asserting objects " + used3 + " bytes " +
                (used3/1024) + " Kb " + (used3/1024/1024) + " Mb");
        System.out.println("number of facts " + engine.getObjectCount() );
        System.out.println("memory used by facts " + (used3 - used2)/1024/1024 + " Mb" );
        System.out.println("elapsed time is " + (end - start) + " ms");
    }
}
