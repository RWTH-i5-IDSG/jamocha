/* Generated By:JJTree: Do not edit this line. SFPDoubleType.java */

package org.jamocha.parser.sfp;

public class SFPDoubleType extends SimpleNode {
  public SFPDoubleType(int id) {
    super(id);
  }

  public SFPDoubleType(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
