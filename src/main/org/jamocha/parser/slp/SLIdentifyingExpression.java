/* Generated By:JJTree: Do not edit this line. SLIdentifyingExpression.java */

package org.jamocha.parser.slp;

public class SLIdentifyingExpression extends SimpleNode {
  public SLIdentifyingExpression(int id) {
    super(id);
  }

  public SLIdentifyingExpression(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
