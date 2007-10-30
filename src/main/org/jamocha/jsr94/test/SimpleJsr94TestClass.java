package org.jamocha.jsr94.test;

import java.util.LinkedList;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;

public class SimpleJsr94TestClass {

	public static void main(String[] args) throws Exception{
		
		// load the RuleServiceProvider for the vendor
		Class.forName( "org.jamocha.jsr94.JamochaRuleServiceProvider" );
		RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider("org.jamocha.jsr94");
		// create a stateless RuleSession
		RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
		StatelessRuleSession srs = (StatelessRuleSession) ruleRuntime.createRuleSession( "fooboo", null, RuleRuntime.STATELESS_SESSION_TYPE );
		// execute all the rules
		List inputList = new LinkedList();
		inputList.add( new String( "Foo" ) );
		inputList.add( new String( "Bar" ) );
		inputList.add( new Integer( 5 ) );
		inputList.add( new Float( 6 ) );
		List resultList = srs.executeRules( inputList );
		System.out.println( "executeRules: " + resultList );
		// release the session
		srs.release();
	}	
}
