/* Generated By:JJTree: Do not edit this line. SLFunctionalTermWithTermOrIE.java */

package org.jamocha.parser.slp;

public class SLFunctionalTermWithTermOrIE extends SimpleNode {
  public SLFunctionalTermWithTermOrIE(int id) {
    super(id);
  }

  public SLFunctionalTermWithTermOrIE(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
