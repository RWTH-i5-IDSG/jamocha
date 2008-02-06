package org.jamocha.jsr94.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class SimpleJsr94TestClass {

	public static void main(String[] args) throws Exception{
		
		// load the RuleServiceProvider for the vendor
		Class.forName( "org.jamocha.jsr94.JamochaRuleServiceProvider" );
		RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider("org.jamocha.jsr94");
		
        RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();
        
        File file = new File("/home/free-radical/ruleset.xml");
        InputStream inStream = new FileInputStream(file);
        RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider(null).createRuleExecutionSet(inStream, null);
        inStream.close();

        String uri = res1.getName();
        ruleAdministrator.registerRuleExecutionSet(uri, res1, null);
		
		// create a stateless RuleSession
		RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
		StatelessRuleSession srs = (StatelessRuleSession) ruleRuntime.createRuleSession( uri, null, RuleRuntime.STATELESS_SESSION_TYPE );
		// execute all the rules
		List inputList = new LinkedList();
		
		inputList.add( new Wurst("Bratwurst", 400) );
		inputList.add( new Wurst("Bockwurst", 300) );
		inputList.add( new Wurst("Käsegriller", 200) );
		
		System.out.println( "executeRules: " + inputList );
		List resultList = srs.executeRules( inputList );
		System.out.println( "executeRules: " + resultList );
		// release the session
		//srs.release();
	}	
}
