/* Generated By:JJTree: Do not edit this line. SFPDefruleConstruct.java */

package org.jamocha.languages.clips.parser;

public class SFPDefruleConstruct extends SimpleNode {
  public SFPDefruleConstruct(int id) {
    super(id);
  }

  public SFPDefruleConstruct(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}