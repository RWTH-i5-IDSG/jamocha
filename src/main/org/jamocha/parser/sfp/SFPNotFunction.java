/* Generated By:JJTree: Do not edit this line. SFPNotFunction.java */

package org.jamocha.parser.sfp;

public class SFPNotFunction extends SimpleNode {
  public SFPNotFunction(int id) {
    super(id);
  }

  public SFPNotFunction(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
