/* Generated By:JJTree: Do not edit this line. SFPMultiSlotDefinition.java */

package org.jamocha.languages.clips.parser;

public class SFPMultiSlotDefinition extends SimpleNode {
  public SFPMultiSlotDefinition(int id) {
    super(id);
  }

  public SFPMultiSlotDefinition(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}