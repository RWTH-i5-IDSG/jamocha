/* Generated By:JJTree: Do not edit this line. SLWff.java */

package org.jamocha.parser.slp;

public class SLWff extends SimpleNode {
  public SLWff(int id) {
    super(id);
  }

  public SLWff(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
