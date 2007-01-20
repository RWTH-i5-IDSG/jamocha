/*
 * Created on Aug 29, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package woolfel.rete;

import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Rete;
import org.jamocha.rule.Defrule;
import org.jamocha.rete.*;
import org.jamocha.rule.*;

import junit.framework.TestCase;


/**
 * @author pete
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoadRulesetTest extends TestCase {

	/**
	 * 
	 */
	public LoadRulesetTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public LoadRulesetTest(String arg0) {
		super(arg0);
	}

    public void testLoadJoinSample13() {
        Rete engine = new Rete();
        engine.loadRuleset("./benchmark_files/join_sample13.clp");
        Collection rules = engine.getCurrentFocus().getAllRules();
        int count = rules.size();
        Iterator itr = rules.iterator();
        while (itr.hasNext()) {
            Defrule r = (Defrule)itr.next();
            System.out.println(r.toPPString());
        }
        assertEquals(0,count);
    }

    public void testLoadTest() {
        Rete engine = new Rete();
        engine.loadRuleset("./benchmark_files/test.clp");
        Collection rules = engine.getCurrentFocus().getAllRules();
        int count = rules.size();
        Iterator itr = rules.iterator();
        while (itr.hasNext()) {
            Defrule r = (Defrule)itr.next();
            System.out.println(r.toPPString());
        }
        assertEquals(0,count);
    }
}
