/* Generated By:JJTree: Do not edit this line. COOLDefglobalConstruct.java */
package org.jamocha.parser.cool;

import org.jamocha.parser.*;

public class COOLDefglobalConstruct extends ConstructNode {
  public COOLDefglobalConstruct(int id) {
    super(id);
  }

  public COOLDefglobalConstruct(COOLParser p, int id) {
    super(p, id);
  }

	public String toString() {
		return "defglobal \"" + name + "\"" + "(" + doc + ")";
	}

	public JamochaValue execute() throws EvaluationException
	{
		int i;
		if (jjtGetNumChildren()==0) return JamochaValue.FALSE;
		for (i=0;i<jjtGetNumChildren();i++) jjtGetChild(i).execute();		
		return JamochaValue.TRUE;
	};
}
