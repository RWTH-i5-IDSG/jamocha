/* Generated By:JJTree: Do not edit this line. SFPLoopForCountFunc.java */

package org.jamocha.parser.sfp;

public class SFPLoopForCountFunc extends SimpleNode {
  public SFPLoopForCountFunc(int id) {
    super(id);
  }

  public SFPLoopForCountFunc(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
