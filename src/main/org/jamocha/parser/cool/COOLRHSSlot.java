/* Generated By:JJTree: Do not edit this line. COOLRHSSlot.java */
package org.jamocha.parser.cool;

import org.jamocha.parser.*;
import org.jamocha.rete.*;

public class COOLRHSSlot extends SimpleNode {
	public COOLRHSSlot(int id) {
		super(id);
	}

	public COOLRHSSlot(COOLParser p, int id) {
		super(p, id);
	}

	// Build slots and return.
	public JamochaValue execute() throws EvaluationException
	{
		if (jjtGetNumChildren()==1)
		{	// Single Field Slot
			return JamochaValue.newSlot(new Slot(name,jjtGetChild(0).execute()));
		} else
		{	// Multifield slot
			int i;
			JamochaValue [] v = new JamochaValue[jjtGetNumChildren()];
			for (i=0;i<jjtGetNumChildren();i++)	v[i]=jjtGetChild(i).execute();
			return JamochaValue.newSlot(new MultiSlot(name,v));
		}
		//return JamochaValue.FALSE;		
	};
	
}
