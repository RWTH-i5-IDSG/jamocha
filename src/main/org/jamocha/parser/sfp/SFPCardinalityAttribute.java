/* Generated By:JJTree: Do not edit this line. SFPCardinalityAttribute.java */

package org.jamocha.parser.sfp;

public class SFPCardinalityAttribute extends SimpleNode {
  public SFPCardinalityAttribute(int id) {
    super(id);
  }

  public SFPCardinalityAttribute(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
