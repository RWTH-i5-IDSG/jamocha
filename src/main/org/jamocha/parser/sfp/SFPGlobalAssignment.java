/* Generated By:JJTree: Do not edit this line. SFPGlobalAssignment.java */

package org.jamocha.parser.sfp;

public class SFPGlobalAssignment extends SimpleNode {
  public SFPGlobalAssignment(int id) {
    super(id);
  }

  public SFPGlobalAssignment(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
