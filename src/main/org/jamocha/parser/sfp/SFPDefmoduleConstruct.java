/* Generated By:JJTree: Do not edit this line. SFPDefmoduleConstruct.java */

package org.jamocha.parser.sfp;

public class SFPDefmoduleConstruct extends SimpleNode {
  public SFPDefmoduleConstruct(int id) {
    super(id);
  }

  public SFPDefmoduleConstruct(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
