/* Generated By:JJTree: Do not edit this line. SFPTADay.java */

package org.jamocha.languages.clips.parser;

public class SFPTADay extends SimpleNode {
  public SFPTADay(int id) {
    super(id);
  }

  public SFPTADay(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
