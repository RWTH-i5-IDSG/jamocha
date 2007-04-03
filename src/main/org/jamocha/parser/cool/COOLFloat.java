/* Generated By:JJTree: Do not edit this line. CLIPS_SLFloat.java */
/** AST Node representing a Float.
 @author jjTree
 @author Ory Chowaw-Liebman
 */
package org.jamocha.parser.cool;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.ValueParam;

public class COOLFloat extends SimpleNode {
    private double value;

    public COOLFloat(int id) {
	super(id);
    }

    public COOLFloat(COOLParser p, int id) {
	super(p, id);
    }

    public void setName(String n) {
	name = n;
	value = Double.parseDouble(n);
    }

    public String toString() {
	return "Float: " + name;
    }

    public Parameter getExpression() {
	return new ValueParam(JamochaValue.newDouble(value));
    }
}
