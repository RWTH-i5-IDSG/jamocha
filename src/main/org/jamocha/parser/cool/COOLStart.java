/* Generated By:JJTree: Do not edit this line. CLIPS_SLStart.java */
package org.jamocha.parser.cool;

import org.jamocha.rete.Parameter;

public class COOLStart extends SimpleNode {
    public COOLStart(int id) {
	super(id);
    }

    public COOLStart(COOLParser p, int id) {
	super(p, id);
    }

    public Parameter getExpression() {
	if (jjtGetNumChildren() == 0)
	    return null;
	return jjtGetChild(0).getExpression();
    };

}
