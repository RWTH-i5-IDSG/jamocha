/* Generated By:JJTree: Do not edit this line. SFPAmpersand.java */

package org.jamocha.parser.sfp;

public class SFPAmpersand extends SimpleNode {
  public SFPAmpersand(int id) {
    super(id);
  }

  public SFPAmpersand(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
