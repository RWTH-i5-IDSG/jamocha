/* Generated By:JJTree: Do not edit this line. SFPLine.java */

package org.jamocha.parser.sfp;

public class SFPLine extends SimpleNode {
  public SFPLine(int id) {
    super(id);
  }

  public SFPLine(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
