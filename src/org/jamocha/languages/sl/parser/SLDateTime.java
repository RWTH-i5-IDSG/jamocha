/* Generated By:JJTree: Do not edit this line. SLDateTime.java */

package org.jamocha.languages.sl.parser;

public class SLDateTime extends SimpleNode {
  public SLDateTime(int id) {
    super(id);
  }

  public SLDateTime(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}