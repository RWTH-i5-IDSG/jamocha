/* Generated By:JJTree: Do not edit this line. SFPAttributeConstraint.java */

package org.jamocha.parser.sfp;

public class SFPAttributeConstraint extends SimpleNode {
  public SFPAttributeConstraint(int id) {
    super(id);
  }

  public SFPAttributeConstraint(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
