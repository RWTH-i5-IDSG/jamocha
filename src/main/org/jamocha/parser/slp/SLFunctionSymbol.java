/* Generated By:JJTree: Do not edit this line. SLFunctionSymbol.java */

package org.jamocha.parser.slp;

public class SLFunctionSymbol extends SimpleNode {
  public SLFunctionSymbol(int id) {
    super(id);
  }

  public SLFunctionSymbol(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
