/**
 * Copyright 2006 Volker Wetzelaer
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
package test.org.jamocha.messagerouter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Rete;

import junit.framework.TestCase;

public class FunctionsViaMessageRouter extends TestCase{
	

	public void test_assert(){
		System.out.println("");
		System.out.println("Start Assert");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		stringChannel
				.executeCommand("(deftemplate ass_tst(slot name)(slot size))");
		
        
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					//String output = message.getMessage().toString();				
					//System.out.println(output);
				}
			}
		} while (!messages.isEmpty());

		int loop = 10;
		for (int i = 1; i <= loop; ++i) {
			stringChannel.executeCommand("(assert(ass_tst(name tst" + i
					+ ")(size " + i + ")))");
			Thread.yield();
			stringChannel.fillEventList(messages);		
			messages.clear();
		}
		
		stringChannel.executeCommand("(facts)");		
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					String output = message.getMessage().toString();
					
				    Pattern p = Pattern.compile ("(for a total of )([0-9]+)", Pattern.CASE_INSENSITIVE);
				    Matcher m = p.matcher (output);
				    while (m.find()) {
				    	int facts = Integer.valueOf(m.group(2)) - 1;
				    	System.out.println("Vergleich: Fakten in RuleML = " + facts + ", Fakten hinzugefügt = " + loop);
				    	assertEquals(loop, facts);
				     }
				}
			}
		} while (!messages.isEmpty());
		
		System.out.println("End Assert");
		System.out.println("");
	}
	
	public void test_deftemplate() {
		System.out.println("");
		System.out.println("Start Deftemplate");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		
		int loop = 10;
		for (int i = 1; i <= loop; ++i) {
			stringChannel.executeCommand("(deftemplate tst" + i + "(slot name)(slot size))");
			Thread.yield();
			stringChannel.fillEventList(messages);		
			messages.clear();
		}
		
		stringChannel.executeCommand("(templates)");	
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);;
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					String output = message.getMessage().toString();
					
				    Pattern p = Pattern.compile ("(for a total of )([0-9]+)", Pattern.CASE_INSENSITIVE);
				    Matcher m = p.matcher (output);
				    while (m.find()) {
				    	int facts = Integer.valueOf(m.group(2)) - 1;
				    	System.out.println("Vergleich: Templates in RuleML = " + facts + ", Templates hinzugefügt = " + loop);
				    	assertEquals(loop, facts);
				     }
				}
			}
		} while (!messages.isEmpty());
		
		System.out.println("End Deftemplate");
		System.out.println("");
	}	
	
	public void test_defrule() {
		System.out.println("");
		System.out.println("Start Defrule");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		
		stringChannel.executeCommand("(deftemplate tst (slot name)(slot size))");
		
		int loop = 10;
		for (int i = 1; i <= loop; ++i) {
			String command = "(defrule tst" + i + " (tst) => (printout t \"Exists\" crlf))";
			//System.out.println(command);
			stringChannel.executeCommand(command);
			Thread.yield();
			stringChannel.fillEventList(messages);		
			messages.clear();
		}
		
		stringChannel.executeCommand("(rules)");	
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);;
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					String output = message.getMessage().toString();
					
				    Pattern p = Pattern.compile ("(for a total of )([0-9]+)", Pattern.CASE_INSENSITIVE);
				    Matcher m = p.matcher (output);
				    while (m.find()) {
				    	int facts = Integer.valueOf(m.group(2));
				    	System.out.println("Vergleich: Regeln in RuleML = " + facts + ", Regeln hinzugefügt = " + loop);
				    	assertEquals(loop, facts);
				     }
				}
			}
		} while (!messages.isEmpty());
		
		System.out.println("End Defrule");
		System.out.println("");
	}	
	
	
	public void test_fact_slots() {
		System.out.println("");
		System.out.println("Start DefFactSlots");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		
		String command = "(deftemplate fa_sl_tst";
		
		int loop = 10;
		for (int i = 1; i <= loop; ++i) {
			command = command + "(slot tst" + i + ")";
		}
		
		command = command + ")";
		
		stringChannel
			.executeCommand(command);
		
		
		command = ("(assert (fa_sl_tst");
			
		for (int i = 1; i <= loop; ++i) {
			command = command + "(tst" + i + " " + i + ")";
		}
		command = command + "))";
		
		stringChannel
			.executeCommand(command);		
		
		stringChannel.executeCommand("(facts)");	
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);;
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					String output = message.getMessage().toString();
					
				    Pattern p = Pattern.compile ("(for a total of )([0-9]+)", Pattern.CASE_INSENSITIVE);
				    Matcher m = p.matcher (output);
				    while (m.find()) {
				    	int facts = Integer.valueOf(m.group(2)) - 1;
				    	System.out.println("Vergleich: Fakten mit " + loop + " slots in RuleML = " + facts + ", Fakten mit " + loop + " Slots hinzugefügt = 1");
				    	assertEquals(1, facts);
				     }
				}
			}
		} while (!messages.isEmpty());
		
		System.out.println("End DefFactSlots");
		System.out.println("");
	}	
	
	public void test_rule_slots(){
		System.out.println("");
		System.out.println("Start TestRuleSlots");
		Rete engine = new Rete();
		MessageRouter router = engine.getMessageRouter();
		StringChannel stringChannel = router.openChannel("TestChannel");
		List<MessageEvent> messages = new ArrayList<MessageEvent>();
		
		int loop = 2;
		for (int i = 1; i <= loop; ++i) {
			stringChannel.executeCommand("(deftemplate ass_tst" + i + "(slot name)(slot size))");
					
			Thread.yield();
			stringChannel.fillEventList(messages);		
			messages.clear();
		}
        
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					//String output = message.getMessage().toString();				
					//System.out.println(output);
				}
			}
		} while (!messages.isEmpty());

		for (int i = 1; i <= loop; ++i) {
			stringChannel.executeCommand("(assert(ass_tst" + i + "(name tst" + i
					+ ")(size " + i + ")))");
			Thread.yield();
			stringChannel.fillEventList(messages);		
			messages.clear();
		}
		 
		String command = "(defrule rl_tst ";
		for (int i = 1; i <= loop; ++i) {
			command = command + "(ass_tst" + i +") ";
		}
		command = command + " => (printout t \"TEST\" crlf))";

		stringChannel.executeCommand(command);
		System.out.println(command);		
		
		stringChannel.executeCommand("(rules)");		
		do {
	        try{
	            Thread.sleep(2000);
	        }
	        catch(InterruptedException e){
	            System.out.println("Sleep Interrupted");
	        }
			messages.clear();
			Thread.yield();
			stringChannel.fillEventList(messages);
			for (MessageEvent message : messages) {
				if (message.isError()) {
					System.err.println(message.getMessage());
				} else {
					
					String output = message.getMessage().toString();
				    Pattern p = Pattern.compile ("(for a total of )([0-9]+)", Pattern.CASE_INSENSITIVE);
				    Matcher m = p.matcher (output);
				    while (m.find()) {
				    	int facts = Integer.valueOf(m.group(2));
				    	System.out.println("Vergleich: Regeln mit " + loop + " facts in RuleML = " + facts + ", Regeln mit " + loop + " Facts hinzugefügt = 1");
				    	assertEquals(1, facts);
				     }
				}
			}
		} while (!messages.isEmpty());
		
		System.out.println("End TestRuleSlots");
		System.out.println("");
	}	

	
	public static void main(String[] args) {
		FunctionsViaMessageRouter test = new FunctionsViaMessageRouter();
		int loop = 1;
		for (int idx=0; idx < loop; idx++) {
			System.out.println("Start  Tests");
			System.out.println("------------ ");
			test.test_deftemplate();
			test.test_assert();
			test.test_defrule(); 
			test.test_fact_slots();
			test.test_rule_slots();
			System.out.println("------------ ");
			System.out.println("End Tests");
		}
	}
}
