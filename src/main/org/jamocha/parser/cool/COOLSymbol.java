/* Generated By:JJTree: Do not edit this line. CLIPS_SLSymbol.java */
package org.jamocha.parser.cool;

import org.jamocha.parser.*;
import org.jamocha.rete.Rete;

public class COOLSymbol extends SimpleNode {

	public COOLSymbol(int id) {
		super(id);
	}

	public COOLSymbol(COOLParser p, int id) {
		super(p, id);
	}
	
	public String toString() {
		return "Symbol: " + name;
	}

	public boolean compareTree(SimpleNode n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same name (which is always the same as the symbol)?
		if (getName()!=n.getName()) return false;
		// Yes, they do
		return true;
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException
	{
		return new JamochaValue(JamochaType.IDENTIFIER,name);
	}
}
