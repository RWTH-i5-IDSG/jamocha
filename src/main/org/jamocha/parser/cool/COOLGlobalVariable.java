/* Generated By:JJTree: Do not edit this line. CLIPS_SLGlobalVariable.java */
/** AST Node representing a Global variable.
	@author jjTree
	@author Ory Chowaw-Liebman
*/
package org.jamocha.parser.cool;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Rete;

public class COOLGlobalVariable extends SimpleNode 
{

	public COOLGlobalVariable(int id) {
		super(id);
	}

	public COOLGlobalVariable(COOLParser p, int id) {
		super(p, id);
	}
	
	public String toString() {
		return "Global Variable: " + name;
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

	public JamochaValue getValue(Rete engine) {
		//return parser.getGlobalVar(name);
		JamochaValue v=engine.getDefglobalValue(name);
		if (v==null) return JamochaValue.NIL;
		else return v;
	}
}
