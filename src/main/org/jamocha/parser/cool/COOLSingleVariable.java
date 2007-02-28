/* Generated By:JJTree: Do not edit this line. CLIPS_SLSingleVariable.java */
/** AST Node representing a Symbol.
	@author jjTree
	@author Ory Chowaw-Liebman
*/
package org.jamocha.parser.cool;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Rete;

public class COOLSingleVariable extends SimpleNode 
{
	DeffunctionParams value=null;

	public COOLSingleVariable(int id) {
		super(id);
	}

	public COOLSingleVariable(COOLParser p, int id) {
		super(p, id);
	}	

	public String toString() {
		return "Single Variable: " + name;
	}

	public boolean compareTree(SimpleNode n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same contents?
		if (getName()!=n.getName()) return false;
		// Yes, they do
		return true;
	}

	// Some nodes may have local data (e.g. classes and functions)
	public void bindLocals(DeffunctionParams [] locals)
	{
		int i;
		System.out.println("Trying to bind: "+name);
		for (i=0;i<locals.length;i++)
		{
			if (locals[i].name.equals(name))
			{
				value=locals[i];
				return;
			}
		}
	}

	public JamochaValue getValue(Rete engine) {
		return value.value;
	}
}
