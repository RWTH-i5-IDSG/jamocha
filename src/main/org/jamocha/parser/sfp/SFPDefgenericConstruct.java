/* Generated By:JJTree: Do not edit this line. SFPDefgenericConstruct.java */

package org.jamocha.parser.sfp;

public class SFPDefgenericConstruct extends SimpleNode {
  public SFPDefgenericConstruct(int id) {
    super(id);
  }

  public SFPDefgenericConstruct(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
