/* Generated By:JJTree: Do not edit this line. CLIPS_SLMultiVariable.java */
/** AST Node representing a Symbol.
	@author jjTree
	@author Ory Chowaw-Liebman
*/
package org.jamocha.parser.cool;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

public class COOLMultiVariable extends SimpleNode 
{
	DeffunctionParams value;

	public COOLMultiVariable(int id) {
		super(id);
	}

	public COOLMultiVariable(COOLParser p, int id) {
		super(p, id);
	}
	
	public String toString() {
		return "Multi Variable: " + name;
	}

	public boolean compareTree(SimpleNode n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same name?
		if (getName()!=n.getName()) return false;
		// Yes, they do
		return true;
	}
	// Some nodes may have local data (e.g. classes and functions)
	public void bindLocals(DeffunctionParams [] locals)
	{
		int i;
		for (i=0;i<locals.length;i++)
			if (locals[i].name.equals(name)) { value=locals[i]; return; };
	}

	public JamochaValue execute() {
		return value.value;
	}
}
