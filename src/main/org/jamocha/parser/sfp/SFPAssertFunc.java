/* Generated By:JJTree: Do not edit this line. SFPAssertFunc.java */

package org.jamocha.parser.sfp;

public class SFPAssertFunc extends SimpleNode {
  public SFPAssertFunc(int id) {
    super(id);
  }

  public SFPAssertFunc(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
