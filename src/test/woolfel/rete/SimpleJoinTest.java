/*
 * Copyright 2002-2006 Peter Lin & RuleML.
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
import java.util.Random;

import org.jamocha.rete.Rete;

import woolfel.examples.model.Account4;
import woolfel.examples.model.Transaction;
import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * SimpleJoin test is used to measure basic join performance for very
 * simple cases.
 */
public class SimpleJoinTest extends TestCase {

	/**
	 * 
	 */
	public SimpleJoinTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public SimpleJoinTest(String arg0) {
		super(arg0);
	}

	public void testFiveRules() {
		int objCount = 25000;
        Random ran = new Random();
		ArrayList facts = new ArrayList();
		// loop and create account and transaction objects
		for (int idx=0; idx < objCount; idx++) {
            Account4 acc = new Account4();
            acc.setAccountId("acc" + idx);
            acc.setAccountType(String.valueOf(ran.nextInt(100000)));
            acc.setFirst(String.valueOf(ran.nextInt(100000)));
            acc.setLast(String.valueOf(ran.nextInt(100000)));
            acc.setMiddle(String.valueOf(ran.nextInt(100000)));
            acc.setStatus(String.valueOf(ran.nextInt(100000)));
            acc.setTitle(String.valueOf(ran.nextInt(100000)));
            acc.setUsername(String.valueOf(ran.nextInt(100000)));
            acc.setCountryCode("US");
            acc.setCash(1298.00);
			facts.add(acc);
			Transaction tx = new Transaction();
			tx.setAccountId("acc" + idx);
			tx.setTotal(1200000);
			facts.add(tx);
		}
		System.out.println("created " + objCount + " Accounts and Transactions");
        Rete engine = new Rete();
        engine.declareObject(Account4.class);
        engine.declareObject(Transaction.class);
        System.out.println("delcare the objects");
	}
}
