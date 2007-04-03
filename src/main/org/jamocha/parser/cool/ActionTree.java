// Simple class to embed a syntax tree as a jamocha action
// @author Ory Chowaw-Liebman

package org.jamocha.parser.cool;

import org.jamocha.rule.*;
import org.jamocha.rete.*;
import org.jamocha.parser.*;

public class ActionTree implements Action
{
	private Node actions=null;
	
	public void configure(Rete engine, Rule util) 
	{
	};
	
	public void executeAction(Rete engine, Fact[] facts)  
	{
		try
		{ actions.getExpression(); 
		} catch (Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	};
	
	public java.lang.String toPPString() 
	{
		return "Not Implemented yet (ActionTree.toPPString()).";
	};  
	
	public void setActions(Node n)
	{
		actions=n;
	};
}