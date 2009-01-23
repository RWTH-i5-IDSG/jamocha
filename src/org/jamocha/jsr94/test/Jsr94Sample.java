package org.jamocha.jsr94.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatefulRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class Jsr94Sample {

	public static void main(String[] args) throws Exception{
		
		// load the RuleServiceProvider for the vendor
		Class.forName( "org.jamocha.communication.jsr94.JamochaRuleServiceProvider" );
		RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider("http://www.jamocha.org");
		
        RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();
        
        
        File file = new File("src/org/jamocha/jsr94/test/ruleset.xml");
        InputStream inStream = new FileInputStream(file);
        RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider(null).createRuleExecutionSet(inStream, null);
        inStream.close();

        String uri = res1.getName();
        ruleAdministrator.registerRuleExecutionSet(uri, res1, null);
		
		// create a stateless RuleSession
		RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
		StatefulRuleSession srs = (StatefulRuleSession) ruleRuntime.createRuleSession( uri, null, RuleRuntime.STATEFUL_SESSION_TYPE );
		// execute all the rules
		List inputList = new LinkedList();
		
		inputList.add( new Customer("Hans Mustercustomer", 1000) );
		inputList.add( new Invoice("Pizzarechnung",101,"unpaid") );
		inputList.add( new Invoice("Wasser",201,"unpaid") );
		inputList.add( new Invoice("Antoniusstrasse",301,"unpaid") );
		inputList.add( new Invoice("Kaufland",401,"unpaid") );
		
		System.out.println( "the list before exec: \n" + inputList + "\n\n");
		srs.addObjects(inputList);
		srs.executeRules();
		List resultList = srs.getObjects();
		System.out.println( "the list after exec : \n" + resultList + "\n\n");

		// release the session
		srs.release();
	}	
}
