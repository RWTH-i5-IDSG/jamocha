/* Generated By:JJTree: Do not edit this line. SLQuantifier.java */

package org.jamocha.languages.sl.parser;

public class SLQuantifier extends SimpleNode {
  public SLQuantifier(int id) {
    super(id);
  }

  public SLQuantifier(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}