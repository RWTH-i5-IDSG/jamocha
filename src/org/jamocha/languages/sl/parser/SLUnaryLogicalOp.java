/* Generated By:JJTree: Do not edit this line. SLUnaryLogicalOp.java */

package org.jamocha.languages.sl.parser;

public class SLUnaryLogicalOp extends SimpleNode {
  public SLUnaryLogicalOp(int id) {
    super(id);
  }

  public SLUnaryLogicalOp(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}