/* Generated By:JJTree: Do not edit this line. COOLWhileFunc.java */
package org.jamocha.parser.cool;

import org.jamocha.parser.*;
import org.jamocha.rete.*;
import org.jamocha.rule.*;

public class COOLWhileFunc extends SimpleNode {
	public COOLWhileFunc(int id) {
		super(id);
	}

	public COOLWhileFunc(COOLParser p, int id) {
		super(p, id);
	}

	public JamochaValue execute() throws EvaluationException
	{
		// Just execute first child. More comples Nodes have to override this anyways.
		return JamochaValue.FALSE;		
	};

}
