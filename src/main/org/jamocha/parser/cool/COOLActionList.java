/* Generated By:JJTree: Do not edit this line. COOLActionList.java */
package org.jamocha.parser.cool;

import org.jamocha.parser.*;
import org.jamocha.rete.Rete;

public class COOLActionList extends SimpleNode {
	public COOLActionList(int id) {
		super(id);
	}

	public COOLActionList(COOLParser p, int id) {
		super(p, id);
	}

	public JamochaValue getValue(Rete engine) throws EvaluationException
	{
		int i;
		if (jjtGetNumChildren()==0) return JamochaValue.FALSE;
		if (jjtGetNumChildren()==1) return  jjtGetChild(0).getValue(engine);
		for (i=0;i<jjtGetNumChildren();i++) jjtGetChild(i).getValue(engine);		
		return JamochaValue.TRUE;
	};

}
