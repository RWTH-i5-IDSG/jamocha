/* Generated By:JJTree: Do not edit this line. SLActionOp.java */

package org.jamocha.languages.sl.parser;

public class SLActionOp extends SimpleNode {
  public SLActionOp(int id) {
    super(id);
  }

  public SLActionOp(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}