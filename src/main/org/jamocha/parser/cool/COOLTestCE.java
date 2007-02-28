/* Generated By:JJTree: Do not edit this line. COOLTestCE.java */
package org.jamocha.parser.cool;

import org.jamocha.rule.*;
import org.jamocha.parser.*;
import org.jamocha.rete.*;

public class COOLTestCE extends SimpleNode {
	TestCondition ce=null;

	public COOLTestCE(int id) {
		super(id);
	}

	public COOLTestCE(COOLParser p, int id) {
		super(p, id);
	}

	public void setCondition(TestCondition test)
	{ ce=test; }
	
	// This just sets the function in the CE, since the function may not exist at parse time
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		int i;
		JamochaValue ret=null;
		ValueParam params[] = new ValueParam[jjtGetNumChildren()];
		Function func=engine.findFunction(name);
		// Return false if function not found (CLIPS does not throw exceptions here)
		if (func==null)
		{
			//System.out.println("No Function");
			return JamochaValue.FALSE;
		}
//		for (i=0;i<jjtGetNumChildren();i++)
//		{
//			//ret=jjtGetChild(i).execute();
//			params[i]=new ValueParam(jjtGetChild(i).execute());
//		}
//		ret=func.executeFunction(parser.getRete(),params);
		ce.setFunction(func);
		return JamochaValue.TRUE;
	}
}
