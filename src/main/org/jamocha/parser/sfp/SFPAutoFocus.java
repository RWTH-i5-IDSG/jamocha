/* Generated By:JJTree: Do not edit this line. SFPAutoFocus.java */

package org.jamocha.parser.sfp;

public class SFPAutoFocus extends SimpleNode {
  public SFPAutoFocus(int id) {
    super(id);
  }

  public SFPAutoFocus(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
