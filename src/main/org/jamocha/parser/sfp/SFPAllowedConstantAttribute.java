/* Generated By:JJTree: Do not edit this line. SFPAllowedConstantAttribute.java */

package org.jamocha.parser.sfp;

public class SFPAllowedConstantAttribute extends SimpleNode {
  public SFPAllowedConstantAttribute(int id) {
    super(id);
  }

  public SFPAllowedConstantAttribute(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
